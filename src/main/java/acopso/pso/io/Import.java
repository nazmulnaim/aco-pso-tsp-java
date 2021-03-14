package acopso.pso.io;

import acopso.common.Utils;
import acopso.pso.model.MapMatrix;

public class Import {
    /**
     * Read the specified data set and return a matrix based on the set.
     * 
     * @return the matrix representing the data set as map
     */
    public static MapMatrix<Integer, Integer, Integer> getMapMatrix() {
        String dataSetName;
        int startingLine;
        dataSetName = Utils.getReportConfigurationValue("data.set.name");
        startingLine = Integer.parseInt(Utils.getReportConfigurationValue("starting.line"));
        int numberOfCities = Integer.parseInt(Utils.getReportConfigurationValue("number.of.cities"));
        String[] lines = Utils.read(dataSetName).split("\n");

        MapMatrix<Integer, Integer, Integer> distanceMatrix = new MapMatrix<>();

        distanceMatrix.set(numberOfCities - 1, numberOfCities - 1, 0);

        int[] x;
        int[] y;

        x = new int[numberOfCities];
        y = new int[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {
            String[] line = Utils.removeWhiteSpace(lines[i+startingLine]).trim().split(" ");
            x[i] = (int)Double.parseDouble(line[1].trim());
            y[i] = (int)Double.parseDouble(line[2].trim());
        }

        // calculate distance between points
        for (int i = 0; i < numberOfCities - 1; i++) {
            distanceMatrix.set(i, i, 0);
            for (int j = i + 1; j < numberOfCities; j++) {
                double rij = Math.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j])) / 10.0);
                int tij = (int) Math.round(rij);
                if (tij < rij) {
                    distanceMatrix.set(i, j, tij + 1);
                    distanceMatrix.set(j, i, distanceMatrix.get(i, j));
                } else {
                    distanceMatrix.set(i, j, tij);
                    distanceMatrix.set(j, i, distanceMatrix.get(i, j));
                }
            }
        }

        /* System.out.println("NILOG");
        for (int i = 0; i < numberOfCities - 1; i++) {
            for (int j = i + 1; j < numberOfCities; j++) {
                System.out.print(distanceMatrix.get(i, j) + " ");
            }
        }
        System.out.println("NILOG"); */

        return distanceMatrix;
    }
}
