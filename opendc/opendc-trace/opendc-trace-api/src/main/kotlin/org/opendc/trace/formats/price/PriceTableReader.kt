

package org.opendc.trace.formats.price

import org.opendc.trace.TableReader
import org.opendc.trace.conv.SPOT_PRICE_TIMESTAMP
import org.opendc.trace.conv.SPOT_PRICE_INSTANCE_TYPE
import org.opendc.trace.conv.SPOT_PRICE_SPOT
import org.opendc.trace.formats.price.parquet.PriceFragment
import org.opendc.trace.util.parquet.LocalParquetReader
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * A [TableReader] implementation for the Price trace format.
 */
internal class PriceTableReader(private val reader: LocalParquetReader<PriceFragment>) : TableReader {
    /**
     * The current record.
     */
    private var record: PriceFragment? = null

    override fun nextRow(): Boolean {
        try {
            val record = reader.read()
            this.record = record

            return record != null
        } catch (e: Throwable) {
            this.record = null
            throw e
        }
    }

    private val colTimestamp = 0
    private val colInstanceType = 1
    private val colSpotPrice = 7

    override fun resolve(name: String): Int {
        return when (name) {
            SPOT_PRICE_TIMESTAMP -> colTimestamp
            SPOT_PRICE_INSTANCE_TYPE -> colInstanceType
            SPOT_PRICE_SPOT -> colSpotPrice
            else -> -1
        }
    }

    override fun isNull(index: Int): Boolean {
        require(index in colTimestamp..colSpotPrice) { "Invalid column index" }
        return false
    }

    override fun getBoolean(index: Int): Boolean {
        throw IllegalArgumentException("Invalid column")
    }

    override fun getInt(index: Int): Int {
        throw IllegalArgumentException("Invalid column")
    }

    override fun getLong(index: Int): Long {
        throw IllegalArgumentException("Invalid column")
    }

    override fun getFloat(index: Int): Float {
        throw IllegalArgumentException("Invalid column")
    }

    override fun getDouble(index: Int): Double {
        val record = checkNotNull(record) { "Reader in invalid state" }
        return when (index) {
            colSpotPrice -> record.spotPrice
            else -> throw IllegalArgumentException("Invalid column")
        }
    }

    override fun getString(index: Int): String {
        val record = checkNotNull(record) { "Reader in invalid state" }
        return when (index) {
            colInstanceType -> record.instanceType
            else -> throw IllegalArgumentException("Invalid column")
        }
    }

    override fun getUUID(index: Int): UUID? {
        throw IllegalArgumentException("Invalid column")
    }

    override fun getInstant(index: Int): Instant {
        val record = checkNotNull(record) { "Reader in invalid state" }
        return when (index) {
            colTimestamp -> record.time
            else -> throw IllegalArgumentException("Invalid column")
        }
    }

    override fun getDuration(index: Int): Duration {
        throw IllegalArgumentException("Invalid column")
    }

    override fun <T> getList(
        index: Int,
        elementType: Class<T>,
    ): List<T>? {
        throw IllegalArgumentException("Invalid column")
    }

    override fun <T> getSet(
        index: Int,
        elementType: Class<T>,
    ): Set<T>? {
        throw IllegalArgumentException("Invalid column")
    }

    override fun <K, V> getMap(
        index: Int,
        keyType: Class<K>,
        valueType: Class<V>,
    ): Map<K, V>? {
        throw IllegalArgumentException("Invalid column")
    }

    override fun close() {
        reader.close()
    }
}
