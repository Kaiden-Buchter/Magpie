import java.util.Random;
import java.util.Arrays;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Magpie {
    // Array of random responses
    private String[] randomResponses = {
        "Interesting, tell me more",
        "Hmmm.",
        "Do you really think so?",
        "You don't say.",
        "Gee wilikers",
        "Is it getting hot in here?",
        "So, would you like to go for a walk?",
        "Could you say that again?"
    };

    /**
     * Returns the initial greeting message.
     * 
     * @return the greeting message
     */
    public String getGreeting() {
        return "Hello, let's talk.";
    }

    /**
     * Gives a response to a user statement.
     * 
     * @param statement the user statement
     * @return a response based on the rules given
     */
    public String getResponse(String statement) {
        statement = statement.trim().toLowerCase();
        String response = "";
        if (statement.length() == 0) {
            response = "Say something, please.";
        } else if (containsInappropriateLanguage(statement)) {
            response = "Please avoid using inappropriate language.";
        } else if (findKeyword(statement, "no") >= 0) {
            response = "Why so negative?";
        } else if (containsFamilyKeyword(statement)) {
            response = "Tell me more about your family.";
        } else if (findKeyword(statement, "i want to", 0) >= 0) {
            response = transformIWantToStatement(statement);
        } else if (findKeyword(statement, "i want", 0) >= 0) {
            response = transformIWantStatement(statement);
        } else {
            int psn = findKeyword(statement, "you", 0);
            if (psn >= 0 && findKeyword(statement, "me", psn) >= 0) {
                response = transformYouMeStatement(statement);
            } else {
                psn = findKeyword(statement, "i", 0);
                if (psn >= 0 && findKeyword(statement, "you", psn) >= 0) {
                    response = transformIYouStatement(statement);
                } else {
                    response = getRandomResponse();
                }
            }
        }
        return response;
    }

    /**
     * Transforms statements of the form "I want to [something]".
     * 
     * @param statement the user statement
     * @return the transformed statement
     */
    private String transformIWantToStatement(String statement) {
        statement = statement.trim();
        String lastChar = statement.substring(statement.length() - 1);
        if (lastChar.equals(".")) {
            statement = statement.substring(0, statement.length() - 1);
        }
        int psn = findKeyword(statement, "i want to", 0);
        String restOfStatement = statement.substring(psn + 9).trim();
        return "What would it mean to " + restOfStatement + "?";
    }

    /**
     * Transforms statements of the form "I want [something]".
     * 
     * @param statement the user statement
     * @return the transformed statement
     */
    private String transformIWantStatement(String statement) {
        statement = statement.trim();
        String lastChar = statement.substring(statement.length() - 1);
        if (lastChar.equals(".")) {
            statement = statement.substring(0, statement.length() - 1);
        }
        int psn = findKeyword(statement, "i want", 0);
        String restOfStatement = statement.substring(psn + 6).trim();
        return "Would you really be happy if you had " + restOfStatement + "?";
    }

    /**
     * Transforms statements of the form "You [something] me".
     * 
     * @param statement the user statement
     * @return the transformed statement
     */
    private String transformYouMeStatement(String statement) {
        statement = statement.trim();
        String lastChar = statement.substring(statement.length() - 1);
        if (lastChar.equals(".")) {
            statement = statement.substring(0, statement.length() - 1);
        }
        int psnOfYou = findKeyword(statement, "you", 0);
        int psnOfMe = findKeyword(statement, "me", psnOfYou + 3);
        String restOfStatement = statement.substring(psnOfYou + 3, psnOfMe).trim();
        return "What makes you think that I " + restOfStatement + " you?";
    }

    /**
     * Transforms statements of the form "I [something] you".
     * 
     * @param statement the user statement
     * @return the transformed statement
     */
    private String transformIYouStatement(String statement) {
        statement = statement.trim();
        String lastChar = statement.substring(statement.length() - 1);
        if (lastChar.equals(".")) {
            statement = statement.substring(0, statement.length() - 1);
        }
        int psnOfI = findKeyword(statement, "i", 0);
        int psnOfYou = findKeyword(statement, "you", psnOfI);
        String restOfStatement = statement.substring(psnOfI + 1, psnOfYou).trim();
        return "Why do you " + restOfStatement + " me?";
    }

    /**
     * Finds the position of a keyword in a statement starting from a given position.
     * 
     * @param statement the user statement
     * @param goal the keyword to find
     * @param startPos the position to start the search from
     * @return the position of the keyword, or -1 if not found
     */
    private int findKeyword(String statement, String goal, int startPos) {
        String phrase = statement.trim().toLowerCase();
        goal = goal.toLowerCase();
        int psn = phrase.indexOf(goal, startPos);
        while (psn >= 0) {
            String before = " ", after = " ";
            if (psn > 0) {
                before = phrase.substring(psn - 1, psn);
            }
            if (psn + goal.length() < phrase.length()) {
                after = phrase.substring(psn + goal.length(), psn + goal.length() + 1);
            }
            if (((before.compareTo("a") < 0) || (before.compareTo("z") > 0))
                    && ((after.compareTo("a") < 0) || (after.compareTo("z") > 0))) {
                return psn;
            }
            psn = phrase.indexOf(goal, psn + 1);
        }
        return -1;
    }

    /**
     * Overloaded method to find the position of a keyword in a statement starting from the beginning.
     * 
     * @param statement the user statement
     * @param goal the keyword to find
     * @return the position of the keyword, or -1 if not found
     */
    private int findKeyword(String statement, String goal) {
        return findKeyword(statement, goal, 0);
    }

    /**
     * Returns a random response from the array of random responses.
     * 
     * @return a random response
     */
    private String getRandomResponse() {
        Random r = new Random();
        return randomResponses[r.nextInt(randomResponses.length)];
    }

    /**
     * Checks if the statement contains inappropriate language using an external API.
     * 
     * @param statement the user statement
     * @return true if the statement contains inappropriate language, false otherwise
     */
    private boolean containsInappropriateLanguage(String statement) {
        try {
            URI uri = new URI("https", "www.purgomalum.com", "/service/containsprofanity", "text=" + statement, null);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();
                return Boolean.parseBoolean(inline);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the statement contains any family-related keywords.
     * 
     * @param statement the user statement
     * @return true if the statement contains family-related keywords, false otherwise
     */
    private boolean containsFamilyKeyword(String statement) {
        String[] familyKeywords = {"mother", "father", "sister", "brother"};
        for (String keyword : familyKeywords) {
            if (findKeyword(statement, keyword) >= 0) {
                return true;
            }
        }
        return containsCommonMisspellings(statement);
    }

    /**
     * Checks if the statement contains common misspellings of family-related keywords using an external API.
     * 
     * @param statement the user statement
     * @return true if the statement contains common misspellings, false otherwise
     */
    private boolean containsCommonMisspellings(String statement) {
        try {
            URI uri = new URI("https", "api.datamuse.com", "/words", "sl=" + statement, null);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                Scanner sc = new Scanner(url.openStream());
                String inline = "";
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();

                String[] words = inline.split("\\},\\{");
                for (String word : words) {
                    if (word.contains("\"word\":\"mother\"") || word.contains("\"word\":\"father\"") ||
                        word.contains("\"word\":\"sister\"") || word.contains("\"word\":\"brother\"")) {
                        return true;
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}