package matchmaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// TODO: use JSON for the name and answers
public class Matchmaker {
    private final HashMap<Integer, Integer> questions; // map of question number to number of answers
    private final LinkedList<User> users;

    /**
     * Loads the survey and stores the number of answers to each question.
     * Then, creates User objects.
     * User data should be in one directory, with the name being the first line and the data
     * being the second.
     */
    public Matchmaker(String surveyFile, String directory) throws IOException {
        // make answer counts for each question
        questions = new HashMap<>();
        String list = Files.readString(Path.of(surveyFile), StandardCharsets.US_ASCII);
        String[] counts = list.split(",");
        for (int i = 0; i < counts.length; i++) {
            questions.put(i, Integer.parseInt(counts[i]));
        }
        // create user objects from survey input
        users = new LinkedList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    String[] input = Files.readString(path, StandardCharsets.US_ASCII).split("\n");
                    String name = input[0]; // name is the first line. TODO might change
                    String[] answers = input[1].split(",");
                    User user = new User(name, answers);
                    users.add(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        Matchmaker mm = new Matchmaker("./data/survey.dat", "./data/users");
        mm.calculatePreferences();
        mm.write("data-out");
    }

    /**
     * Calculates the average of the differences between each user's answers.
     * Answer indices have a value based on the number of answers for the question that the
     * answer corresponds to.
     * The average of the differences between these values of two users will be stored.
     */
    public void calculatePreferences() {
        for (User one: users) {
            for (User two: users) {
                if (one != two && !one.preferenceExists(two)) {
                    // TODO: don't calculate preferences for the same gender
                    BigDecimal average = new BigDecimal("0.0");
                    // average of differences. will be same for user pairs
                    // use big decimal for rounding to thousands
                    for (int i = 0; i < questions.size(); i++) {
                        int ans = questions.get(i); // number of answers for the question index
                        // calculate the value of each answer index, which will be used in a
                        // difference between two values
                        BigDecimal v = BigDecimal.valueOf((double) one.getAnswer(i) / (ans - 1));
                        BigDecimal v2 = BigDecimal.valueOf((double) two.getAnswer(i) / (ans - 1));
                        // floating points are definitely needed
                        BigDecimal diff = BigDecimal.valueOf(Math.abs(v.doubleValue() - v2.doubleValue()));
                        average = average.add(diff);
                    }
                    BigDecimal preference = average.divide(BigDecimal.valueOf(questions.size()), 3,
                            RoundingMode.HALF_UP); // find the avg but scale to the thousandths
                    one.add(two, preference.doubleValue());
                    two.add(one, preference.doubleValue());
                }
            }
        }
    }

    /**
     * Writes the output: spreadsheet and individual data.
     * TODO should individuals know who they were most compatible with before matching?
     *
     * @param directory the directory to write the files to
     */
    public void write(String directory) throws IOException {
        writeCSV(directory);
        writeIndividuals(directory);
    }

    /**
     * The final data spreadsheet will be a table with each person's compatibility with one another.
     * This spreadsheet should be kept private.
     */
    private void writeCSV(String directory) throws IOException {
        // manual CSV creation
        HashMap<String, HashMap<String, Double>> map = new HashMap<>(); // preference map
        for (User user: users) {
            HashMap<String, Double> preferences = new HashMap<>();
            LinkedList<User.Preference> list = user.getPreferences();
            for (User.Preference preference: list) {
                preferences.put(preference.user.getName(), preference.getValue());
            }
            map.put(user.getName(), preferences);
        }
        String[][] csv = new String[users.size() + 1][];
        for (int i = 0; i < csv.length; i++) { // initialize so no errors
            csv[i] = new String[users.size() + 1];
            Arrays.fill(csv[i], "");
        }
        // first column/row labels
        csv[0][0] = "Name";
        ArrayList<String> names = new ArrayList<>(map.keySet());
        for (int i = 1; i <= names.size(); i++) {
            csv[0][i] = names.get(i - 1);
            csv[i][0] = names.get(i - 1);
        }
        for (int i = 1; i < csv.length; i++) {
            for (int j = 1; j < csv[i].length; j++) {
                String one = csv[0][i];
                String two = csv[j][0];
                if (!one.equals(two)) { // don't do preferences for one's self
                    csv[i][j] = String.valueOf(map.get(one).get(two));
                }
            }
        }
        StringBuilder csvBuilder = new StringBuilder();
        for (String[] array: csv) {
            for (String s: array) {
                csvBuilder.append(s).append(", ");
            }
            // remove the last comma
            csvBuilder = new StringBuilder(csvBuilder.substring(0, csvBuilder.length() - 2));
            csvBuilder.append("\n");
            // remember that there are empty values for a user's preference for themselves;
            // the last user will have an extra comma on purpose
        }
        System.out.println(csvBuilder);
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(directory + "/data.csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write(csvBuilder.toString());
        writer.close();
    }

    /**
     * Writes the matchmaker's results for individuals.
     * This is assumed to be used after the CSV is written. data-out should already be created.
     * TODO contact information, and this should probably be a pretty HTML page
     */
    private void writeIndividuals(String directory) throws IOException {
        File dir = new File(directory + "/users");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (User user: users) {
            File file = new File(directory + "/users/" + user.getName() + ".txt");
            StringBuilder output = new StringBuilder("Your top matches are: \n");
            int matches = 0;
            for (User.Preference preference: user.getPreferences()) {
                if (matches < 3) {
                    output.append(String.format("- %s\n", preference.user.getName()));
                    matches++; // TODO inline when checking for gender preferences
                }
                else {
                    break;
                }
            }
            FileWriter writer = new FileWriter(file);
            writer.write(output.toString());
            writer.close();
        }
    }
}
