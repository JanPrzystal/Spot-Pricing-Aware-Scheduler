
@file:JvmName("ComputeWorkloadsPrice")
package org.opendc.compute.price





import org.opendc.simulator.compute.price.PriceFragment
import java.io.File
import javax.management.InvalidAttributeValueException

/**
 * Construct a workload from a price trace.
 */
public fun getPriceFragments(pathToFile: String?): List<PriceFragment>? {
    if (pathToFile == null) {
        return null
    }

    return getPriceFragments(File(pathToFile))
}

/**
 * Construct a workload from a price trace.
 */
public fun getPriceFragments(file: File): List<PriceFragment> {
    if (!file.exists()) {
        throw InvalidAttributeValueException("The price trace cannot be found")
    }

    return PriceTraceLoader().get(file)
}
