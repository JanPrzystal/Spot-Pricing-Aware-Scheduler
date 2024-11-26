package org.opendc.compute.price

import org.opendc.simulator.compute.price.PriceFragment
import org.opendc.trace.Trace
import org.opendc.trace.conv.SPOT_PRICE_TIMESTAMP
import org.opendc.trace.conv.SPOT_PRICE_SPOT
import org.opendc.trace.conv.SPOT_PRICE_INSTANCE_TYPE
import org.opendc.trace.conv.TABLE_PRICE
import java.io.File
import java.lang.ref.SoftReference
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * A helper class for loading price traces into memory.
 *
 * @param baseDir The directory containing the traces.
 */
public class PriceTraceLoader {
    /**
     * The cache of workloads.
     */
    private val cache = ConcurrentHashMap<String, SoftReference<List<PriceFragment>>>()

    private val builder = PriceFragmentNewBuilder()

    /**
     * Read the metadata into a workload.
     */
    private fun parsePrice(trace: Trace): List<PriceFragment> {
        val reader = checkNotNull(trace.getTable(TABLE_PRICE)).newReader()

        val startTimeCol = reader.resolve(SPOT_PRICE_TIMESTAMP)
        val priceCol = reader.resolve(SPOT_PRICE_SPOT)
        val instanceTypeCol = reader.resolve(SPOT_PRICE_INSTANCE_TYPE)

        try {
            while (reader.nextRow()) {
                val startTime = reader.getInstant(startTimeCol)!!
                val price = reader.getDouble(priceCol)
                val instanceType = reader.getString(instanceTypeCol)!!

                builder.add(startTime, instanceType, price)
            }

            builder.fixReportTimes()
            return builder.fragments
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            reader.close()
        }
    }

    /**
     * Load the trace with the specified [name] and [format].
     */
    public fun get(pathToFile: File): List<PriceFragment> {
        val trace = Trace.open(pathToFile, "org/opendc/compute/price") //TODO

        return parsePrice(trace)
    }

    /**
     * Clear the workload cache.
     */
    public fun reset() {
        cache.clear()
    }

    /**
     * A builder for a price trace.
     */
    private class PriceFragmentNewBuilder {
        /**
         * The total load of the trace.
         */
        public val fragments: MutableList<PriceFragment> = mutableListOf()

        /**
         * Add a fragment to the trace.
         *
         * @param startTime Timestamp at which the fragment starts (in epoch millis).
         * @param price The price during this fragment
         */
        fun add(
            startTime: Instant,
            instanceType: String,
            price: Double,
        ) {
            fragments.add(
                PriceFragment(
                    startTime.toEpochMilli(),
                    Long.MAX_VALUE,
                    instanceType,
                    price,
                ),
            )
        }

        fun fixReportTimes() {
            fragments.sortBy { it.startTime }

            for (i in 0..fragments.size - 2) {
                fragments[i].endTime = fragments[i + 1].startTime
            }

            fragments[0].startTime = Long.MIN_VALUE
        }
    }
}
