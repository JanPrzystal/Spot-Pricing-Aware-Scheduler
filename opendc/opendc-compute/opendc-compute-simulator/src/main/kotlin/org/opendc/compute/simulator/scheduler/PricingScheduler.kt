package org.opendc.compute.simulator.scheduler

import org.opendc.compute.simulator.scheduler.filters.HostFilter
import org.opendc.compute.simulator.scheduler.filters.PriceFilter
import org.opendc.compute.simulator.scheduler.weights.HostWeigher
import org.opendc.compute.simulator.service.ComputeService
import org.opendc.compute.simulator.service.HostView
import org.opendc.compute.simulator.service.ServiceTask
import java.util.Deque

public class PricingScheduler(
    private val filters: List<HostFilter>,
    private val weighers: List<HostWeigher>,
    private val subsetSize: Int = 8,
    private var threshold: Double,
) : ComputeScheduler {

    private val hosts = mutableListOf<HostView>()

    /**
     * The scheduler assigns a batch of hosts. This list is used to store them for future use by the select method
     */
    private val savedHosts = mutableListOf<HostView>()

    private var average: Double = 0.0
    private var temporaryAverage: Double = 0.0
    private var hostsPicked: Int = 0
    private var previousHostsPicked: Int = 0

    private var checkThreshold = false

    private var currentLimit = subsetSize

    private var priceFilter = PriceFilter(threshold)

    init {
        require(subsetSize >= 1) { "Subset size must be one or greater" }
    }

    override fun addHost(host: HostView) {
        hosts.add(host)
    }

    override fun removeHost(host: HostView) {
        hosts.remove(host)
    }

    private fun normalizeResults(results: List<HostWeigher.Result>, size: Int): DoubleArray {
        val weights = DoubleArray(size)

        for (result in results) {
            val min = result.min
            val range = (result.max - min)

            // Skip result if all weights are the same
            if (range == 0.0) {
                continue
            }

            val multiplier = result.multiplier
            val factor = multiplier / range

            for ((i, weight) in result.weights.withIndex()) {
                weights[i] += factor * (weight - min)
            }
        }

        return weights
    }

    override fun select(pendingTasks: Deque<ComputeService.SchedulingRequest>): HostView? {
        if (savedHosts.isNotEmpty()){
            currentLimit--
            return savedHosts.removeFirst()
        }

        println("scheduling ${pendingTasks.size} tasks")

        var filteredHosts = hosts.toList()
        for (task in pendingTasks) {
            val tmp = filteredHosts.filter { host -> filters.all { filter -> filter.test(host, task.task) } }
            if(tmp.isNotEmpty())
                filteredHosts = tmp
        }

        val subset =
            if (weighers.isNotEmpty()) {
                //TODO weights for every task
                val results = weighers.map { it.getWeights(filteredHosts, pendingTasks.peek().task) }

                val normalized = normalizeResults(results, filteredHosts.size)

                normalized.indices
                    .asSequence()
                    .sortedByDescending { normalized[it] }
                    .map { filteredHosts[it] }
                    .take(subsetSize)
                    .toList()
            } else {
                filteredHosts
            }

        if (subset.isNotEmpty()) {
            println("scheduled ${subset.size} tasks")

            savedHosts.addAll(subset)
            return select(pendingTasks)
        } else
            return null
    }

    override fun canScheduleMore(): Boolean {
        if (savedHosts.isEmpty() && currentLimit==0){
            println("cannot schedule more: limit reached")
            currentLimit = subsetSize

            average = temporaryAverage
            previousHostsPicked = hostsPicked
            checkThreshold = true

            return false
        } else
            return true
    }

    /**
     * Copied from FilterScheduler
     */
    override fun select(task: ServiceTask): HostView? {
        val hosts = hosts
        var filteredHosts = hosts.filter { host -> filters.all { filter -> filter.test(host, task) } }

        if (checkThreshold)
            filteredHosts = filteredHosts.filter { host -> priceFilter.test(host, average/previousHostsPicked) }

        println("selecting for taks $task")

        if (filteredHosts.isEmpty()){
            println("No matching hosts")
            threshold += 0.1
            priceFilter = PriceFilter(threshold)
            return null
        }

        val subset =
            if (weighers.isNotEmpty()) {
                val results = weighers.map { it.getWeights(filteredHosts, task) }
                val weights = normalizeResults(results, filteredHosts.size)

                weights.indices
                    .asSequence()
                    .sortedByDescending { weights[it] }
                    .map { filteredHosts[it] }
                    .take(1)
                    .toList()
            } else {
                filteredHosts
            }

        if(subset.isNotEmpty()) {
            println("picking ${subset[0].host.getName()}")
            currentLimit--
            hostsPicked++

            temporaryAverage += subset[0].priceToPerformance

            println("current p2p average = ${temporaryAverage/hostsPicked}")
            return subset[0]
        }
        else return null
    }
}
