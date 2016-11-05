import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

//  gnuplot -c "C:/Users/Eirikpc/Documents/graph.dat"
//  gnuplot -c "C:/Users/Eivind/Documents/graph.dat"

public class SelfOrganizingMap {
    private boolean stepByStep, printDistPerWrite;
    private int numOfNodes, numOfCities, nodesPerCity, min_x, max_x, min_y, max_y,
            iterationsPerWrite, learningDecayType, radiusDecayType;
    private final int STATIC = 0, LIN = 1, EXP = 2;
    private double radius, radiusDecay, initLearningRate, learningRate, learningDecay,
            plotIterationDelay, graphMargin, radiusFactor;
    private long maxRunTime, startTime, endTime;
    private double[][] nodeWeights, cityCoords;
    private String area;
    private Random random;

    private SelfOrganizingMap() {

        // ---- SETTINGS ---------------------------------
        area = "uy734";

        initLearningRate = 0.1;     // 0.1
        learningDecay = 0.001;      // 0.001
        learningDecayType = EXP;

        radiusDecay = 0.01;         // 0.01    Trade-off between efficiency and accuracy (the higher the faster)
        radiusDecayType = EXP;
        radiusFactor = 1/2.0;       // 1/2.0

        nodesPerCity = 10;           // 2       Trade-off (obviously)
        maxRunTime = 200;
        printDistPerWrite = false;
        stepByStep = false;
        iterationsPerWrite = 1;
        plotIterationDelay = 0.1;
        graphMargin = 0.1;
        // -----------------------------------------------

        learningRate = initLearningRate;
        min_x = Integer.MAX_VALUE;
        max_x = 0;
        min_y = Integer.MAX_VALUE;
        max_y = 0;
        random = new Random();
        cityCoords = readCityCoords(area);
        scaleMaxValues(graphMargin);
        writeGraphConfig(min_x, max_x, min_y, max_y);
        numOfCities = cityCoords.length;
        numOfNodes = numOfCities*nodesPerCity;
        radius = numOfNodes*radiusFactor;
        nodeWeights = genRandomNodes();
        writeCityCoordsToFile();
        writeWeightsToFile(); // Shows initial random weights
    }

    private void runAlgorithm() throws InterruptedException {

        Scanner reader = new Scanner(System.in);
        startTime = System.currentTimeMillis();
        int epoch = 0;
        if(stepByStep) reader.nextLine();

        while (endTime - startTime < maxRunTime * 1000){
            for (int city = 0; city < numOfCities; city++) {
                int bmu = findBmu(city);
                updateNeighbours(bmu, city);
            }
            endTime = System.currentTimeMillis();
            updateRadius();
            updateLearningRate();
            epoch++;
            System.out.println("Epoch " + epoch);

            if(epoch % iterationsPerWrite == 0){
                if (!writeWeightsToFile()) break;
                if (printDistPerWrite) {
                    System.out.println("Total node chain distance: " + getTotalNodeEuclDistance());
                    System.out.println("Total ordered city distance: " + getTotalOrderedCityEuclDistance() + "\n");
                }
                if(stepByStep) reader.nextLine();
            }
        }
//        nodeWeights = readWeights(); //Used to read weights from file
        System.out.println("\nArea................." + area.replace("alt/", ""));
        System.out.println("Init learning rate..." + initLearningRate);
        System.out.println("Learning decay type.." + translateDecayType(learningDecayType));
        System.out.println("Learning decay......." + learningDecay);
        System.out.println("Radius factor........" + radiusFactor);
        System.out.println("Radius decay type...." + translateDecayType(learningDecayType));
        System.out.println("\nNodes per city......." + nodesPerCity);
        System.out.println("Run run time........." + (endTime - startTime) + " ms");
        System.out.println("Total epochs........." + epoch);
        System.out.println("\nTotal node chain distance....." + getTotalNodeEuclDistance());
        System.out.println("Total ordered city distance..." + getTotalOrderedCityEuclDistance());
    }

    private void updateLearningRate() {
        if (learningDecayType == EXP) learningRate *= 1-learningDecay;
        else if (learningDecayType == LIN) {
            double newLearningRate = learningRate - learningDecay;
            if (newLearningRate > 0) learningRate = newLearningRate;
            else learningRate = 0;
        }
    }

    private void updateRadius() {
        if (radiusDecayType == EXP) radius *= 1-radiusDecay;
        else if (radiusDecayType == LIN) {
            double newRadius = radius - radiusDecay;
            if (newRadius >= 0.5) radius = newRadius;
            else radius = 0.5;  // 0.5 to avoid dividing by 0. No functional difference.
        }
    }


    private int findBmu(int city) {
        double city_x = cityCoords[city][0];
        double city_y = cityCoords[city][1];
        int bmu = -1;
        double lowest_dist = Double.MAX_VALUE;
        for (int i = 0; i < numOfNodes; i++) {
            double euclDistance = euclDistance(nodeWeights[i][0], nodeWeights[i][1], city_x, city_y);
            if (euclDistance < lowest_dist) {
                bmu = i;
                lowest_dist = euclDistance;
            }
        }
        return bmu;
    }

    private void updateNeighbours(int node, int city) {
        nodeWeights[node] = getNewWeights(node, city, 0);
        for (int i = 1; i <= radius; i++) {
            if ((node+i) > (numOfNodes - 1)) {
                nodeWeights[node+i-numOfNodes] = getNewWeights(node+i-numOfNodes, city, i);
            }
            else nodeWeights[node+i] = getNewWeights(node+i, city, i);
        }
        for (int i = 1; i <= radius; i++) {
            if ((node-i) < 0) {
                nodeWeights[node-i+numOfNodes] = getNewWeights(node-i+numOfNodes, city, i);
            }
            else nodeWeights[node-i] = getNewWeights(node-i, city, i);
        }
    }

    private double[] getNewWeights(int node, int city, int latticeDist) {
        double[] newWeights = new double[2];
        double neighbourValue = neighbourhoodFunction(latticeDist);
        double node_x = nodeWeights[node][0];
        double node_y = nodeWeights[node][1];
        double city_x = cityCoords[city][0];
        double city_y = cityCoords[city][1];
        newWeights[0] = node_x + (neighbourValue * learningRate * (city_x - node_x));
        newWeights[1] = node_y + (neighbourValue * learningRate * (city_y - node_y));
        return newWeights;
    }

    private double neighbourhoodFunction(int latticeDist){
        return 1-(latticeDist/radius);
    }

    private double euclDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

    private double getTotalOrderedCityEuclDistance() {
        ArrayList<ArrayList<Integer>> nodeClassList = new ArrayList<>(numOfNodes);
        for (int i = 0; i < numOfNodes; i++) {
            nodeClassList.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < numOfCities; i++) {
            nodeClassList.get(findBmu(i)).add(i);
        }
        int[] orderedCityArray = new int[numOfCities];
        int index = 0;
        for (int i = 0; i < numOfNodes; i++) {
            ArrayList<Integer> nodeClass = nodeClassList.get(i);
            for (int city : nodeClass) {
                orderedCityArray[index] = city;
                index++;
            }
        }
        double totalEuclDistance =  0;
        for (int i = 0; i < numOfCities-1; i++) {
            int city = orderedCityArray[i];
            int nextCity = orderedCityArray[i+1];
            double x1 = cityCoords[city][0];
            double y1 = cityCoords[city][1];
            double x2 = cityCoords[nextCity][0];
            double y2 = cityCoords[nextCity][1];
            totalEuclDistance += euclDistance(x1, y1, x2, y2);
        }
        int lastCity = orderedCityArray[numOfCities-1];
        int firstCity = orderedCityArray[0];
        double x1 = cityCoords[lastCity][0];
        double y1 = cityCoords[lastCity][1];
        double x2 = cityCoords[firstCity][0];
        double y2 = cityCoords[firstCity][1];
        totalEuclDistance += euclDistance(x1, y1, x2, y2);
        return totalEuclDistance;
    }

    private double getTotalNodeEuclDistance() {
        double totalEuclDistance =  0;
        for (int i = 0; i < numOfNodes-1; i++) {
            double x1 = nodeWeights[i][0];
            double y1 = nodeWeights[i][1];
            double x2 = nodeWeights[i+1][0];
            double y2 = nodeWeights[i+1][1];
            totalEuclDistance += euclDistance(x1, y1, x2, y2);
        }
        double x1 = nodeWeights[numOfNodes-1][0];
        double y1 = nodeWeights[numOfNodes-1][1];
        double x2 = nodeWeights[0][0];
        double y2 = nodeWeights[0][1];
        totalEuclDistance += euclDistance(x1, y1, x2, y2);
        return totalEuclDistance;
    }

    private double[][] genRandomNodes() {
        double[][] random_nodes = new double[numOfNodes][2];
        for (int i = 0; i < numOfNodes; i++) {
            random_nodes[i] = genRandomWeights();
        }
        return random_nodes;
    }

    private double[] genRandomWeights() {
        double[] weights = new double[2];
        weights[0] = this.random.nextFloat() * (max_x - min_x) + min_x;
        weights[1] = this.random.nextFloat() * (max_y - min_y) + min_y;
        return weights;
    }


    private double[][] readCityCoords(String filename) {
        double[][] newCityCoordsArray = new double[1][1];
        ArrayList<double[]> newCityCoords = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + filename + ".tsp"))) {
            String line = br.readLine();
            String name = line.substring(6);
            System.out.println("Reading coords from " + "src/main/resources/" + filename + ".tsp" + " – " + name);
            while (!line.equals("NODE_COORD_SECTION")) {
                line = br.readLine();
            }
            line = br.readLine();
            while (!line.equals("EOF")) {
                String[] splitLine = line.split(" ");
                double x = Double.parseDouble(splitLine[2]);
                double y = Double.parseDouble(splitLine[1]);
                double[] xy_array = {x, y};
                newCityCoords.add(xy_array);
                setMaxValues(x, y);
                line = br.readLine();
            }
            Collections.shuffle(newCityCoords);  // Randomizes city order to avoid left-right bias at start.
            newCityCoordsArray = new double[newCityCoords.size()][];
            for (int i = 0; i < newCityCoords.size(); i++) {
                newCityCoordsArray[i] = newCityCoords.get(i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newCityCoordsArray;
    }

    private void setMaxValues(double x, double y) {
        if (x < this.min_x) this.min_x = (int) x-1;
        if (x > this.max_x) this.max_x = (int) x+1;
        if (y < this.min_y) this.min_y = (int) y-1;
        if (y > this.max_y) this.max_y = (int) y+1;
    }

    private void scaleMaxValues(double percentage) {
        double distance = (max_x - min_x) * percentage;
        min_x -= distance;
        max_x += distance;
        distance = (max_y - min_y) * percentage;
        min_y -= distance;
        max_y += distance;
    }

    private double[][] readWeights() { // Not used in main algorithm
        ArrayList<double[]> newWeights = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("weights.plot"))) {
            String line = br.readLine();
            String name = line.substring(6);
            line = br.readLine();
            while (line != null) {
                String[] splitLine = line.split(" ");
                double x = Double.parseDouble(splitLine[0]);
                double y = Double.parseDouble(splitLine[1]);
                double[] xy_array = {x, y};
                newWeights.add(xy_array);
                setMaxValues(x, y);
                line = br.readLine();
            }
            double[][] newWeightsArray = new double[newWeights.size()][];
            for (int i = 0; i < newWeights.size(); i++) {
                newWeightsArray[i] = newWeights.get(i);
            }
            return newWeightsArray;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    private void writeGraphConfig(int min_x,int max_x, int min_y, int max_y){
        String graphDatFilePath = "C:\\Users\\Eivind\\Documents\\graph.dat";
        String dataFilesDir = "C:\\Users\\Eivind\\IdeaProjects\\IT3105_Artificial_Intelligence_Programming\\project3'";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("C:\\Users\\Eirikpc\\Documents\\graph.dat"), "utf-8"))){
//            writer.write("set loadpath 'C:\\Users\\Eirikpc\\NTNU\\IT3105_Artificial_Intelligence_Programming\\project3'\n");
                new FileOutputStream(graphDatFilePath), "utf-8"))){
            writer.write("set loadpath '" + dataFilesDir + "\n");
            writer.write("set xrange ["+min_x+":"+max_x+"]\n");
            writer.write("set yrange ["+min_y+":"+max_y+"]\n");
            writer.write("plot \"weights.plot\" using 1:2 title \"Weights\" with linespoints, \"cities.plot\" using 1:2 title \"Cities\"\n");
            writer.write("pause "+ plotIterationDelay +"\n");
            writer.write("reread\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCityCoordsToFile() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("cities.plot"), "utf-8"))){
            for (int i = 0; i < numOfCities; i++) {
                writer.write(cityCoords[i][0] + " " + cityCoords[i][1] + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeWeightsToFile(){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("weights_temp.plot"), "utf-8"))) {
            for (double[] weight_array: nodeWeights) {
                writer.write(weight_array[0] + " " + weight_array[1] + "\n");
            }
            writer.write(nodeWeights[0][0] + " " + nodeWeights[0][1]);
            writer.close();

            File newWeights = new File("weights_temp.plot");
            File oldWeights = new File("weights.plot");
            if (FileUtils.contentEquals(newWeights, oldWeights)) {
                return false;
            }
            while (!oldWeights.delete() && oldWeights.exists()) {
            }
            while (!newWeights.renameTo(new File("weights.plot"))) {}
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private String translateDecayType(int decayType) {
        if(decayType == 0) return "STATIC";
        else if (decayType == 1) return "LIN";
        else if (decayType == 2) return "EXP";
        return "none";
    }


    public static void main(String[] args) throws InterruptedException {
        SelfOrganizingMap som = new SelfOrganizingMap();
        som.runAlgorithm();
    }

//    public double normalize(double input, double inputHigh, double inputLow, double normalizedHigh, double normalizedLow){
//        return ((input - inputLow) / (inputHigh - inputLow)) * (normalizedHigh - normalizedLow) + normalizedLow;
//    }
}
