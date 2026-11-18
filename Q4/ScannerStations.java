
class ThreadStation extends Thread {
    private int scripts;

    ThreadStation(String name, int scripts) {
        super(name);
        this.scripts = scripts;
    }

    public void run() {
        System.out.println(getName() + " started.");

        try {
            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(300);
            }

            System.out.println(getName()
                    + " completed " + scripts + " scripts.");

        } catch (InterruptedException e) {
            System.out.println(getName() + " interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

class RunnableStation implements Runnable {
    private String name;
    private int scripts;

    RunnableStation(String name, int scripts) {
        this.name = name;
        this.scripts = scripts;
    }

    public void run() {
        System.out.println(name + " started.");

        try {
            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(300);
            }

            System.out.println(name
                    + " completed " + scripts + " scripts.");

        } catch (InterruptedException e) {
            System.out.println(name + " interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

public class ScannerStations {
    public static void main(String[] args) {

        ThreadStation station1 =
                new ThreadStation("Station 1", 5);

        ThreadStation station2 =
                new ThreadStation("Station 2", 4);

        Thread station3 =
                new Thread(new RunnableStation("Station 3", 6));

        Thread station4 =
                new Thread(new RunnableStation("Station 4", 3));

        // Assigning thread priorities
// Assigning thread priorities
station1.setPriority(Thread.MIN_PRIORITY);
station2.setPriority(Thread.NORM_PRIORITY);
station3.setPriority(Thread.MAX_PRIORITY);
station4.setPriority(Thread.MIN_PRIORITY);

// Making Station 4 a daemon thread
station4.setDaemon(true);
        Thread[] stations = {
            station1, station2, station3, station4
        };

        System.out.println("===== SCANNER STATIONS =====");
        System.out.println("Thread Priority Experiment");

        for (Thread station : stations) {
            System.out.println(station.getName()
                    + " priority: " + station.getPriority());
        }

        for (Thread station : stations) {
            station.start();
        }

        System.out.println("\nChecking thread status:");

        for (Thread station : stations) {
            System.out.println(station.getName()
                    + " alive: " + station.isAlive());
        }

        for (Thread station : stations) {
            try {
                station.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println("\nAll scanner stations completed.");
        System.out.println("Total scripts scanned: 18");
    }
}
