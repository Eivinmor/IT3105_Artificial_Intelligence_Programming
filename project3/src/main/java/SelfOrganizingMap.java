import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


public class SelfOrganizingMap {

    private int numOfNodes, numOfCities, min_x, max_x, min_y, max_y;
    private double[][] nodeWeights, cityCoords;
    private Random random;
    private double radius, radiusDecay, learningRate, learningDecay;


    private SelfOrganizingMap() {

        // ---- SETTINGS ---------------------------------
        this.numOfCities = 50;
        this.numOfNodes = 5;
        this.min_x = 2;
        this.max_x = 10;
        this.min_y = 2;
        this.max_y = 10;
        this.radius = 3;
        this.radiusDecay = 0.0001;
        this.learningRate = 0.2;
        this.learningDecay = 0.0001;
        // -----------------------------------------------

        random = new Random();
        nodeWeights = genRandomNodes();
        // cityCoords has to be collected from file
    }

    private void runAlgorithm() {
        for (double[] weight_array: nodeWeights) {
            System.out.print(weight_array[0]);
            System.out.println("\t" + weight_array[1]);
        }
        System.out.println(findBmu(5, 5, nodeWeights));
    }

    private int findBmu(double city_x, double city_y, double[][] nodeWeights) {
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

    private double euclDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.exp(x1-x2) + Math.exp(y1-y2));
    }

//    private void update_neighbours(double[][] nodeWeights) {
//    }

    private double[] getNewWeights(double[][] nodeWeights, int node, int bmu, double city_x, double city_y, double learningRate) {
        double[] newWeights = new double[2];
        double node_x = nodeWeights[node][0];
        double node_y = nodeWeights[node][1];
        double bmu_x = nodeWeights[bmu][0];
        double bmu_y = nodeWeights[bmu][1];
        double neighbourValue = neighbourhoodFunction(node_x, node_y, bmu_x, bmu_y);
        newWeights[0] = node_x + neighbourValue * learningRate * (city_x - node_x);
        newWeights[1] = node_y + neighbourValue * learningRate * (city_y - node_y);
        return newWeights;
    }

    private double neighbourhoodFunction(double x1, double y1, double x2, double y2){
        double neighbourValue = 1 - (euclDistance(x1, y1, x2, y2) / radius);
        return neighbourValue;
    }

    private double[] genRandomWeights() {
        double[] weights = new double[2];
        weights[0] = this.random.nextFloat() * (max_x - min_x) + min_x;
        weights[1] = this.random.nextFloat() * (max_y - min_y) + min_y;
        return weights;
    }

    private double[][] genRandomNodes() {
        double[][] random_nodes = new double[numOfNodes][2];
        for (int i = 0; i < numOfNodes; i++) {
            random_nodes[i] = genRandomWeights();
        }
        return random_nodes;
    }

    private double[][] readCityCoords(String filepath) {
        double[][] newCityCoordsArray = new double[1][1];
        ArrayList<double[]> newCityCoords = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            String name = line.substring(7);
            System.out.println("Reading coords from " + name);
            while (line != "NODE_COORD_SECTION") {
                br.readLine();
            }
            while (line != "EOF") {
                String[] splitLine = line.split(" ");
                double x = Double.parseDouble(splitLine[1]);
                double y = Double.parseDouble(splitLine[2]);
                double[] xy_array = {x, y};
                newCityCoords.add(xy_array);
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

    public static void main(String[] args) {
        SelfOrganizingMap som = new SelfOrganizingMap();
        som.runAlgorithm();

    }



}
