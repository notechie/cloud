package com.mycompany.cloudsim.priorityscheduling;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

import java.util.*;

public class PriorityScheduler {

    // Class to extend Cloudlet to include priority
    public static class PriorityCloudlet extends Cloudlet {
        private int priority;

        public PriorityCloudlet(int cloudletId, long length, int pesNumber, long fileSize, long outputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, int priority) {
            super(cloudletId, length, pesNumber, fileSize, outputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    public static void runPriorityScheduling() {
        try {
            // Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Create Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_0");

            // Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Create VMs
            List<Vm> vmlist = new ArrayList<>();
            int mips = 1000;
            int ram = 1024; // VM memory (RAM)
            long bw = 512; // VM bandwidth
            long size = 10000; // Image size (required storage)
            String vmm = "Xen"; // VMM name

            for (int i = 0; i < 5; i++) {
                Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }

            broker.submitVmList(vmlist);
            List<PriorityCloudlet> cloudletList = new ArrayList<>();
            int length = 10000;
            int fileSize = 300;
            int outputSize = 300;

            // Example of setting priority for each cloudlet
            int[] priorities = {1, 5, 3, 2, 4, 5, 3, 2, 1, 4}; // Higher number means higher priority
            for (int i = 0; i < 10; i++) {
                PriorityCloudlet cloudlet = new PriorityCloudlet(i, length, 1, fileSize, outputSize, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull(), priorities[i]);
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            // Sort cloudlets based on priority (descending order)
            cloudletList.sort((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority()));

            // Submit sorted cloudlet list to broker
            broker.submitCloudletList(cloudletList);

            // Start Simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Print results
            List<Cloudlet> resultList = broker.getCloudletReceivedList();
            printCloudletList(resultList);

            System.out.println("Priority Scheduling Finished!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 10000;
        int ram = 8192; // Host memory (RAM)
        long storage = 100000;
        int bw = 10000;
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        for (int i = 0; i < 2; i++) {
            hostList.add(new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList)));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID\tSTATUS\tData center ID\tVM ID\tTime\tStart Time\tFinish Time");

        for (Cloudlet cloudlet : list) {
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.println(cloudlet.getCloudletId() + "\tSUCCESS\t" + cloudlet.getResourceId() + "\t" + cloudlet.getVmId() + "\t" +
                        cloudlet.getActualCPUTime() + "\t" + cloudlet.getExecStartTime() + "\t" + cloudlet.getFinishTime());
            }
        }
    }

}

