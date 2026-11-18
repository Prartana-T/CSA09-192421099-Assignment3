
public class SeatBooking {

    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    // Version 1: Without synchronization
    public void bookWithoutSync(int seat) {
        if (!booked[seat]) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            booked[seat] = true;
            bookingsMade++;
        }
    }

    // Version 2: Synchronized method
    public synchronized void bookWithMethod(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
        }
    }

    // Version 3: Synchronized block
    public void bookWithBlock(int seat) {
        synchronized (this) {
            if (!booked[seat]) {
                booked[seat] = true;
                bookingsMade++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "unsafe";

        SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];

        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10;

            users[i] = new Thread(() -> {
                switch (mode) {
                    case "method":
                        screen.bookWithMethod(seat);
                        break;
                    case "block":
                        screen.bookWithBlock(seat);
                        break;
                    default:
                        screen.bookWithoutSync(seat);
                }
            });

            users[i].start();
        }

        for (Thread t : users) {
            t.join();
        }

        System.out.println("Booking Mode: " + mode);
        System.out.println("Total users: 100");
        System.out.println("Available seats: 10");
        System.out.println("Seats sold: " + screen.bookingsMade);
    }
}
