package org.opendc.compute.simulator.scheduler.weights

import org.opendc.compute.simulator.service.HostView
import org.opendc.compute.simulator.service.ServiceTask

/**
 * A [DemandWeigher] that weighs the hosts based on the available PRICE (price) on the host.
 *
 * @param multiplier Weight multiplier ratio. A positive value will result in the scheduler preferring hosts with more
 * available price, and a negative number will result in the scheduler preferring hosts with less price.
 */
public class DemandWeigher(override val multiplier: Double = 1.0) : HostWeigher {
    override fun getWeight(
        host: HostView,
        task: ServiceTask,
    ): Double {
        val isOnDemand = host.host.getName().contains("demand", true)
        if(isOnDemand) {
            return 100.0
        }
        else return 0.0
    }

    override fun toString(): String = "PriceWeigher"
}
