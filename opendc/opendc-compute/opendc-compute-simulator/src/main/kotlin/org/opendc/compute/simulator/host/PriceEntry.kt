package org.opendc.compute.simulator.host

public data class PriceEntry (
    public val hostName: String,
    public var startTime: Long,
    public var durationMs: Long,
    public var price: Double
){
    public fun getTotalCost(): Double {
        return price * (durationMs / 60 / 60 / 1000)
    }
}
