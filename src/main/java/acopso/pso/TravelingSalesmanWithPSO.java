package acopso.pso;

import acopso.aco.graph.*;
import acopso.aco.io.*;

public class TravelingSalesmanWithPSO {
  private Graph graph;
  private int numOfParticles, generations;

  /**
   * Construct TravelingSalesman For PSO.
   * 
   * @param swarm       Number of Swarm to run per generation.
   * @param generations The number of generations to run
   * @param weight      The Weight Factor
   * @param start       The Starting Point
   */
  public TravelingSalesmanWithPSO(int swarm, int generations, double weight, int start) {
    this.numOfParticles = swarm;
    this.generations = generations;
    // graph = Import.getGraph(evaporation);
  }
}
