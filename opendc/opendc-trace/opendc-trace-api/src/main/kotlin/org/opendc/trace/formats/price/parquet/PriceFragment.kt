package org.opendc.trace.formats.price.parquet


import java.time.Instant

/**
 * A task in the Workflow Trace Format.
 */
internal data class PriceFragment(
    val startTime: Instant,
    val instanceType: String,
    val spotPrice : Double,
)
