
import java.io.*;

class TrackedReader implements AutoCloseable {
    private BufferedReader reader;
    private static int openCount = 0;

    TrackedReader(String fileName) throws IOException {
        reader = new BufferedReader(new FileReader(fileName));
        openCount++;
    }

    String readLine() throws IOException {
        return reader.readLine();
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
            openCount--;
        }
    }

    static int getOpenCount() {
        return openCount;
    }
}

public class DailySalesReport {

    static void createFiles() throws IOException {
        try (PrintWriter p1 = new PrintWriter("store1.txt");
             PrintWriter p2 = new PrintWriter("store2.txt")) {
            p1.println("Store 1 Sales: Rs. 5000");
            p2.println("Store 2 Sales: Rs. 7000");
        }
    }

    // Manual resource closing
    static void manualClosing() {
        TrackedReader first = null;
        TrackedReader second = null;

        try {
            first = new TrackedReader("store1.txt");
            System.out.println(first.readLine());

            second = new TrackedReader("missing.txt");
            System.out.println(second.readLine());

            first.close();
            second.close();

        } catch (IOException e) {
            System.out.println("Manual version error: " + e.getMessage());

        } finally {
            System.out.println("Manual open readers: "
                    + TrackedReader.getOpenCount());
        }
    }

    // Automatic resource closing
    static void automaticClosing() {
        try (TrackedReader first = new TrackedReader("store1.txt");
             TrackedReader second = new TrackedReader("missing.txt")) {

            System.out.println(first.readLine());
            System.out.println(second.readLine());

        } catch (IOException e) {
            System.out.println("Try-with-resources error: "
                    + e.getMessage());

        } finally {
            System.out.println("Try-with-resources open readers: "
                    + TrackedReader.getOpenCount());
        }
    }

   
public static void main(String[] args) throws IOException {
    createFiles();

    System.out.println("===== TRY-WITH-RESOURCES =====");
    automaticClosing();

    System.out.println("\n===== MANUAL CLOSING =====");
    manualClosing();
}

}
