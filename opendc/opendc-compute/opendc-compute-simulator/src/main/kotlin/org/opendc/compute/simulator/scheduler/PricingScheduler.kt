package org.opendc.compute.simulator.scheduler

import org.opendc.compute.simulator.scheduler.filters.HostFilter
import org.opendc.compute.simulator.scheduler.weights.HostWeigher
import org.opendc.compute.simulator.service.ComputeService
import org.opendc.compute.simulator.service.HostView
import org.opendc.compute.simulator.service.ServiceTask
import java.util.Deque
import java.util.Random
import kotlin.math.min

public class PricingScheduler(
    private val filters: List<HostFilter>,
    private val weighers: List<HostWeigher>,
    private val subsetSize: Int = 8,
    private val threshold: Double = 1.0
) : ComputeScheduler {

    private val hosts = mutableListOf<HostView>()

    /**
     * The scheduler assigns a batch of hosts. This list is used to store them for future use by the select method
     */
    private val savedHosts = mutableListOf<HostView>()

    private var average: Double = 0.0

    private var currentLimit = subsetSize

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
            return false
        } else
            return true
    }

    /**
     * Copied from FilterScheduler
     */
    override fun select(task: ServiceTask): HostView? {
        val hosts = hosts
        val filteredHosts = hosts.filter { host -> filters.all { filter -> filter.test(host, task) } }

        println("selecting for taks $task")

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
        }

        // fixme: currently finding no matching hosts can result in an error
        return when (val maxSize = min(subsetSize, subset.size)) {
            0 -> null
            1 -> subset[0]
            else -> subset[Random().nextInt(maxSize)]
        }
    }
}
