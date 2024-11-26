package org.opendc.trace.formats.price.parquet

import org.apache.hadoop.conf.Configuration
import org.apache.parquet.hadoop.api.InitContext
import org.apache.parquet.hadoop.api.ReadSupport
import org.apache.parquet.io.api.RecordMaterializer
import org.apache.parquet.schema.LogicalTypeAnnotation
import org.apache.parquet.schema.MessageType
import org.apache.parquet.schema.PrimitiveType
import org.apache.parquet.schema.Types
import org.opendc.trace.conv.SPOT_PRICE_TIMESTAMP
import org.opendc.trace.conv.SPOT_PRICE_INSTANCE_TYPE
import org.opendc.trace.conv.SPOT_PRICE_SPOT

/**
 * A [ReadSupport] instance for [PriceFragment] objects.
 *
 * @param projection The projection of the table to read.
 */
internal class PriceReadSupport(private val projection: List<String>?) : ReadSupport<PriceFragment>() {
    /**
     * Mapping of table columns to their Parquet column names.
     */
    private val colMap =
        mapOf(
            SPOT_PRICE_TIMESTAMP to "Time",
            SPOT_PRICE_INSTANCE_TYPE to "InstanceType",
            SPOT_PRICE_SPOT to "SpotPrice",
        )

    override fun init(context: InitContext): ReadContext {
        val projectedSchema =
            if (projection != null) {
                Types.buildMessage()
                    .apply {
                        val fieldByName = READ_SCHEMA.fields.associateBy { it.name }

                        for (col in projection) {
                            val fieldName = colMap[col] ?: continue
                            addField(fieldByName.getValue(fieldName))
                        }
                    }
                    .named(READ_SCHEMA.name)
            } else {
                READ_SCHEMA
            }
        return ReadContext(projectedSchema)
    }

    override fun prepareForRead(
        configuration: Configuration,
        keyValueMetaData: Map<String, String>,
        fileSchema: MessageType,
        readContext: ReadContext,
    ): RecordMaterializer<PriceFragment> = PriceRecordMaterializer(readContext.requestedSchema)

    companion object {
        /**
         * Parquet read schema for the "price" table in the trace.
         */
        @JvmStatic
        val READ_SCHEMA: MessageType =
            Types.buildMessage()
                .addFields(
                    Types
                        .optional(PrimitiveType.PrimitiveTypeName.INT64)
                        .`as`(LogicalTypeAnnotation.timestampType(true, LogicalTypeAnnotation.TimeUnit.MILLIS))
                        .named("Time"),
                    Types
                        .optional(PrimitiveType.PrimitiveTypeName.BINARY)
                        .named("InstanceType"),
                    Types
                        .optional(PrimitiveType.PrimitiveTypeName.DOUBLE)
                        .named("SpotPrice"),
                )
                .named("price_fragment")
    }
}
