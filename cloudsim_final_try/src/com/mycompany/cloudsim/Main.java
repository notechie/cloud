package com.mycompany.cloudsim;

import com.mycompany.cloudsim.fcfs.FCFSScheduler;
import com.mycompany.cloudsim.sjf.SJFScheduler;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the scheduling algorithm to run:");
        System.out.println("1. FCFS (First-Come, First-Served)");
        System.out.println("2. SJF (Shortest Job First)");
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
            default:
                System.out.println("Invalid choice. Please select either 1 or 2.");
        }
    }
}
