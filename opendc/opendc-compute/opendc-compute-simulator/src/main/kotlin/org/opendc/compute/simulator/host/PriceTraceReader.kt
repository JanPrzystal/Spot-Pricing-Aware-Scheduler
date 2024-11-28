
@file:JvmName("ComputeWorkloadsPrice")
package org.opendc.compute.simulator.host





import java.io.File
import javax.management.InvalidAttributeValueException

/**
 * Construct a workload from a price trace.
 */
public fun getPriceFragments(pathToFile: String?): List<org.opendc.compute.simulator.host.PriceFragment>? {
    if (pathToFile == null) {
        return null
    }

    return getPriceFragments(File(pathToFile))
}

/**
 * Construct a workload from a price trace.
 */
public fun getPriceFragments(file: File): List<org.opendc.compute.simulator.host.PriceFragment> {
    if (!file.exists()) {
        throw InvalidAttributeValueException("The price trace cannot be found")
    }

    return PriceTraceLoader().get(file)
}
