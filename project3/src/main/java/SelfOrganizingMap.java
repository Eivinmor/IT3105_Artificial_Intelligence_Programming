import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.TimeUnit;

//gnuplot -c "C:/Users/Eirikpc/Documents/graph.dat"
//gnuplot -c "C:\Users\Eivind\Documents\graph.dat"

public class SelfOrganizingMap {
    private int numOfNodes, numOfCities, nodesPerCity, numOfIterations, min_x, max_x, min_y, max_y, iterations, iterationsPerWrite;
    private double[][] nodeWeights, cityCoords;
    private Random random;
    private double radius, radiusDecay, learningRate, learningDecay, plotIterationTime, graphMargin;
    private String area;
    private long runTime, startTime, endTime;

    private SelfOrganizingMap() {
        // ---- SETTINGS ---------------------------------
        area = "qa194";
        radiusDecay = 0.01;
        learningRate = 0.1;
        learningDecay = 0.001;
        numOfIterations = 10000;
        runTime = 40;
        nodesPerCity = 10;
        iterationsPerWrite = 10;
        plotIterationTime = 0.1;
        graphMargin = 0.1;
        // -----------------------------------------------
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
        radius = numOfNodes/2;
        nodeWeights = genRandomNodes();
        writeCityCoordsToFile();
    }

    private void runAlgorithm() throws InterruptedException {
//        System.out.println("\nCITY COORDS:");
//        for (int i = 0; i < cityCoords.length; i++) {
//            System.out.println(cityCoords[i][0] + " " + cityCoords[i][1]);
//        }
        startTime = System.currentTimeMillis();
        iterations = 0;

        while (endTime - startTime < runTime * 1000){
            if(iterations % iterationsPerWrite == 0){
                writeWeightsToFile();
            }
            for (int city = 0; city < numOfCities; city++) {
                int bmu = findBmu(city);
                updateNeighbours(bmu, city);
            }
            endTime = System.currentTimeMillis();
            radius *= 1-radiusDecay;
            learningRate *= 1-learningDecay;
            iterations++;
            System.out.println("Iteration: " + iterations);
        }
        System.out.println("\nTotal iterations: " + iterations);
        System.out.println("Total node chain distance: " + getTotalNodeEuclDistance());
        System.out.println("Total ordered city distance: " + getTotalOrderedCityEuclDistance()); //fint navn :^=)
    }

    private void writeWeightsToFile(){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("weights.plot"), "utf-8"))) {
            for (double[] weight_array: nodeWeights) {
                //                    System.out.print(weight_array[0]);
                //                    System.out.println("\t" + weight_array[1]);
                writer.write(weight_array[0] + "\t" + weight_array[1] + "\n");
            }
            writer.write(nodeWeights[0][0] + " " + nodeWeights[0][1]);
        }
        catch (Exception e){}
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

    private double[][] genRandomNodes() {
        double[][] random_nodes = new double[numOfNodes][2];
        for (int i = 0; i < numOfNodes; i++) {
            random_nodes[i] = genRandomWeights();
        }
        return random_nodes;
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
                double x = Double.parseDouble(splitLine[1]);
                double y = Double.parseDouble(splitLine[2]);
                double[] xy_array = {x, y};
                newCityCoords.add(xy_array);
                setMaxValues(x, y);
                line = br.readLine();
            }
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

    private void writeCityCoordsToFile() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("cities.plot"), "utf-8"))){
            for (int i = 0; i < numOfCities; i++) {
                writer.write(cityCoords[i][0] + " " + cityCoords[i][1] + "\n");
            }
        }
        catch (Exception e){ }
    }

    private void writeGraphConfig(int min_x,int max_x, int min_y, int max_y){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("C:\\Users\\Eirikpc\\Documents\\graph.dat"), "utf-8"))){
//            writer.write("set loadpath 'C:\\Users\\Eirikpc\\NTNU\\IT3105_Artificial_Intelligence_Programming\\project3'\n");
                new FileOutputStream("C:\\Users\\Eivind\\Documents\\graph.dat"), "utf-8"))){
            writer.write("set loadpath 'C:\\Users\\Eivind\\Programmering\\IT3105_Artificial_Intelligence_Programming\\project3'\n");
            writer.write("set xrange ["+min_x+":"+max_x+"]\n");
            writer.write("set yrange ["+min_y+":"+max_y+"]\n");
            writer.write("plot \"weights.plot\" using 1:2 title \"Weights\" with linespoints, \"cities.plot\" using 1:2 title \"Cities\"\n");
            writer.write("pause "+ plotIterationTime +"\n");
            writer.write("reread\n");
        }
        catch (Exception e){
        }
    }

    private double[] genRandomWeights() {
        double[] weights = new double[2];
        weights[0] = this.random.nextFloat() * (max_x - min_x) + min_x;
        weights[1] = this.random.nextFloat() * (max_y - min_y) + min_y;
        return weights;
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
            double n1_x = cityCoords[city][0];
            double n1_y = cityCoords[city][1];
            double n2_x = cityCoords[nextCity][0];
            double n2_y = cityCoords[nextCity][1];
            totalEuclDistance += euclDistance(n1_x, n1_y, n2_x, n2_y);
        }
        int lastCity = orderedCityArray[numOfCities-1];
        int firstCity = orderedCityArray[0];
        double n1_x = nodeWeights[lastCity][0];
        double n1_y = nodeWeights[lastCity][1];
        double n2_x = nodeWeights[firstCity][0];
        double n2_y = nodeWeights[firstCity][1];
        totalEuclDistance += euclDistance(n1_x, n1_y, n2_x, n2_y);
        return totalEuclDistance;
    }

    private double getTotalNodeEuclDistance() {
        double totalEuclDistance =  0;
        for (int i = 0; i < numOfNodes-1; i++) {
            double n1_x = nodeWeights[i][0];
            double n1_y = nodeWeights[i][1];
            double n2_x = nodeWeights[i+1][0];
            double n2_y = nodeWeights[i+1][1];
            totalEuclDistance += euclDistance(n1_x, n1_y, n2_x, n2_y);
        }
        double n1_x = nodeWeights[numOfNodes-1][0];
        double n1_y = nodeWeights[numOfNodes-1][1];
        double n2_x = nodeWeights[0][0];
        double n2_y = nodeWeights[0][1];
        totalEuclDistance += euclDistance(n1_x, n1_y, n2_x, n2_y);
        return totalEuclDistance;
    }

    public static void main(String[] args) throws InterruptedException {
        SelfOrganizingMap som = new SelfOrganizingMap();
        som.runAlgorithm();
    }

//    public double normalize(double input, double inputHigh, double inputLow, double normalizedHigh, double normalizedLow){
//        return ((input - inputLow) / (inputHigh - inputLow)) * (normalizedHigh - normalizedLow) + normalizedLow;
//    }
}
