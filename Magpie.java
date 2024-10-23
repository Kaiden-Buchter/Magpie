import java.util.Random;
import java.util.Arrays;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Magpie {
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
     * Get a default greeting
     * @return a greeting
     */
    public String getGreeting() {
        return "Hello, let's talk.";
    }

    /**
     * Gives a response to a user statement
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
        } else if (findKeyword(statement, "hello") >= 0 || findKeyword(statement, "hi") >= 0) {
            response = "Hello! How can I assist you today?";
        } else if (findKeyword(statement, "how are you") >= 0) {
            response = "I'm just a program, but I'm here to help you!";
        } else if (findKeyword(statement, "weather") >= 0) {
            response = "I'm not sure about the weather, but I hope it's nice where you are!";
        } else {
            int psn = findKeyword(statement, "i", 0);
                if (psn >= 0 && findKeyword(statement, "you", psn) >= 0) {
                    response = transformIYouStatement(statement);
                } else {
                    response = getRandomResponse();
                }
            }
        return response;
    }

    /**
     * Take a statement with "I want to <something>." and transform it into 
     * "What would it mean to <something>?"
     * @param statement the user statement, assumed to contain "I want to"
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
     * Take a statement with "I want <something>." and transform it into 
     * "Would you really be happy if you had <something>?"
     * @param statement the user statement, assumed to contain "I want"
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
     * Take a statement with "I <something> you" and transform it into 
     * "Why do you <something> me?"
     * @param statement the user statement, assumed to contain "I" followed by "you"
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
     * Search for one word in phrase. The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string
     * (so, for example, "I know" does not contain "no").
     *
     * @param statement the string to search
     * @param goal the string to search for
     * @param startPos the character of the string to begin the search at
     * @return the index of the first occurrence of goal in statement or -1 if it's not found
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
     * Search for one word in phrase. The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string
     * (so, for example, "I know" does not contain "no"). The search begins at the beginning of the string.
     * @param statement the string to search
     * @param goal the string to search for
     * @return the index of the first occurrence of goal in statement or -1 if it's not found
     */
    private int findKeyword(String statement, String goal) {
        return findKeyword(statement, goal, 0);
    }

    /**
     * Pick a default response to use if nothing else fits.
     * @return a non-committal string
     */
    private String getRandomResponse() {
        Random r = new Random();
        return randomResponses[r.nextInt(randomResponses.length)];
    }

    /**
     * Check if the statement contains inappropriate language using an external API.
     * @param statement the user statement
     * @return true if inappropriate language is found, false otherwise
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
     * Check if the statement contains family-related keywords or common misspellings using an external API.
     * @param statement the user statement
     * @return true if family-related keywords, false otherwise
     */
    private boolean containsFamilyKeyword(String statement) {
        String[] familyKeywords = {"mother", "father", "sister", "brother"};
        for (String keyword : familyKeywords) {
            if (findKeyword(statement, keyword) >= 0) {
                return true;
            }
        }
        return false;
    }
}