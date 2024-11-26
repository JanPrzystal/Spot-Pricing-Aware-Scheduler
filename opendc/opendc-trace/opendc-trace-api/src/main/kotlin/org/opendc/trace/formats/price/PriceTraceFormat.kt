package org.opendc.trace.formats.price

import org.opendc.trace.TableColumn
import org.opendc.trace.TableColumnType
import org.opendc.trace.TableReader
import org.opendc.trace.TableWriter
import org.opendc.trace.conv.SPOT_PRICE_TIMESTAMP
import org.opendc.trace.conv.SPOT_PRICE_INSTANCE_TYPE
import org.opendc.trace.conv.SPOT_PRICE_SPOT
import org.opendc.trace.conv.TABLE_PRICE
import org.opendc.trace.formats.price.parquet.PriceReadSupport
import org.opendc.trace.spi.TableDetails
import org.opendc.trace.spi.TraceFormat
import org.opendc.trace.util.parquet.LocalParquetReader
import java.nio.file.Path

/**
 * A [TraceFormat] implementation for the Spot Price trace.
 */
public class PriceTraceFormat : TraceFormat {
    override val name: String = "spot_price"

    override fun create(path: Path) {
        throw UnsupportedOperationException("Writing not supported for this format")
    }

    override fun getTables(path: Path): List<String> = listOf(TABLE_PRICE)

    override fun getDetails(
        path: Path,
        table: String,
    ): TableDetails {
        return when (table) {
            TABLE_PRICE ->
                TableDetails(
                    listOf(
                        TableColumn(SPOT_PRICE_TIMESTAMP, TableColumnType.Instant),
                        TableColumn(SPOT_PRICE_INSTANCE_TYPE, TableColumnType.String),
                        TableColumn(SPOT_PRICE_SPOT, TableColumnType.Double),
                    ),
                )
            else -> throw IllegalArgumentException("Table $table not supported")
        }
    }

    override fun newReader(
        path: Path,
        table: String,
        projection: List<String>?,
    ): TableReader {
        return when (table) {
            TABLE_PRICE -> {
                val reader = LocalParquetReader(path, PriceReadSupport(projection))
                PriceTableReader(reader)
            }
            else -> throw IllegalArgumentException("Table $table not supported")
        }
    }

    override fun newWriter(
        path: Path,
        table: String,
    ): TableWriter {
        throw UnsupportedOperationException("Writing not supported for this format")
    }
}
