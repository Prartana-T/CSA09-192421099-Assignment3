
import java.util.concurrent.CountDownLatch;

class Wallet {
    int id;
    int balance;

    Wallet(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }
}

public class WalletTransfer {

    // Version 1: Can cause deadlock
    static void unsafeTransfer(
            Wallet from, Wallet to,
            CountDownLatch ready) {

        synchronized (from) {
            System.out.println(
                Thread.currentThread().getName()
                + " locked Wallet " + from.id
            );

            ready.countDown();

            try {
                ready.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            synchronized (to) {
                from.balance -= 100;
                to.balance += 100;
            }
        }
    }

    // Version 2: Prevents deadlock using lock ordering
    static void safeTransfer(Wallet from, Wallet to) {

        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;

        synchronized (first) {
            synchronized (second) {
                from.balance -= 100;
                to.balance += 100;
            }
        }

        System.out.println(
            Thread.currentThread().getName()
            + " completed transfer safely."
        );
    }

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("===== DEADLOCK DEMONSTRATION =====");

        Wallet a = new Wallet(1, 1000);
        Wallet b = new Wallet(2, 1000);

        CountDownLatch ready = new CountDownLatch(2);

        Thread t1 = new Thread(
            () -> unsafeTransfer(a, b, ready), "Student 1"
        );

        Thread t2 = new Thread(
            () -> unsafeTransfer(b, a, ready), "Student 2"
        );

        // Daemon threads prevent the demonstration
        // from keeping the program running forever.
        t1.setDaemon(true);
        t2.setDaemon(true);

        t1.start();
        t2.start();

        t1.join(1000);
        t2.join(1000);

        if (t1.isAlive() && t2.isAlive()) {
            System.out.println("Deadlock detected!");
            System.out.println("Both threads are waiting for each other.");
        }

        System.out.println("\n===== DEADLOCK PREVENTION =====");

        Wallet x = new Wallet(1, 1000);
        Wallet y = new Wallet(2, 1000);

        Thread t3 = new Thread(
            () -> safeTransfer(x, y), "Student 1"
        );

        Thread t4 = new Thread(
            () -> safeTransfer(y, x), "Student 2"
        );

        t3.start();
        t4.start();

        t3.join();
        t4.join();

        System.out.println("Both transfers completed.");
        System.out.println("Wallet 1 balance: " + x.balance);
        System.out.println("Wallet 2 balance: " + y.balance);
    }
}
