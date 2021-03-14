package acopso;

import acopso.aco.TravelingSalesmanWithACO;
import acopso.common.Utils;
import acopso.pso.TravelingSalesmanWithPSO;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (Utils.getReportConfigurationValue("optimization.method").contains("aco")) {
            aco(args);
        }
        if (Utils.getReportConfigurationValue("optimization.method").contains("pso")) {
            pso(args);
        }
    }

    public static void aco(String[] args) {
        System.out.println("------------------ANT COLONY OPTIMIZATION------------------");
        if (args.length == 1 && args[0].equals("-p")) {
            acoMenu();
        } else {
            System.out.println("Use the parameter '-p' for custom settings.");
            System.out.println("Otherwise the default values will be: ");
            System.out.println("Ants per epoch:           100");
            System.out.println("Epochs:                   100");
            System.out.println("Evaporation Rate:         0.1");
            System.out.println("Alpha (pheromone impact): 1");
            System.out.println("Beta (distance impact):   5");

            int ants = 100; // Number of ants to run per generation.
            int gen = 100; // Number of generations.
            double evap = 0.1; // Evaporation rate of pheromones.
            int alpha = 1; // Impact of pheromones on decision making.
            int beta = 5; // Impact of distance on decision making.

            TravelingSalesmanWithACO travelingSalesman = new TravelingSalesmanWithACO(ants, gen, evap, alpha, beta);
            travelingSalesman.run();
        }
        System.out.println("-------------------------COMPLETE--------------------------");
    }

    private static void acoMenu() {
        TravelingSalesmanWithACO tsp;
        int ants, gen;
        double evap;
        int alpha, beta;

        ants = getUserInt("Ants per epoch:           ");
        gen = getUserInt("Epochs:                   ");
        evap = getUserDouble("Evaporation Rate:         ");
        alpha = getUserInt("Alpha (pheromone impact): ");
        beta = getUserInt("Beta (distance impact):   ");

        tsp = new TravelingSalesmanWithACO(ants, gen, evap, alpha, beta);
        tsp.run();
    }

    public static void pso(String[] args) {
        System.out.println("------------------PARTICLE SWARM OPTIMIZATION------------------");
        if (args.length == 1 && args[0].equals("-p")) {
            psoMenu();
        } else {
            System.out.println("Use the parameter '-p' for custom settings.");
            System.out.println("Otherwise the default values will be: ");
            System.out.println("Swarm Number:           100");
            System.out.println("Iteration Time:         100");
            System.out.println("Weight Factor:          0.5");
            System.out.println("Starting Point:         1");

            int numberOfSwarm = 100; // Number of Swarm to run per generation.
            int generation = 100; // Number of Iteration.
            double weight = 0.5; // Weight Factor.
            int start = 0; // Starting Point.

            TravelingSalesmanWithPSO tsp = new TravelingSalesmanWithPSO(numberOfSwarm, generation, weight, start);
            tsp.run();

        }
        System.out.println("-------------------------COMPLETE--------------------------");
    }

    private static void psoMenu() {
        int numberOfSwarm, generation;
        double weight;
        int start;

        numberOfSwarm = getUserInt("Swarm Number:           ");
        generation = getUserInt("Iteration Time:         ");
        weight = getUserDouble("Weight Factor:          ");
        start = getUserInt("Starting Point:         ");

        TravelingSalesmanWithPSO tsp = new TravelingSalesmanWithPSO(numberOfSwarm, generation, weight, start);
        tsp.run();
    }

    private static double getUserDouble(String msg) {
        double input;
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(msg);

            if (sc.hasNextDouble()) {
                input = sc.nextDouble();

                if (input <= 0) {
                    System.out.println("Number must be positive.");
                } else {
                    break;
                }

            } else {
                System.out.println("Invalid input.");
            }
        }
        return input;
    }

    private static int getUserInt(String msg) {
        int input;
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(msg);

            if (sc.hasNextInt()) {
                input = sc.nextInt();

                if (input <= 0) {
                    System.out.println("Number must be positive.");
                } else {
                    break;
                }

            } else {
                System.out.println("Invalid input.");
            }
        }
        return input;
    }

}
