import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PSO_TSP {
    private static double[][] distances;
    private static int NUM_CITIES;
    private static int NUM_PARTICLES;
    private static int NUM_ITERATIONS;
    private static double C1;
    private static double C2;
    private static double INERTIA_WEIGHT;
    private static double V_MAX;
    private static int NUM_PRECISION;
    public static int[] globalBestTour;
    private static double globalBestFitness = Double.POSITIVE_INFINITY;

    public static void main(String[] args) {
        new PSOTemplate();
    }

    public static String runAlgorithm(double c1, double c2, double inertiaWeight, int numIterations, int numParticles, int numCities, double vMax, int numPrecision, String filePath) {
        // Initialiser les variables statiques avec les paramètres fournis
        C1 = c1;
        C2 = c2;
        INERTIA_WEIGHT = inertiaWeight;
        NUM_ITERATIONS = numIterations;
        NUM_PARTICLES = numParticles;
        NUM_CITIES = numCities;
        V_MAX = vMax;
        NUM_PRECISION = numPrecision;

        // Lire les distances à partir du fichier spécifié
        distances = readDistancesFromFile(filePath);

        // Appeler executePSO() pour démarrer l'algorithme PSO
        return executePSO();
    }

    private static double[][] readDistancesFromFile(String filePath) {
        double[][] distances = new double[NUM_CITIES][NUM_CITIES];

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < NUM_CITIES) {
                String[] values = line.trim().split(";");
                for (int col = 0; col < values.length && col < NUM_CITIES; col++) {
                    distances[row][col] = Double.parseDouble(values[col]);
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return distances;
    }

    private static String executePSO() {
        Particle[] particles = new Particle[NUM_PARTICLES];
        initializeParticles(particles);

        chronoo chrono = new chronoo();
        chrono.start();

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            for (Particle particle : particles) {
                int[] newTour = updateTour(particle);
                double newFitness = fitness(newTour);
                if (newFitness < particle.getFitness()) {
                    particle.setTour(newTour);
                    particle.setFitness(newFitness);
                    if (newFitness < globalBestFitness) {
                        globalBestTour = newTour.clone();
                        globalBestFitness = newFitness;
                    }
                }
            }
           
        }

        chrono.stop();

        StringBuilder result = new StringBuilder();
        result.append("Meilleur parcours trouvé : ").append(arrayToString(globalBestTour)).append("\n");
        result.append("Fitness du meilleur parcours : ").append(globalBestFitness).append("\n");
        result.append("Temps d'exécution : ").append(chrono.getMilliSec()).append(" ms\n");

        return result.toString();
    }

    private static void initializeParticles(Particle[] particles) {
        Random random = new Random();
        for (int i = 0; i < particles.length; i++) {
            int[] tour = generateRandomTour();
            particles[i] = new Particle(tour, fitness(tour));
            if (particles[i].getFitness() < globalBestFitness) {
                globalBestTour = tour.clone();
                globalBestFitness = particles[i].getFitness();
            }
        }
    }

    private static int[] generateRandomTour() {
        List<Integer> tour = new ArrayList<>();
        tour.add(0); // Ajouter la première ville comme ville de départ
        for (int i = 1; i < NUM_CITIES; i++) {
            tour.add(i);
        }
        Random random = new Random();
        for (int i = 1; i < NUM_CITIES; i++) { // Commencer à i = 1 pour éviter de permuter la première ville
            int index1 = random.nextInt(NUM_CITIES - 1) + 1; // Utiliser NUM_CITIES - 1 pour exclure la première ville
            int index2 = random.nextInt(NUM_CITIES - 1) + 1;
            int temp = tour.get(index1);
            tour.set(index1, tour.get(index2));
            tour.set(index2, temp);
        }
        return tour.stream().mapToInt(Integer::intValue).toArray();
    }


    private static int[] updateTour(Particle particle) {
        int[] currentTour = particle.getTour();
        int[] velocity = particle.getVelocity();
        int[] personalBestTour = particle.getPersonalBestTour();
        int[] globalBestTour = PSO_TSP.globalBestTour.clone(); // Supposons que PSO_TSP contient la référence à la meilleure position globale

        Random random = new Random();
        int[] newTour = new int[currentTour.length];
        for (int i = 1; i < currentTour.length; i++) {
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            velocity[i] = (int) (INERTIA_WEIGHT * velocity[i] +
                    C1 * r1 * (personalBestTour[i] - currentTour[i]) +
                    C2 * r2 * (globalBestTour[i] - currentTour[i]));
            if (velocity[i] > V_MAX) {
                velocity[i] = (int) V_MAX;
            } else if (velocity[i] < -V_MAX) {
                velocity[i] = (int) -V_MAX;
            }
            // Mise à jour de la position en fonction de la vitesse
            newTour[i] = currentTour[i] + velocity[i];
            // S'assurer que la nouvelle position reste dans les limites du parcours
            if (newTour[i] < 0) {
                newTour[i] = 0;
            } else if (newTour[i] >= NUM_CITIES) {
                newTour[i] = NUM_CITIES - 1;
            }
        }

        return newTour;
    }

 
    private static double fitness(int[] tour) {
        double totalDistance = 0;
        for (int i = 0; i < NUM_CITIES - 1; i++) {
            totalDistance += distances[tour[i]][tour[i + 1]];
        }
        totalDistance += distances[tour[NUM_CITIES - 1]][tour[0]]; // Distance from last city back to first city
        return totalDistance;
    }

    private static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    static class chronoo {
        private long m_start;
        private long m_stop;

        public chronoo() {
        }

        //Lancer le chronometre
        public void start() {
            m_start = System.currentTimeMillis();
        }

        //Arreter le chronometre
        public void stop() {
            m_stop = System.currentTimeMillis();
        }

        //Retourner le nombre de millisecondes separant l'appel des methode start() et stop()
        public long getMilliSec() {
            return m_stop - m_start;
        }
    }
}

class Particle {
    private int[] tour;
    private int[] velocity;
    private double fitness;
    private int[] personalBestTour;
    private double personalBestFitness;

    public Particle(int[] tour, double fitness) {
        this.tour = tour;
        this.fitness = fitness;
        this.velocity = new int[tour.length];
        this.personalBestTour = tour.clone();
        this.personalBestFitness = fitness;
    }

    public int[] getTour() {
        return tour;
    }

    public void setTour(int[] tour) {
        this.tour = tour;
    }

    public int[] getVelocity() {
        return velocity;
    }

    public void setVelocity(int[] velocity) {
        this.velocity = velocity;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int[] getPersonalBestTour() {
        return personalBestTour;
    }

    public void setPersonalBestTour(int[] personalBestTour) {
        this.personalBestTour = personalBestTour;
    }

    public double getPersonalBestFitness() {
        return personalBestFitness;
    }

    public void setPersonalBestFitness(double personalBestFitness) {
        this.personalBestFitness = personalBestFitness;
    }
}