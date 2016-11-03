import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


public class SelfOrganizingMap {

    private int numOfNodes, numOfCities, numOfIterations, min_x, max_x, min_y, max_y;
    private double[][] nodeWeights, cityCoords;
    private Random random;
    private double radius, radiusDecay, learningRate, learningDecay;
    private String area;


    private SelfOrganizingMap() {

        // ---- SETTINGS ---------------------------------
        this.radius = 3;
        this.radiusDecay = 0.0001;
        this.learningRate = 0.2;
        this.learningDecay = 0.0001;
        this.area = "wi29";
        this.numOfIterations = 10000;
        // -----------------------------------------------
        this.min_x = Integer.MAX_VALUE;
        this.max_x = 0;
        this.min_y = Integer.MAX_VALUE;
        this.max_y = 0;
        random = new Random();
        cityCoords = readCityCoords(area);
        numOfCities = cityCoords.length;
        this.numOfNodes = 2*numOfCities;
        nodeWeights = genRandomNodes();
    }

    private void runAlgorithm() {
        System.out.println("\nCITY COORDS:");
        for (int i = 0; i < cityCoords.length; i++) {
            System.out.println(cityCoords[i][0] + " " + cityCoords[i][1]);
        }
//
        System.out.println("\nRANDOM NODES:");
        for (double[] weight_array: nodeWeights) {
            System.out.print(weight_array[0]);
            System.out.println("\t" + weight_array[1]);
        }

        for (int i = 0; i < numOfIterations; i++) {
            for (int city = 0; city < numOfCities; city++) {
                int bmu = findBmu(city);
                updateNeighbours(bmu, city);
            }
        }

        System.out.println("\nUPDATED NODES");
        for (double[] weight_array: nodeWeights) {
            System.out.print(weight_array[0]);
            System.out.println("\t" + weight_array[1]);
        }
    }

    private int findBmu(int city) {
        double city_x = cityCoords[city][0];
        double city_y = cityCoords[city][1];
//        System.out.println("\nFinding BMU for " + city_x + " " + city_y);
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
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

    private void updateNeighbours(int node, int city) {
        nodeWeights[node] = getNewWeights(node, city, 0);
        for (int i = 1; i <= radius; i++) {
            if ((node+i) > (numOfNodes - 1)) {
                nodeWeights[node+i-numOfNodes] = getNewWeights(node, city, i);
            }
            else {
                nodeWeights[node+i] = getNewWeights(node, city, i);
            }
        }
        for (int i = 1; i <= radius; i++) {
            if ((node-i) < 0) {
                nodeWeights[node-i+numOfNodes] = getNewWeights(node, city, i);
            }
            else{
                nodeWeights[node-i] = getNewWeights(node, city, i);
            }
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

    private void setMaxValues(double x, double y) {
        if (x < this.min_x) {
            this.min_x = (int) x-1;
        }
        else if (x > this.max_x) {
            this.max_x = (int) x+1;
        }
        if (y < this.min_y) {
            this.min_y = (int) y-1;
        }
        else if (y > this.max_y) {
            this.max_y = (int) y+1;
        }
    }

    public static void main(String[] args) {
        SelfOrganizingMap som = new SelfOrganizingMap();
        som.runAlgorithm();

    }


    // Old neighbourhood function
//    private double neighbourhoodFunction(double[][] nodeWeights , int node1, int node2){
//        double x1 = nodeWeights[node1][0];
//        double y1 = nodeWeights[node1][1];
//        double x2 = nodeWeights[node2][0];
//        double y2 = nodeWeights[node2][1];
//        double euclDistance = euclDistance(x1, y1, x2, y2);
//        if (euclDistance > radius) {
//            System.out.println(0);
//            return 0;
//        }
//        return 1 - (euclDistance/radius);
//    }

}
