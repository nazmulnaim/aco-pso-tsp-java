package acopso.pso;

import acopso.aco.graph.*;
import acopso.aco.io.*;

public class TravelingSalesmanWithPSO {
  PSO pso;
  /**
   * Construct TravelingSalesman For PSO.
   * 
   * @param numberOfSwarm       Number of Swarm to run per generation.
   * @param generations The number of generations to run
   * @param globalWeight      The Weight Factor
   * @param start       The Starting Point
   */
  public TravelingSalesmanWithPSO(int numberOfSwarm, int generations, double globalWeight, int start) {
    pso = new PSO(numberOfSwarm, generations, globalWeight, start);
  }
  public void run () {
    pso.solve();
  }
}
