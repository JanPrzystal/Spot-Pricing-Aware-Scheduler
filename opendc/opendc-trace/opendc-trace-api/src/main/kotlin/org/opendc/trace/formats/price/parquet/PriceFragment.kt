package org.opendc.trace.formats.price.parquet


import java.time.Instant

/**
 * A task in the Workflow Trace Format.
 */
internal data class PriceFragment(
    val time: Instant,
    val instanceType: String,
    val spotPrice : Double,
)
