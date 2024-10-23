import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Magpie chatbot = new Magpie();
        Scanner scanner = new Scanner(System.in);

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
}