package org.opendc.compute.simulator.scheduler.filters

import org.opendc.compute.simulator.service.HostView
import org.opendc.compute.simulator.service.ServiceTask


public class DemandFilter() : HostFilter {
    override fun test(
        host: HostView,
        task: ServiceTask,
    ): Boolean {
        val isOnDemand = host.host.getName().contains("demand", true)
        return isOnDemand
    }

    override fun toString(): String = "PriceFilter"
}
