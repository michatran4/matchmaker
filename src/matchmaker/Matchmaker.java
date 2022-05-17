package matchmaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Matchmaker {
    private Question[] questions;
    private final LinkedList<User> users;
    private final HashMap<String, Integer> infoColumns;
    // extra information columns that aren't questions. important data for user creation, though

    /**
     * Loads the survey and stores the number of answers to each question.
     * Then, creates User objects.
     * User data should be in one directory, with the name being the first line and the data
     * being the second.
     */
    public Matchmaker(String surveyFile, String countsData, String surveyData) throws IOException {
        users = new LinkedList<>();
        infoColumns = new HashMap<>();
        // 0th column is the timestamp
        infoColumns.put("email", 1);
        infoColumns.put("name", 2);
        infoColumns.put("id", 3);
        infoColumns.put("private", 4);
        infoColumns.put("public", 5);
        parseCSV(surveyFile, countsData, surveyData);
    }

    /**
     * Creates user and question objects from the survey input.
     *
     * @param surveyFile   the survey questions and answers
     * @param countsData   the count data to make questions properly
     * @param surveyData   the csv from user input
     */
    private void parseCSV(String surveyFile, String countsData, String surveyData) throws IOException {
        // TODO document
        String[] survey = Files.readString(Path.of(surveyFile), StandardCharsets.US_ASCII)
                .split("\n\n"); // sets of questions and answers
        survey = Arrays.stream(survey).filter(s -> s.contains("\n")).toArray(String[]::new);
        questions = new Question[survey.length];
        // disregard sections. This needs to be paired up correctly with question creation
        Scanner counts = new Scanner(Files.readString(Path.of(countsData), StandardCharsets.US_ASCII)
                        .replace(",", " ")); // TODO should be weights
        Scanner csv = new Scanner(new File(surveyData));
        csv.nextLine();
        int index = 0;
        for (String question: survey) {
            Scanner answers = new Scanner(question);
            answers.nextLine();
            double weight = 1.0;
            String count = counts.next();
            System.out.println(question + " " + count);
            if (count.contains(":")) {
                weight = Double.parseDouble(count.split(":")[0]);
            }
            questions[index++] = new Question(question, answers, weight);
        }
        // now, initialize all users
        while (csv.hasNextLine()) {
            String[] answers = csv.nextLine().split(",");
            int[] indices = new int[questions.length];
            String name = answers[infoColumns.get("name")];
            String email = answers[infoColumns.get("email")];
            int id = Integer.parseInt(answers[infoColumns.get("id")]);
            infoColumns.put("id", 3);
            String priv = answers[infoColumns.get("private")];
            String pub = answers[infoColumns.get("public")];
            // calculate the indices here, starting with where the questions start
            index = 0;
            for (int i = answers.length - questions.length; i < answers.length; i++) {
                Question question = questions[index];
                int answer = question.getIndex(answers[i]);
                indices[index++] = answer;
            }
            User user = new User(name, email, id, priv, pub, indices);
            users.add(user);
        }
        System.out.println(users);
    }

    public static void main(String[] args) throws IOException {
        Matchmaker mm = new Matchmaker("./data/survey.txt", "./data/counts.dat",
                "./data/forms.csv");
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
                    for (int i = 0; i < questions.length; i++) {
                        int ans = questions[i].getNumAnswers(); // number of answers for the question index
                        // calculate the value of each answer index, which will be used in a
                        // difference between two values
                        BigDecimal v = BigDecimal.valueOf((double) one.getAnswer(i) / (ans - 1));
                        BigDecimal v2 = BigDecimal.valueOf((double) two.getAnswer(i) / (ans - 1));
                        // floating points are definitely needed
                        BigDecimal diff = BigDecimal.valueOf(Math.abs(v.doubleValue() - v2.doubleValue()));
                        diff = diff.multiply(BigDecimal.valueOf(questions[i].weight));
                        average = average.add(diff);
                    }
                    BigDecimal preference = average.divide(BigDecimal.valueOf(questions.length), 3,
                            RoundingMode.HALF_UP); // find the avg but scale to the thousandths
                    one.add(two, preference.doubleValue());
                    two.add(one, preference.doubleValue());
                }
            }
        }
    }

    /**
     * Writes the output: spreadsheet and individual data.
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
        //System.out.println(csvBuilder);
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
            File file = new File(directory + "/users/" + user.getId() + ".txt");
            StringBuilder output = new StringBuilder("Your top matches are here!\n\n");
            int matches = 0;
            for (User.Preference preference: user.getPreferences()) {
                if (matches < 3) {
                    output.append(
                            String.format("%s\nContact Information:\n%s\n\n",
                            preference.user.getName(), preference.user.getPub())
                    );
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

    private static class Question {
        private final String question;
        private final double weight;
        private final HashMap<String, Integer> answers; // map of answer to its index

        public Question(String question, Scanner answers, double weight) {
            this.question = question;
            this.answers = new HashMap<>();
            while (answers.hasNextLine()) {
                this.answers.put(answers.nextLine(), this.answers.size());
            }
            this.weight = weight;
        }

        public int getNumAnswers() {
            return answers.size();
        }

        public int getIndex(String answer) {
            return answers.get(answer);
        }

        public String toString() {
            return question + ": " + answers;
        }
    }
}
