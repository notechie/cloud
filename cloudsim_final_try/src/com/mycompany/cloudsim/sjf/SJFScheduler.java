package com.mycompany.cloudsim.sjf;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class SJFScheduler {
    public static void runSJF() {
        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            Datacenter datacenter = createDatacenter("Datacenter_0");

            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            List<Vm> vmlist = new ArrayList<>();
            int mips = 1000;
            int ram = 1024;
            long bw = 512;
            long size = 10000;
            String vmm = "Xen";

            for (int i = 0; i < 5; i++) {
                Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }

            broker.submitVmList(vmlist);

            List<Cloudlet> cloudletList = new ArrayList<>();
            int[] lengths = {5000, 10000, 2000, 8000, 3000}; // Varying task sizes
            int fileSize = 300;
            int outputSize = 300;

            for (int i = 0; i < 5; i++) {
                Cloudlet cloudlet = new Cloudlet(i, lengths[i], 1, fileSize, outputSize, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            Collections.sort(cloudletList, (c1, c2) -> Long.compare(c1.getCloudletLength(), c2.getCloudletLength()));

            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> resultList = broker.getCloudletReceivedList();
            printCloudletList(resultList);

            System.out.println("SJF Scheduling Finished!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 10000;
        int ram = 8192;
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
