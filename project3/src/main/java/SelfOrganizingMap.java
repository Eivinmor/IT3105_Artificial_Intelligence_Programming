import java.util.Random;
import java.lang.Math;


public class SelfOrganizingMap {

    private int num_of_nodes, num_of_cities, min_x, max_x, min_y, max_y;
    private double[][] node_weights, city_coords;
    private Random random;
    private double radius, radiusDecay, learningRate, learningDecay;


    private SelfOrganizingMap() {

        // ---- SETTINGS ---------------------------------
        this.num_of_cities = 50;
        this.num_of_nodes = 5;
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
        node_weights = genRandomNodes();
        // city_coords has to be collected from file
    }

    private void runAlgorithm() {
        for (double[] weight_array: node_weights) {
            System.out.print(weight_array[0]);
            System.out.println("\t" + weight_array[1]);
        }
        System.out.println(find_bmu(5, 5, node_weights));
    }

    private int find_bmu(double city_x, double city_y, double[][] node_weights) {
        int bmu = -1;
        double lowest_dist = Double.MAX_VALUE;
        for (int i = 0; i < num_of_nodes; i++) {
            double eucl_distance = euclDistance(node_weights[i][0], node_weights[i][1], city_x, city_y);
            if (eucl_distance < lowest_dist) {
                bmu = i;
                lowest_dist = eucl_distance;
            }
        }
        return bmu;
    }

    private double euclDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.exp(x1-x2) + Math.exp(y1-y2));
    }

//    private void update_neighbours(double[][] node_weights) {
//    }
//
//    private double[] updateNodeWeights(double[] weights) {
//    }

    private double[] genRandomWeights() {
        double[] weights = new double[2];
        weights[0] = this.random.nextFloat() * (max_x - min_x) + min_x;
        weights[1] = this.random.nextFloat() * (max_y - min_y) + min_y;
        return weights;
    }

    private double[][] genRandomNodes() {
        double[][] random_nodes = new double[num_of_nodes][2];
        for (int i = 0; i < num_of_nodes; i++) {
            random_nodes[i] = genRandomWeights();
        }
        return random_nodes;
    }

    public static void main(String[] args) {
        SelfOrganizingMap som = new SelfOrganizingMap();
        som.runAlgorithm();

    }


}
