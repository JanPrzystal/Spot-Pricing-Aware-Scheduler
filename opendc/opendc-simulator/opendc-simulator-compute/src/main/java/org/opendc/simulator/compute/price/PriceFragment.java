package org.opendc.simulator.compute.price;

/**
 * An object holding the spot price information during a specific time frame.
 */
public class PriceFragment {
    private long startTime;
    private long endTime;
    private String instanceType;
    private double spotPrice;
    /*
    private String region;
    private String availabilityZone;
    private double sps;
    private double ifMetric;
    private double onDemandPrice;

    private double savings;
    */


    public PriceFragment(
        long startTime,
        long endTime,
        String instanceType,
       /* String region,
        String availabilityZone,
        double sps,
        double ifMetric,
        */
        //double onDemandPrice,
        double spotPrice
        //double savings
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.instanceType = instanceType;
        this.spotPrice = spotPrice;
        /*
        this.region = region;
        this.availabilityZone = availabilityZone;
        this.sps = sps;
        this.ifMetric = ifMetric;
        this.onDemandPrice = onDemandPrice;

        this.savings = savings;*/
    }

    // Getters
    public long getStartTime() {
        return startTime;
    }
    public long getEndTime() {
        return endTime;
    }

    public String getInstanceType() {
        return instanceType;
    }
    public double getSpotPrice() {
        return spotPrice;
    }

/*
    public String getRegion() {
        return region;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public double getSps() {
        return sps;
    }

    public double getIfMetric() {
        return ifMetric;
    }

    public double getOnDemandPrice() {
        return onDemandPrice;
    }


    public double getSavings() {
        return savings;
    }
*/
    // Setters
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setSpotPrice(double spotPrice) {
        this.spotPrice = spotPrice;
    }
/*
    public void setRegion(String region) {
        this.region = region;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public void setSps(double sps) {
        this.sps = sps;
    }

    public void setIfMetric(double ifMetric) {
        this.ifMetric = ifMetric;
    }

    public void setOnDemandPrice(double onDemandPrice) {
        this.onDemandPrice = onDemandPrice;
    }



    public void setSavings(double savings) {
        this.savings = savings;
    }
    */

}
