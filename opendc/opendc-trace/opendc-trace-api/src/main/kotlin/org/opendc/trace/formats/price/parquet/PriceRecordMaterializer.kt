package org.opendc.trace.formats.price.parquet

import org.apache.parquet.io.api.Converter
import org.apache.parquet.io.api.GroupConverter
import org.apache.parquet.io.api.PrimitiveConverter
import org.apache.parquet.io.api.RecordMaterializer
import org.apache.parquet.schema.MessageType
import java.text.SimpleDateFormat
import java.time.Instant


internal class PriceRecordMaterializer(schema: MessageType) : RecordMaterializer<PriceFragment>() {
    companion object {
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    private var localTimestamp: Instant = Instant.MIN
    private var localInstanceType: String = ""

    private var localSpotPrice: Double = 0.0

    private val root = object : GroupConverter() {
        private val converters = schema.fields.map { type ->
            when (type.name) {
                "Time" -> object : PrimitiveConverter() {
                    override fun addBinary(value: org.apache.parquet.io.api.Binary) {
                        val date = sdf.parse(value.toStringUsingUTF8())
                        localTimestamp = Instant.ofEpochMilli(date.time)
                    }
                }
                "InstanceType" -> object : PrimitiveConverter() {
                    override fun addBinary(value: org.apache.parquet.io.api.Binary) {
                        localInstanceType = value.toStringUsingUTF8()
                    }
                }

                "SpotPrice" -> object : PrimitiveConverter() {
                    override fun addDouble(value: Double) {
                        localSpotPrice = value
                    }
                }
                else -> error("Unknown column $type")
            }
        }

        override fun start() {
            localTimestamp = Instant.MIN
            localInstanceType = ""
            localSpotPrice = 0.0
        }

        override fun end() {}

        override fun getConverter(fieldIndex: Int): Converter = converters[fieldIndex]
    }

    override fun getCurrentRecord(): PriceFragment =
        PriceFragment(
            localTimestamp,
            localInstanceType,
            localSpotPrice
        )

    override fun getRootConverter(): GroupConverter = root
}





































































