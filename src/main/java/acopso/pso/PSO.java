package acopso.pso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import acopso.pso.model.MapMatrix;
import acopso.pso.model.SwarmPosition;

public class PSO {

    private int bestNumber;

    private float globalWeight;

    private int generation; // iteration time

    private int numberOfSwarm; // particle num

    private int numberOfCities; // particlepoint num

    private int currentGeneration; // current generation

    private int start; // start point

    private MapMatrix<Integer, Integer, Integer> distanceMatrix; // matrix of distance

    private MapMatrix<Integer, Integer, Integer> numOfParticles; // particle swarm

    private ArrayList<ArrayList<SwarmPosition>> swaplistOfEachParticle; // swap list of each particle

    private MapMatrix<Integer, Integer, Integer> bestSolutionOfEachParticle; // best solution of each
                                                                             // particle among all generations

    private Map<Integer, Integer> evaluationOfBestSolution; // evaluation value of best solution

    private Map<Integer, Integer> globalBestSolution; // global best solution

    private int evaluationOfGlobalBestSolution; // evaluation value of global best solution

    private int bestGeneration; // best generation

    private Map<Integer, Integer> fitness;

    private Random random;

    ExecutorService executorService = Executors.newFixedThreadPool(50);

    public PSO(int cityNum, int g, int s, float globalWeight, int b) {
        this.numberOfCities = cityNum;
        this.generation = g;
        this.numberOfSwarm = s;
        this.globalWeight = globalWeight;
        this.start = b;
    }

    public void init(String filename) throws IOException {
        int[] x;
        int[] y;
        String strbuff;
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader data = new BufferedReader(new InputStreamReader(is));
        distanceMatrix = new MapMatrix<>();
        x = new int[numberOfCities];
        y = new int[numberOfCities];

        distanceMatrix.set(numberOfCities - 1, numberOfCities - 1, 0);

        numOfParticles = new MapMatrix<>();
        fitness = new HashMap<>();

        // individual
        bestSolutionOfEachParticle = new MapMatrix<>();
        evaluationOfBestSolution = new HashMap<>();

        // global
        globalBestSolution = new HashMap<>();
        evaluationOfGlobalBestSolution = Integer.MAX_VALUE;

        bestGeneration = 0;
        currentGeneration = 0;

        random = new Random(System.currentTimeMillis());

        for (int i = 0; i < numberOfCities; i++) {
            strbuff = data.readLine();
            String[] strcol = strbuff.split(" ");
            x[i] = Integer.valueOf(strcol[1]);// x
            y[i] = Integer.valueOf(strcol[2]);// y
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
    }

    void initGroupParticleSwarm() {
        int i, j, k;
        for (k = 0; k < numberOfSwarm; k++) // swarm num
        {
            // start point
            numOfParticles.set(k, 0, start);
            for (i = 1; i < numberOfCities;) // particle num
            {
                numOfParticles.set(k, i, random.nextInt(65535) % numberOfCities);
                for (j = 0; j < i; j++) {
                    if (numOfParticles.get(k, i) == numOfParticles.get(k, j) || numOfParticles.get(k, i) == start) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }

    public Map<Integer, Integer> getGlobalBestSolution() {
        return globalBestSolution;
    }

    public int getEvaluationOfGlobalBestSolution() {
        return evaluationOfGlobalBestSolution;
    }

    void initSwappingListForEachParticle() {
        int ra;
        int ra1;
        int ra2;

        swaplistOfEachParticle = new ArrayList<ArrayList<SwarmPosition>>();

        for (int i = 0; i < numberOfSwarm; i++) {
            ArrayList<SwarmPosition> list = new ArrayList<SwarmPosition>();
            ra = random.nextInt(65535) % numberOfCities;
            for (int j = 0; j < ra; j++) {
                ra1 = random.nextInt(65535) % numberOfCities;
                while (ra1 == 0) {
                    ra1 = random.nextInt(65535) % numberOfCities;
                }
                ra2 = random.nextInt(65535) % numberOfCities;
                while (ra1 == ra2 || ra2 == 0) {
                    ra2 = random.nextInt(65535) % numberOfCities;
                }

                SwarmPosition S = new SwarmPosition(ra1, ra2);
                list.add(S);
            }

            swaplistOfEachParticle.add(list);
        }
    }

    public int evaluateLength(Map<Integer, Integer> chr) {
        int len = 0;
        // point 1, 2, 3...
        for (int i = 1; i < numberOfCities; i++) {
            len += distanceMatrix.get(chr.get(i - 1), chr.get(i));
        }
        len += distanceMatrix.get(chr.get(numberOfCities - 1), chr.get(0));
        return len;
    }

    public void add(Map<Integer, Integer> arr, ArrayList<SwarmPosition> list) {
        int temp = 0;
        SwarmPosition S;
        for (int i = 0; i < list.size(); i++) {
            S = list.get(i);
            temp = arr.get(S.getX());
            arr.put(S.getX(), arr.get(S.getY()));
            arr.put(S.getY(), temp);
        }
    }

    // get swapping list from b to a
    public ArrayList<SwarmPosition> minus(Map<Integer, Integer> a, Map<Integer, Integer> b) {
        Map<Integer, Integer> temp = new HashMap<>();
        temp.putAll(b);
        int index;
        // swapping unit
        SwarmPosition S;
        // swapping list
        ArrayList<SwarmPosition> list = new ArrayList<SwarmPosition>();
        for (int i = 0; i < numberOfCities; i++) {
            if (a.get(i) != temp.get(i)) {
                // find the same index as a[i] in temp[]
                index = findNum(temp, a.get(i));
                // change i and index in temp[]
                changeIndex(temp, i, index);
                // record swapping unit
                S = new SwarmPosition(i, index);
                // save swapping unit
                list.add(S);
            }
        }
        return list;
    }

    public int findNum(Map<Integer, Integer> arr, int num) {
        int index = -1;
        for (int i = 0; i < numberOfCities; i++) {
            if (arr.get(i) == num) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void changeIndex(Map<Integer, Integer> arr, int index1, int index2) {
        int temp = arr.get(index1);
        arr.put(index1, arr.get(index2));
        arr.put(index2, temp);
    }

    // 二维数组拷贝
    public void copyMatrix(MapMatrix<Integer, Integer, Integer> from, MapMatrix<Integer, Integer, Integer> to) {
        for (int i = 0; i < numberOfSwarm; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                to.set(i, j, from.get(i, j));
            }
        }
    }

    // 一维数组拷贝
    public void copyMap(Map<Integer, Integer> from, Map<Integer, Integer> to) {
        for (int i = 0; i < numberOfCities; i++) {
            to.put(i, from.get(i));
        }
    }

    private void particle(int i) {
        ArrayList<SwarmPosition> Vi;
        int len;
        int j;
        float ra;
        float rb;
        ArrayList<SwarmPosition> Vii = new ArrayList<SwarmPosition>();

        // refresh velocity
        // Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
        Vi = swaplistOfEachParticle.get(i);

        // wVi+表示获取Vi中size*w取整个交换序列
        len = (int) (Vi.size() * globalWeight);

        for (j = 0; j < len; j++) {
            Vii.add(Vi.get(j));
        }

        // Pid-Xid
        ArrayList<SwarmPosition> a = minus(bestSolutionOfEachParticle.get(i), numOfParticles.get(i));
        ra = random.nextFloat();

        // ra(Pid-Xid)
        len = (int) (a.size() * ra);

        for (j = 0; j < len; j++) {
            Vii.add(a.get(j));
        }

        // Pgd-Xid
        ArrayList<SwarmPosition> b = minus(globalBestSolution, numOfParticles.get(i));
        rb = random.nextFloat();

        // rb(Pgd-Xid)
        len = (int) (b.size() * rb);

        for (j = 0; j < len; j++) {
            SwarmPosition tt = b.get(j);
            Vii.add(tt);
        }

        // save new Vii
        swaplistOfEachParticle.set(i, Vii);

        // refresh position
        // Xid’=Xid+Vid
        add(numOfParticles.get(i), Vii);
    }

    public void evolution() {
        int i, j, k;
        int len = 0;
        float ra = 0f;

        ArrayList<SwarmPosition> Vi;

        for (currentGeneration = 0; currentGeneration < generation; currentGeneration++) {
            // create concurrent threads to record particles' movement
            ArrayList<Callable<Void>> runnables = new ArrayList<>();
            for (i = 0; i < numberOfSwarm; i++) {
                if (i == bestNumber)
                    continue;

                final int ii = i;
                runnables.add(new Callable<Void>() {
                    @Override
                    public Void call() {
                        particle(ii);
                        return null;
                    }
                });

            }
            try {
                List<Future<Void>> futures = executorService.invokeAll(runnables);
                for (Future<Void> future : futures) {
                    future.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // calculate fitness value of new swarm, get best solution
            for (k = 0; k < numberOfSwarm; k++) {
                fitness.put(k, evaluateLength(numOfParticles.get(k)));
                if (evaluationOfBestSolution.get(k) > fitness.get(k)) {
                    evaluationOfBestSolution.put(k, fitness.get(k));
                    copyMap(numOfParticles.get(k), bestSolutionOfEachParticle.get(k));
                    bestNumber = k;
                }
                if (evaluationOfGlobalBestSolution > evaluationOfBestSolution.get(k)) {
                    System.out.println(
                            "Shortest distance: " + evaluationOfGlobalBestSolution + " Generation: " + bestGeneration);
                    bestGeneration = currentGeneration;
                    evaluationOfGlobalBestSolution = evaluationOfBestSolution.get(k);
                    copyMap(bestSolutionOfEachParticle.get(k), globalBestSolution);
                }
            }
        }
    }

    public void run() {
        int i;
        int k;

        initGroupParticleSwarm();
        initSwappingListForEachParticle();

        // make each particle remember its own best solution
        copyMatrix(numOfParticles, bestSolutionOfEachParticle);

        for (k = 0; k < numberOfSwarm; k++) {
            fitness.put(k, evaluateLength(numOfParticles.get(k)));
            evaluationOfBestSolution.put(k, fitness.get(k));
            if (evaluationOfGlobalBestSolution > evaluationOfBestSolution.get(k)) {
                evaluationOfGlobalBestSolution = evaluationOfBestSolution.get(k);
                copyMap(bestSolutionOfEachParticle.get(k), globalBestSolution);
                bestNumber = k;
            }
        }

        System.out.println("Initial particle swarm...");
        for (k = 0; k < numberOfSwarm; k++) {
            for (i = 0; i < numberOfCities; i++) {
                System.out.print(numOfParticles.get(k, i) + ",");
            }
            System.out.println();
            System.out.println("----" + fitness.get(k));
        }

        evolution();

        System.out.println("Final particle swarm...");
        for (k = 0; k < numberOfSwarm; k++) {
            for (i = 0; i < numberOfCities; i++) {
                System.out.print(numOfParticles.get(k, i) + ",");
            }
            System.out.println();
            System.out.println("----" + fitness.get(k));
        }

        System.out.println("Best generation: ");
        System.out.println(bestGeneration);
        System.out.println("Shortest distance: ");
        System.out.println(evaluationOfGlobalBestSolution);
        System.out.println("Best path: ");
        for (i = 0; i < numberOfCities; i++) {
            System.out.print(globalBestSolution.get(i) + ",");
        }

    }

}
