package org.opendc.compute.simulator.scheduler.filters

import org.opendc.compute.simulator.service.HostView

/**
 * A [HostFilter] that filters hosts based on ...
 *
 * @param threshold
 */
public class PriceFilter(private val threshold: Double) {
    public fun test(
        host: HostView,
        avgPrice: Double,
    ): Boolean {
        val hostPrice = host.priceToPerformance

        return avgPrice <= hostPrice*threshold // host price needs to be bigger because it's p2p
    }
}
