package acopso.aco.io;

import acopso.aco.graph.*;
import acopso.common.Utils;

/**
 * Helper class for reading the data set and converting it to a graph.
 */
public class Import {

    /**
     * Read the specified data set and return a graph based on the set.
     * @return          the graph representing the data set
     */
    public static Graph getGraph (double evaporationRate, int alpha, int beta) {

        String dataSetName;
        int startingLine;
        dataSetName = Utils.getReportConfigurationValue("data.set.name");
        startingLine = Integer.parseInt(Utils.getReportConfigurationValue("starting.line"));
        String[] lines = Utils.read(dataSetName).split("\n");

        int numOfCities = Integer.parseInt(Utils.getReportConfigurationValue("number.of.cities"));

        Vertex[] vertices = new Vertex[numOfCities];

        for (int i = startingLine; i < startingLine+numOfCities; i++) {
            String[] line = Utils.removeWhiteSpace(lines[i]).trim().split(" ");
            int x = (int)Double.parseDouble(line[1].trim());
            int y = (int)Double.parseDouble(line[2].trim());
            Vertex v = new Vertex(line[0], x, y);
            vertices[i-startingLine] = v;
        }

        Graph graph = new Graph(evaporationRate, alpha, beta);

        for (int i = 0; i < numOfCities; i++) {
            graph.addVertex(vertices[i]);
        }

        for (Vertex v : graph) {
            for (int i = 0; i < numOfCities; i++) {
                if (vertices[i] != v) {
                    graph.addEdge(v, vertices[i]);
                }
            }
        }
        return graph;
    }
}
