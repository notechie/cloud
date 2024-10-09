package com.mycompany.cloudsim.SRTF;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.provisioners.*;

public class SRTFScheduler {
    public static void runSRTF() {
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

            for (int i = 0; i < 3; i++) { // Create 3 VMs
                Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }

            broker.submitVmList(vmlist);

            // Create Cloudlets (tasks)
            List<Cloudlet> cloudletList = new ArrayList<>();
            int fileSize = 300;
            int outputSize = 300;

            // Generate Cloudlets with varying lengths
            for (int i = 0; i < 10; i++) {
                int length = 10000 - (i * 1000); // Shorter cloudlets have lower lengths
                Cloudlet cloudlet = new Cloudlet(i, length, 1, fileSize, outputSize,
                        new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            // Sort cloudlets in ascending order based on length for SRTF scheduling
            Collections.sort(cloudletList, new Comparator<Cloudlet>() {
                @Override
                public int compare(Cloudlet c1, Cloudlet c2) {
                    return Long.compare(c1.getCloudletLength(), c2.getCloudletLength());
                }
            });

            broker.submitCloudletList(cloudletList);

            // Start Simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Print results
            List<Cloudlet> resultList = broker.getCloudletReceivedList();
            printCloudletList(resultList);

            System.out.println("Shortest Remaining Time First Scheduling Finished!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 10000;
        int ram = 8192; // Host RAM (8 GB)
        long storage = 100000;
        int bw = 10000;
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        for (int i = 0; i < 2; i++) { // Create 2 hosts
            hostList.add(new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw),
                    storage, peList, new VmSchedulerTimeShared(peList)));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux",
                "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList),
                    new LinkedList<>(), 0);
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
                System.out.println(cloudlet.getCloudletId() + "\tSUCCESS\t" + cloudlet.getResourceId() +
                        "\t" + cloudlet.getVmId() + "\t" + cloudlet.getActualCPUTime() + "\t" +
                        cloudlet.getExecStartTime() + "\t" + cloudlet.getFinishTime());
            }
        }
    }
}
