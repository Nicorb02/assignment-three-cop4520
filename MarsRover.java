import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class SharedMemory {
    private List<Double> temperatureReadings;

    public SharedMemory() {
        this.temperatureReadings = new ArrayList<>();
    }

    public synchronized void addTemperatureReading(double temperature) {
        temperatureReadings.add(temperature);
    }

    public synchronized List<Double> getTemperatureReadings() {
        return temperatureReadings;
    }
}

class TemperatureSensor implements Runnable {
    private SharedMemory sharedMemory;

    public TemperatureSensor(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            double temperature = random.nextDouble() * 170 - 100;
            sharedMemory.addTemperatureReading(temperature);
            System.out.println("temp reading: " + temperature);
            // temp reading every min
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

class TemperatureModule implements Runnable {
    private SharedMemory sharedMemory;

    public TemperatureModule(SharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    @Override
    public void run() {
        while (true) {
            // wait an hour for the report
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (Exception e) {
                System.out.println(e);
            }
            List<Double> temperatureReadings = sharedMemory.getTemperatureReadings();
            if (!temperatureReadings.isEmpty()) {
                // sort on the hour
                Collections.sort(temperatureReadings);
                List<Double> highestTemperatures = temperatureReadings.subList(temperatureReadings.size() - 5, temperatureReadings.size());
                List<Double> lowestTemperatures = temperatureReadings.subList(0, 5);

                // now find 10 min interval with biggest diff
                double maxDifference = 0;
                int maxDifferenceStartIndex = 0;
                for (int i = 0; i < temperatureReadings.size() - 10; i++) {
                    double difference = temperatureReadings.get(i + 10) - temperatureReadings.get(i);
                    if (difference > maxDifference) {
                        maxDifference = difference;
                        maxDifferenceStartIndex = i;
                    }
                }
                List<Double> maxDifferenceInterval = temperatureReadings.subList(maxDifferenceStartIndex, maxDifferenceStartIndex + 10);

                System.out.println("Top 5 highest temperatures: " + highestTemperatures);
                System.out.println("Top 5 lowest temperatures: " + lowestTemperatures);
                System.out.println("10-minute interval with largest temperature difference: " + maxDifferenceInterval);
            }
        }
    }
}

public class MarsRover {
    public static void main(String[] args) {
        SharedMemory sharedMemory = new SharedMemory();

        Thread[] sensors = new Thread[8];
        for (int i = 0; i < sensors.length; i++) {
            sensors[i] = new Thread(new TemperatureSensor(sharedMemory));
            sensors[i].start();
        }

        Thread atmosphericModuleThread = new Thread(new TemperatureModule(sharedMemory));
        atmosphericModuleThread.start();
    }
}
