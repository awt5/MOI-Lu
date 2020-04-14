import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert number1");
        System.out.print("> ");
        String number1 = scanner.nextLine();
        System.out.println("Insert number2");
        System.out.print("> ");
        String number2 = scanner.nextLine();

        int sum = Integer.parseInt(number1) + Integer.parseInt(number2);
        System.out.println("Sum: " + sum);
    }
}
