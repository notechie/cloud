package com.mycompany.cloudsim;

import com.mycompany.cloudsim.SRTF.SRTFScheduler;
import com.mycompany.cloudsim.fcfs.FCFSScheduler;
import com.mycompany.cloudsim.priorityscheduling.PriorityScheduler;
import com.mycompany.cloudsim.sjf.SJFScheduler;
import com.mycompany.cloudsim.roundrobin.RoundRobinScheduler;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the scheduling algorithm to run:");
        System.out.println("1. FCFS (First-Come, First-Served)");
        System.out.println("2. SJF (Shortest Job First)");
        System.out.println("3. Round robin (Round Robin )");
        System.out.println("4. Priority Scheduling");
        System.out.println("5. Shortest Remaining time first Scheduling");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Running FCFS Scheduling...");
                FCFSScheduler.runFCFS();
                break;
            case 2:
                System.out.println("Running SJF Scheduling...");
                SJFScheduler.runSJF();
                break;
            case 3:
                System.out.println("Running Round robin Scheduling...");
                RoundRobinScheduler.runRoundRobin();
                break;
            case 4:
                System.out.println("Running Priority Scheduling...");
                PriorityScheduler.runPriorityScheduling();
                break;
            case 5:
                System.out.println("Running Shortest Remaining Time First...");
                SRTFScheduler.runSRTF();
                break;
            default:
                System.out.println("Invalid choice. Please select one of the above options");
        }
        scanner.close();
    }
}
