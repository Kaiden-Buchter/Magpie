import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Magpie chatbot = new Magpie();
        Scanner scanner = new Scanner(System.in);

        clearScreen();
        System.out.println("Welcome to Magpie Chatbot!");
        System.out.println("==========================");
        System.out.println("Magpie: " + chatbot.getGreeting());

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("bye") || userInput.equalsIgnoreCase("goodbye")) {
                System.out.println("Magpie: Goodbye!");
                break;
            }
            String response = chatbot.getResponse(userInput);
            System.out.println("Magpie: " + response);
        }

        scanner.close();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}