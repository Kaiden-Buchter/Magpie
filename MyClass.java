import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Magpie chatbot = new Magpie();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bot: " + chatbot.getGreeting());

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Bot: Goodbye!");
                break;
            }
            String response = chatbot.getResponse(userInput);
            System.out.println("Bot: " + response);
        }

        scanner.close();
    }
}