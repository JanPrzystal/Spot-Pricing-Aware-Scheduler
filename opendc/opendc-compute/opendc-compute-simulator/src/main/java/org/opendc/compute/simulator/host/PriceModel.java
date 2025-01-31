package org.opendc.compute.simulator.host;



import java.util.List;
import java.util.Random;


import org.opendc.compute.api.TaskState;
import org.opendc.compute.simulator.internal.Guest;
import org.opendc.compute.simulator.scheduler.ComputeSchedulers;
import org.opendc.compute.simulator.service.ComputeService;
import org.opendc.simulator.compute.workload.SimWorkload;
import org.opendc.simulator.compute.workload.Workload;
import org.opendc.simulator.engine.FlowGraph;
import org.opendc.simulator.engine.FlowNode;

/**
 * PriceModel used to provide the Spot Pricing information for a {@link SimHost}
 * A PriceModel is based on a list of {@link PriceFragment} that define the price at specific time frames.
 */
public class PriceModel extends FlowNode {
    private double totalCost = 0.0; // Total accumulated cost
    private SimHost host; // Reference to the host (if applicable)

    private long startTime = 0L; // The absolute timestamp on which the workload started
    private List<PriceFragment> fragments; // List of price fragments for simulation
    private PriceFragment currentFragment; // Current active price fragment
    private int fragmentIndex; // Index of the current fragment

    private Random rand = new Random();

    /**
     * Construct a PriceModel
     *
     * @param parentGraph The active FlowGraph which should be used to make the new FlowNode
     * @param host The SimHost which may require price updates (can be null if not applicable)
     * @param priceFragments A list of Price Fragments defining the price at different time frames
     * @param startTime The start time of the simulation. This is used to convert relative time to absolute time.
     */
    public PriceModel(
        FlowGraph parentGraph, SimHost host, List<PriceFragment> priceFragments, long startTime) {
        super(parentGraph);

        this.host = host;
        this.startTime = startTime;
        this.fragments = priceFragments;
        this.fragmentIndex = 0;
        this.currentFragment = this.fragments.get(this.fragmentIndex);
    }

    public void close() {
        this.closeNode();
    }

    /**
     * Convert the given relative time to the absolute time by adding the start of workload
     */
    private long getAbsoluteTime(long time) {
        return time + startTime;
    }

    /**
     * Convert the given absolute time to the relative time by subtracting the start of workload
     */
    private long getRelativeTime(long time) {
        return time - startTime;
    }

    /**
     * Traverse the fragments to find the fragment that matches the given absoluteTime
     */
    private void findCorrectFragment(long absoluteTime) {
        // Traverse to the previous fragment, until you reach the correct fragment
        while (absoluteTime < this.currentFragment.getStartTime()) {
            this.currentFragment = fragments.get(--this.fragmentIndex);
        }

        // Traverse to the next fragment, until you reach the correct fragment
        while (absoluteTime >= this.currentFragment.getEndTime()) {
            try {
                this.currentFragment = fragments.get(++this.fragmentIndex);
            } catch (IndexOutOfBoundsException e) {
                // force update of price by random readjustment
                // prevent indefinite sim updates by only updating if there are tasks left
                if (!ComputeService.instance.getTasks().isEmpty())
                    this.currentFragment = new PriceFragment(
                        currentFragment.getEndTime(),
                        currentFragment.getEndTime() + 1000 * 60 * 60,
                        currentFragment.getInstanceType(),
                        rand.nextInt(0, 100) > 90 ?
                            currentFragment.getSpotPrice() + (rand.nextGaussian() / 200) :
                            currentFragment.getSpotPrice()
                    );
                else throw e;
            }
        }
    }
//TODO
    @Override
    public long onUpdate(long now) {
        long absoluteTime = getAbsoluteTime(now);

        // Check if the current fragment is still the correct fragment
        if ((absoluteTime < currentFragment.getStartTime()) || (absoluteTime >= currentFragment.getEndTime())) {
            this.findCorrectFragment(absoluteTime);
        }
//
//        // Calculate cost for the duration of the current fragment
//        long duration = Math.min(currentFragment.getEndTime(), absoluteTime) - Math.max(currentFragment.getStartTime(), getAbsoluteTime(lastUpdateTime()));
//        if (duration > 0) {
//            this.totalCost += currentFragment.getPricePerUnit() * duration;
//        }

        pushPriceValue(currentFragment.getSpotPrice(), absoluteTime);

        // Update again at the end of this fragment
        return getRelativeTime(currentFragment.getEndTime());
    }

    /**
     * Get the total accumulated cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    private void pushPriceValue(double price, long absoluteTime) {
        //TODO 1 put there the ondemand price
//        try {
            if (Math.random() < -0.0005) {
                for (Guest guest : this.host.getGuests()) {
                    if (guest.getVirtualMachine() != null && guest.getState() == TaskState.RUNNING) {
                        SimWorkload simWorkload = guest.getVirtualMachine().getActiveWorkload();
                        if (simWorkload != null) {
                            simWorkload.makeSnapshot(absoluteTime);
                            Workload snapshot = simWorkload.getSnapshot();
                            //guest.getTask().workload = simWorkload.getSnapshot();
                            //guest.fail();  // This triggers rescheduling through the normal flow
                            //guest.getTask().start();
                            if (snapshot != null) {
                                guest.getTask().setWorkload(snapshot); // Assign the snapshot to the tas
                            }
                        }
                    }
                }
                this.host.fail();
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        this.host.setPrice(price);
    }
}
