**Spot Pricing-Aware Scheduler**
This project is based on the OpenDC platform for datacenter simulation (https://github.com/atlarge-research/opendc.git)

This repository implements a spot pricing-aware scheduler which is meant to minimize the financial cost of computing a workload on cloud instances. The scheduler leverages the pricing data and the hosts' capabilities to reduce the total price needed to complete the workload.

**Supplemental Material for Scientific Report**

Please note that this repository contains the code supporting our scientific report on [Spot Pricing-aware Schedulers].


# Spot Pricing-Aware Scheduling: Project Documentation


## Requirements

1. **Software**:
   - OpenDC Simulator ([OpenDC](https://opendc.org))
   - IntelliJ IDEA ([Download](https://www.jetbrains.com/idea/download/))

2. **Data**: SpotLake datasets and BitBrains workloads for simulations.

---

## Running Simulations
All the schedulers and their parameters with their respective effect on the proce are describe in the report.

1. **Setup**:
   
   - Modify `ComputeSchedulers.kt` for scheduler parameters.
     For price-aware schedulers, you can tune two key parameters that control cost-time trade-offs:

   ```kotlin

   threshold = 3.0    // Price-to-performance threshold multiplier

   subsetSize = 8     // Number of tasks per scheduling cycle

   ```

   

   * threshold (T): Controls host selection based on price-to-performance ratio (ptp)

      After initial period, only selects hosts where ptp does not exceed ptp * T

       Lower values (e.g., 1.1) lead to lower costs but longer execution times

   

   * subsetSize (Nt): Limits tasks scheduled per cycle

     Prevents scheduling all pending tasks at once

     Helpful when limited well-scoring hosts are available


   - Configure `simple_experiment.json` for the scheduler and topology.
     ```json

     {

         "name": "simple",

         "topologies": [{

             "pathToFile": "topologies/aws.json"

         }],

         "workloads": [{

             "pathToFile": "workloads/bitbrains-small",

             "type": "ComputeWorkload"

         }],

         "allocationPolicies": [{

             "policyType": "PriceToPerformance"

         }]

     }

     ```

     Where:

     topologies: Points to AWS infrastructure configuration

      workloads: Specifies BitBrains dataset location

     allocationPolicies: Defines the scheduler type (e.g., PriceToPerformance / Random / Demand / Price)
3. **Execution**:
   - Run OpenDC in IntelliJ IDEA with `ExperimentCli` as the main class.
   - Use `--experiment-path <path-to-experiment-file>`.
   - Results will be visible in the console 
---

## Experiments

1. **Schedulers Tested**: Random, Price-based, Demand, Price-to-Performance (PTP).
2. **Metrics**:
   - **Cost**: Total price of task execution.
   - **Time**: Task completion duration.
3. **Results**: PTP scheduler consistently offers the best cost-efficiency.

---

## Contributing

Fork the repository and submit a pull request. Contact:

* Jan Przystał: j.p.przystal@student.vu.nl
* Luuk van den Beemt: l.d.vanden.beemt@student.vu.nl
* Mihaela Mereuta: m.mereuta@student.vu.nl

--- 
