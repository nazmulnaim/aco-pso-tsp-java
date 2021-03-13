package acopso.pso.io;

import acopso.common.Utils;

public class Import {
    /**
     * Read the specified data set and return a matrix based on the set.
     * 
     * @return the matrix representing the data set
     */
    public static Graph getGraph(double evaporationRate, int alpha, int beta) {

        String dataSetName;
        int startingLine;

        dataSetName = Utils.getReportConfigurationValue("data.set.name");
        startingLine = Integer.parseInt(Utils.getReportConfigurationValue("starting.line"));

        String[] lines = Utils.read(dataSetName).split("\n");

        int numOfCities = Integer.parseInt(Utils.getReportConfigurationValue("number.of.cities"));

        Vertex[] vertices = new Vertex[numOfCities];

        // Read each line and turn it into a Vertex.
        for (int i = startingLine; i < startingLine + numOfCities; i++) {
            String[] line = removeWhiteSpace(lines[i]).trim().split(" ");
            int x = (int) Double.parseDouble(line[1].trim());
            int y = (int) Double.parseDouble(line[2].trim());
            Vertex v = new Vertex(line[0], x, y);
            vertices[i - startingLine] = v;
        }

        Graph graph = new Graph(evaporationRate, alpha, beta);

        // Create the spine of the graph (the vertices).
        for (int i = 0; i < numOfCities; i++) {
            graph.addVertex(vertices[i]);
        }

        // Create the edges of the graph (connect every vertex to each other).
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
