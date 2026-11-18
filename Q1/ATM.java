
import java.util.Scanner;

class ATM {
    private double balance = 5000.00;

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount must be positive."
            );
        }

        if (amount > balance) {
            throw new ArithmeticException(
                "Insufficient account balance."
            );
        }

        balance -= amount;
        System.out.println("Withdrawal successful.");
        System.out.printf("Remaining balance: Rs. %.2f%n", balance);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        Scanner sc = new Scanner(System.in);

        System.out.println("===== ATM SYSTEM =====");
        System.out.println("Available balance: Rs. 5000.00");

        try {
            System.out.print("Enter withdrawal amount: ");
            double amount = sc.nextDouble();
            atm.withdraw(amount);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount: " + e.getMessage());

        } catch (ArithmeticException e) {
            System.out.println("Transaction failed: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");

        } finally {
            System.out.println("Thank you for using our ATM.");
            sc.close();
        }
    }
}
