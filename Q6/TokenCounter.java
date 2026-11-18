
public class TokenCounter {

    private final int[] buffer = new int[5];
    private int count = 0;
    private int in = 0;
    private int out = 0;

    private int maximumSize = 0;
    private int minimumSize = 5;

    public synchronized void put(int token)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;

        maximumSize = Math.max(maximumSize, count);
        minimumSize = Math.min(minimumSize, count);

        System.out.println("Produced: " + token
                + " | Buffer size: " + count);

        notifyAll();
    }

    public synchronized int take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        maximumSize = Math.max(maximumSize, count);
        minimumSize = Math.min(minimumSize, count);

        System.out.println("Consumed: " + token
                + " | Buffer size: " + count);

        notifyAll();

        return token;
    }

    public static void main(String[] args)
            throws InterruptedException {

        TokenCounter counter = new TokenCounter();
        int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= 20; t++) {
                    counter.put(t);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Cook");

        Thread student1 = new Thread(() -> {
            try {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += counter.take();
                }
                collected[0] = sum;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Student 1");

        Thread student2 = new Thread(() -> {
            try {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += counter.take();
                }
                collected[1] = sum;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Student 2");

        cook.start();
        student1.start();
        student2.start();

        cook.join();
        student1.join();
        student2.join();

        System.out.println("\n===== FINAL SUMMARY =====");
        System.out.println("Student 1 collected: " + collected[0]);
        System.out.println("Student 2 collected: " + collected[1]);
        System.out.println("Total tokens collected: "
                + (collected[0] + collected[1]));
        System.out.println("Maximum buffer size: "
                + counter.maximumSize);
        System.out.println("Minimum buffer size: "
                + counter.minimumSize);
    }
}
