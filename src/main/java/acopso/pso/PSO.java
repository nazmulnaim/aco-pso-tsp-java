package acopso.pso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import acopso.common.Utils;
import acopso.pso.io.Import;
import acopso.pso.model.MapMatrix;
import acopso.pso.model.NextPossiblePosition;

public class PSO {

    private int bestNumber; // Best fitness value
    private double globalWeight; // Weight factor for each particle
    private int generation; // Total iteration time
    private int numberOfParticles; // Total number of particle
    private int numberOfCities; // Total number of cities
    private int currentGeneration; // current iteration number
    private int start; // Starting city
    private MapMatrix<Integer, Integer, Integer> distanceMatrix; // Map matrix of distance
    private MapMatrix<Integer, Integer, Integer> travelingSequenceCitiesOfAllParticles; // All traveling path of the swarm of all particles
    private List<List<NextPossiblePosition>> possibleTravelingPathOfAllParticles; // List of traveling path each particle
    private MapMatrix<Integer, Integer, Integer> bestTravelingPathOfAllParticles; // Best traveling path of each particle among all generations
    private Map<Integer, Integer> evaluationOfBestSolution; // Evaluation value of best traveling path
    private Map<Integer, Integer> globalBestTravelingPath; // Global best traveling path
    private int evaluationOfGlobalBestSolution; // Evaluation value of global best traveling path
    private int bestGeneration; // Generation of the best evaluation
    private Map<Integer, Integer> fitness; // Best fitness value of each particle
    private Random random;


    /**
     * Represents each particle from the particle swarm Optimization algorithm.
     */

    public PSO(int numberOfSwarm, int generation, double globalWeight, int start) {
        this.numberOfParticles = numberOfSwarm;
        this.generation = generation;
        this.globalWeight = globalWeight;
        this.start = start;
        this.numberOfCities = Integer.parseInt(Utils.getReportConfigurationValue("number.of.cities"));
        init();
    }

    /**
     * calculating covered distance for each particle
     * finding global and individual best
     */

    public void init() {
        distanceMatrix = new MapMatrix<>();
        travelingSequenceCitiesOfAllParticles = new MapMatrix<>();
        fitness = new HashMap<>();

        bestTravelingPathOfAllParticles = new MapMatrix<>();
        evaluationOfBestSolution = new HashMap<>();

        globalBestTravelingPath = new HashMap<>();
        evaluationOfGlobalBestSolution = Integer.MAX_VALUE;

        bestGeneration = 0;
        currentGeneration = 0;

        random = new Random(System.currentTimeMillis());
        distanceMatrix = Import.getMapMatrix();
    }

    /**
     * Starting each particles from a point,
     * select the cities the particles can travel and
     * check if the particles have covered all the cities
     */

    void initTravelingCitiesOfParticles() {
        int currentCity, checkingCity, particle;
        for (particle = 0; particle < numberOfParticles; particle++)
        {
            travelingSequenceCitiesOfAllParticles.set(particle, 0, start);
            for (currentCity = 1; currentCity < numberOfCities;)
            {
                int nextCity = random.nextInt(65535) % numberOfCities;
                travelingSequenceCitiesOfAllParticles.set(particle, currentCity, nextCity);
                for (checkingCity = 0; checkingCity < currentCity; checkingCity++) {
                    if (travelingSequenceCitiesOfAllParticles.get(particle, currentCity) == travelingSequenceCitiesOfAllParticles.get(particle, checkingCity) || travelingSequenceCitiesOfAllParticles.get(particle, currentCity) == start) {
                        break;
                    }
                }
                if (checkingCity == currentCity) {
                    currentCity++;
                }
            }
        }
    }

    /**
     * Get the traveling points for each particles and store them in an array.
     */

    void setinitialTravelingPathEachParticle() {
        int numberOfCityToBeVisited;
        int cityA;
        int cityB;

        possibleTravelingPathOfAllParticles = new ArrayList<>();

        for (int i = 0; i < numberOfParticles; i++) {
            List<NextPossiblePosition> list = new ArrayList<>();
            numberOfCityToBeVisited = random.nextInt(65535) % numberOfCities;
            for (int j = 0; j < numberOfCityToBeVisited; j++) {
                cityA = random.nextInt(65535) % numberOfCities;
                while (cityA == 0) {
                    cityA = random.nextInt(65535) % numberOfCities;
                }
                cityB = random.nextInt(65535) % numberOfCities;
                while (cityA == cityB || cityB == 0) {
                    cityB = random.nextInt(65535) % numberOfCities;
                }
                NextPossiblePosition nextPossiblePosition = new NextPossiblePosition(cityA, cityB);
                list.add(nextPossiblePosition);
            }
            possibleTravelingPathOfAllParticles.add(list);
        }
    }

    /**
     * Get the length of total distance of the swarm
     */

    public int evaluateLength(Map<Integer, Integer> chr) {
        int length = 0;
        for (int i = 1; i < numberOfCities; i++) {
            length += distanceMatrix.get(chr.get(i - 1), chr.get(i));
        }
        length += distanceMatrix.get(chr.get(numberOfCities - 1), chr.get(0));
        return length;
    }

    /**
     * Update traveling path of a particle
     */
    public void updateTravelingPath(Map<Integer, Integer> travelingSequenceOfCitiesOfAParticle, List<NextPossiblePosition> updatedTravelingPath) {
        int temp = 0;
        NextPossiblePosition S;
        for (int i = 0; i < updatedTravelingPath.size(); i++) {
            S = updatedTravelingPath.get(i);
            temp = travelingSequenceOfCitiesOfAParticle.get(S.getX());
            travelingSequenceOfCitiesOfAParticle.put(S.getX(), travelingSequenceOfCitiesOfAParticle.get(S.getY()));
            travelingSequenceOfCitiesOfAParticle.put(S.getY(), temp);
        }
    }

    /**
     * Update traveling path of a particle to hole the best route with the newly obtained traveling sequence of cities of the same particle
     */
    public List<NextPossiblePosition> updateTravelingSequenceOfCities(Map<Integer, Integer> bestTravelingPathOfAParticle, Map<Integer, Integer> travelingSequenceCitiesOfAParticle) {
        Map<Integer, Integer> temp = new HashMap<>();
        temp.putAll(travelingSequenceCitiesOfAParticle);
        int index;
        NextPossiblePosition nextPossiblePosition;
        List<NextPossiblePosition> fullTravelingPathOfAParticle = new ArrayList<>();
        for (int city = 0; city < numberOfCities; city++) {
            if (bestTravelingPathOfAParticle.get(city) != temp.get(city)) {
                index = findCityPositionInTheTravelingSequence(temp, bestTravelingPathOfAParticle.get(city));
                changePositionOfTheCityInTheTravelingSequence(temp, city, index);
                nextPossiblePosition = new NextPossiblePosition(city, index);
                fullTravelingPathOfAParticle.add(nextPossiblePosition);
            }
        }
        return fullTravelingPathOfAParticle;
    }

    /**
     * Find city position in the traveling sequence of the cities of a particle
     */
    public int findCityPositionInTheTravelingSequence(Map<Integer, Integer> travelingSequenceCitiesOfAParticle, int city) {
        int index = -1;
        for (int i = 0; i < numberOfCities; i++) {
            if (travelingSequenceCitiesOfAParticle.get(i) == city) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Change position of a city in the traveling sequence of cities a particles 
     */
    public void changePositionOfTheCityInTheTravelingSequence(Map<Integer, Integer> travelingSequenceCitiesOfAParticle, int city, int swapingCity) {
        int temp = travelingSequenceCitiesOfAParticle.get(city);
        travelingSequenceCitiesOfAParticle.put(city, travelingSequenceCitiesOfAParticle.get(swapingCity));
        travelingSequenceCitiesOfAParticle.put(swapingCity, temp);
    }

    /**
     * Utility method to copy map matrix
     */
    public void copyMapMatrix(MapMatrix<Integer, Integer, Integer> from, MapMatrix<Integer, Integer, Integer> to) {
        for (int i = 0; i < numberOfParticles; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                to.set(i, j, from.get(i, j));
            }
        }
    }

    /**
     * Utility method to copy collection (map)
     */
    public void copyMap(Map<Integer, Integer> from, Map<Integer, Integer> to) {
        for (int i = 0; i < numberOfCities; i++) {
            to.put(i, from.get(i));
        }
    }

    /**
     * Calculate total traveling path for a specific particle
     */
    private void calculateMovementOfAParticle(int particle) {
        List<NextPossiblePosition> travelingPath;
        int length;
        float randomFactorA;
        float ramdomFactorB;
        List<NextPossiblePosition> finalTravelingPath = new ArrayList<>();

        travelingPath = possibleTravelingPathOfAllParticles.get(particle);

        // calculating global weight
        length = (int) (travelingPath.size() * globalWeight);

        for (int j = 0; j < length; j++) {
            finalTravelingPath.add(travelingPath.get(j));
        }

        List<NextPossiblePosition> a = updateTravelingSequenceOfCities(bestTravelingPathOfAllParticles.get(particle), travelingSequenceCitiesOfAllParticles.get(particle));
        randomFactorA = random.nextFloat();

        length = (int) (a.size() * randomFactorA);

        for (int j = 0; j < length; j++) {
            finalTravelingPath.add(a.get(j));
        }

        List<NextPossiblePosition> b = updateTravelingSequenceOfCities(globalBestTravelingPath, travelingSequenceCitiesOfAllParticles.get(particle));
        ramdomFactorB = random.nextFloat();

        length = (int) (b.size() * ramdomFactorB);

        for (int j = 0; j < length; j++) {
            NextPossiblePosition tt = b.get(j);
            finalTravelingPath.add(tt);
        }

        possibleTravelingPathOfAllParticles.set(particle, finalTravelingPath);
        updateTravelingPath(travelingSequenceCitiesOfAllParticles.get(particle), finalTravelingPath);
    }


    /**
     * Evaluate the map and the particles traveling route to calculate the best route of each 
     * generation and then pick the best solution
     */
    public void evolution() {
        for (currentGeneration = 0; currentGeneration < generation; currentGeneration++) {
            for (int i = 0; i < numberOfParticles; i++) {
                if (i == bestNumber){
                    continue;
                }
                calculateMovementOfAParticle(i);
            }

            for (int k = 0; k < numberOfParticles; k++) {
                fitness.put(k, evaluateLength(travelingSequenceCitiesOfAllParticles.get(k)));
                if (evaluationOfBestSolution.get(k) > fitness.get(k)) {
                    evaluationOfBestSolution.put(k, fitness.get(k));
                    copyMap(travelingSequenceCitiesOfAllParticles.get(k), bestTravelingPathOfAllParticles.get(k));
                    bestNumber = k;
                }
                if (evaluationOfGlobalBestSolution > evaluationOfBestSolution.get(k)) {
                    bestGeneration = currentGeneration;
                    evaluationOfGlobalBestSolution = evaluationOfBestSolution.get(k);
                    copyMap(bestTravelingPathOfAllParticles.get(k), globalBestTravelingPath);
                }
            }
        }
    }

    /**
     * Run the algorithm.
     */
    public void solve() {

        initTravelingCitiesOfParticles();
        setinitialTravelingPathEachParticle();

        copyMapMatrix(travelingSequenceCitiesOfAllParticles, bestTravelingPathOfAllParticles);

        for (int k = 0; k < numberOfParticles; k++) {
            fitness.put(k, evaluateLength(travelingSequenceCitiesOfAllParticles.get(k)));
            evaluationOfBestSolution.put(k, fitness.get(k));
            if (evaluationOfGlobalBestSolution > evaluationOfBestSolution.get(k)) {
                evaluationOfGlobalBestSolution = evaluationOfBestSolution.get(k);
                copyMap(bestTravelingPathOfAllParticles.get(k), globalBestTravelingPath);
                bestNumber = k;
            }
        }

        evolution();

        System.out.print("Best Tour: ");
        for (int i = 0; i < numberOfCities; i++) {
            if(i==0){
                System.out.print(globalBestTravelingPath.get(i));
            }else{
                System.out.print(" -> " + globalBestTravelingPath.get(i));
            }
        }
        System.out.println();
        System.out.print("Shortest distance: ");
        System.out.println(evaluationOfGlobalBestSolution);
    }
}
