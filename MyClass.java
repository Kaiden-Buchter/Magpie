import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Magpie maggie = new Magpie();
        Scanner in = new Scanner(System.in);

        System.out.println(maggie.getGreeting());
        String statement = in.nextLine();

        while (!statement.equalsIgnoreCase("Bye")) {
            System.out.println(maggie.getResponse(statement));
            statement = in.nextLine();
        }
    }
}