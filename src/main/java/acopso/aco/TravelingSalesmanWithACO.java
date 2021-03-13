package acopso.aco;

import acopso.aco.graph.*;
import acopso.aco.io.*;

/**
 * Solves the Traveling Salesman Problem using vanilla Ant Colony Optimization.
 */
public class TravelingSalesmanWithACO {

    private Graph graph;
    private int numOfAnts, generations;

    /**
     * Construct TravelingSalesman For ACO.
     * @param ants          the number of ants to run per generation
     * @param generations   the number of generations to run
     * @param evaporation   the rate of evaporation
     * @param alpha         the impact of pheromones on decision making
     * @param beta          the impact of distance in decision making
     */
    public TravelingSalesmanWithACO (int ants, int generations, double evaporation, int alpha, int beta) {
        this.numOfAnts = ants;
        this.generations = generations;
        graph = Import.getGraph(evaporation, alpha, beta);
    }

    /**
     * Run the algorithm.
     */
    public void run () {

        ACO bestAnt = null;
        int bestEval = 0;

        delay(1000);

        for (int i = 0; i < generations; i++) {
            ACO[] ants = createAnts(numOfAnts);
            ACO ant = travel(ants);
            updatePheromones(ants);

            if (bestAnt == null) {
                bestAnt = ant;
                bestEval = ant.eval();
            } else if (ant.eval() < bestEval) {
                bestAnt = ant;
                bestEval = ant.eval();
            }
        }

        System.out.print("Best Tour: ");
        System.out.println(bestAnt);
        System.out.println("Evaluation: " + bestEval);
    }

    /**
     * Create ants and put them on random starting positions on the graph.
     * @param quantity  the quantity of ants to create
     * @return          an array of the ants created
     */
    private ACO[] createAnts (int quantity) {
        ACO[] ants = new ACO[quantity];
        for (int i = 0; i < quantity; i++) {
            ants[i] = new ACO(graph);
        }
        return ants;
    }

    /**
     * Let each ant in the input array travel until an entire tour is completed.
     * @param ants      the ants to allow to travel
     * @return          the ant with the best evaluation
     */
    private ACO travel (ACO[] ants) {

        ACO bestAnt = null;
        int bestEval = 0;

        for (ACO ant : ants) {
            while (ant.notFinished()) {
                ant.travel();
            }

            if (bestAnt == null) {
                bestAnt = ant;
                bestEval = ant.eval();
            } else if (ant.eval() < bestEval) {
                bestAnt = ant;
                bestEval = ant.eval();
            }
        }

        return bestAnt;
    }

    /**
     * Update the pheromones in the graph based on an array of ants with
     * completed tours.
     * @param ants  the ants that will be used to update the pheromones
     */
    private void updatePheromones (ACO[] ants) {
        for (ACO ant : ants) {
            graph.updatePheromone(ant);
        }
    }

    /**
     * Sleep the thread for a specified amount of time.
     * @param ms    milliseconds to sleep for
     */
    private static void delay (int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
