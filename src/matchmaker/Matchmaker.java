package matchmaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private final HashSet<Match> matches;

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
        matches = new HashSet<>();
    }

    public static void main(String[] args) throws IOException {
        Matchmaker mm = new Matchmaker("./data/survey.dat", "./data/users");
        mm.calculatePreferences();
        mm.stableMatch();
        System.out.println("Matches found:");
        for (Match m: mm.matches) {
            System.out.println(m);
        }
        System.out.println();
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
                    double average = 0.0; // average of differences. will be same for user pairs
                    for (int i = 0; i < questions.size(); i++) {
                        int ans = questions.get(i); // number of answers for the question index
                        // calculate the value of each answer index, which will be used in a
                        // difference between two values
                        double v = (double) (one.getAnswer(i)) / (ans - 1);
                        double v2 = (double) (two.getAnswer(i)) / (ans - 1);
                        double diff = Math.max(v, v2) - Math.min(v, v2);
                        average += diff;
                    }
                    one.add(two, average / questions.size());
                    two.add(one, average / questions.size());
                }
            }
        }
    }

    /**
     * Matches two users, breaking off current matches if necessary.
     *
     * @return the list containing users that are now heartbroken and need a new match
     */
    public LinkedList<User> match(User one, User two) {
        LinkedList<User> list = new LinkedList<>();
        // find current matches and break them
        Match match1 = findMatch(one);
        Match match2 = findMatch(two);
        if (match1 != null) {
            User left = match1.getOther(one);
            matches.remove(match1);
            list.add(left);
            System.out.println("Match removed: " + match1);
        }
        if (match2 != null) {
            User left = match2.getOther(two);
            matches.remove(match2);
            list.add(left);
            System.out.println("Match removed: " + match2);
        }
        // make a new match
        Match m = new Match(one, two);
        matches.add(m);
        System.out.println("New match: " + m);
        System.out.println(matches);
        System.out.println();
        return list;
    }

    /**
     * Applies a stable matching algorithm to match users according to their preferences.
     * Users that are already matched will still try to match with their own preferences.
     * <p>
     * User C goes through all of their preferences, each labeled as A.
     * User A may or may not already be matched to a partner B.
     * User C may or may not already be matched to a partner D.
     * - It is important to still go through C's preferences, as a possible better match can be
     * made.
     * There are four cases to check:
     * - If they are both not taken, then match them both. Only remove C from the queue because
     * A will try again.
     * - If A is taken, then A must prefer C over B.
     * - If C is taken, then C must prefer A over D.
     * - If they are both taken, then they must prefer each other over their current matches.
     * To prevent an infinite loop, the average of differences MUST be LOWER.
     */
    public void stableMatch() {
        for (User user: users) {
            System.out.print(user.getName() + ": ");
            System.out.println(user.preferencesToString());
        }
        Queue<User> free = new LinkedList<>(users);
        while (!free.isEmpty()) { // TODO massive print debugs
            User C = free.poll();
            System.out.println(C);
            for (User.Preference preference: C.getPreferences()) {
                User A = preference.user; // C is trying to match with A. determine their statuses
                System.out.println("- new preference: " + A);
                Match aTaken = findMatch(A);
                Match cTaken = findMatch(C);

                // both are free, easiest case
                if (aTaken == null && cTaken == null) { // A^B C^D
                    System.out.println("Both are not taken.");
                    match(A, C);
                    break; // break after finding a match; it's the best case for now
                }

                // C is taken but wishes to possibly find a better one.
                else if (aTaken == null) {
                    System.out.println("Scandal #1: Looking for trouble.");
                    // in this case, just compare values
                    User D = cTaken.getOther(C);
                    double toChallenge = C.getPreference(D);
                    double challenger = C.getPreference(A);
                    System.out.println(C + "'s preference for current partner " + D + ": " + toChallenge);
                    System.out.println(C + "'s preference for new partner " + A + ": " + challenger);
                    if (challenger < toChallenge) {
                        LinkedList<User> unmatched = match(A, C);
                        free.addAll(unmatched);
                        break;
                    }
                    else {
                        System.out.println("Unsuccessful match.");
                    }
                }

                // A is taken, but C wishes to challenge.
                else if (cTaken == null) {
                    System.out.println("Scandal #2: A challenger approaches.");
                    User B = aTaken.getOther(A);
                    double toChallenge = A.getPreference(B);
                    double challenger = A.getPreference(C);
                    System.out.println(A + "'s preference for current partner " + B + ": " + toChallenge);
                    System.out.println(A + "'s preference for new partner " + C + ": " + challenger);
                    if (challenger < toChallenge) {
                        LinkedList<User> unmatched = match(A, C);
                        free.addAll(unmatched);
                        break;
                    }
                    else {
                        System.out.println("Unsuccessful match.");
                    }
                }

                // both are taken, worst case scandal
                else {
                    System.out.println("Scandal #3: The worst case.");
                    User B = aTaken.getOther(A);
                    User D = cTaken.getOther(C);
                    if (A == D) {
                        if (B == C) {
                            // have already arrived at the next lowest preference, and
                            // you're already matched. No need to continue.
                            System.out.println("The best match was already made; not a scandal.");
                            System.out.println();
                            break;
                        }
                        else {
                            throw new IllegalStateException(); // matching mess up
                        }
                    }
                    double toChallenge1 = A.getPreference(B);
                    double toChallenge2 = C.getPreference(D);
                    double challenger = C.getPreference(A);
                    System.out.println(A + "'s preference for current partner " + B + ": " + toChallenge1);
                    System.out.println(C + "'s preference for current partner " + D + ": " + toChallenge2);
                    System.out.println("Preference for each other: " + challenger);
                    if (challenger < toChallenge1 && challenger < toChallenge2) {
                        LinkedList<User> unmatched = match(A, C);
                        free.addAll(unmatched);
                        break;
                    }
                    else {
                        System.out.println("Unsuccessful match.");
                    }
                }
            }
        }
    }

    /**
     * Determines if there is a match containing the specified user.
     */
    private Match findMatch(User user) {
        for (Match match: matches) {
            if (match.contains(user)) {
                return match;
            }
        }
        return null;
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
        for (Match match: matches) {
            for (User user: match.getUsers()) {
                User other = match.getOther(user);
                File file = new File(directory + "/users/" + user.getName() + ".txt");
                String output = String.format("You matched with %s!\n", other.getName()); // TODO
                FileWriter writer = new FileWriter(file);
                writer.write(output);
                writer.close();
            }
        }
    }

    /**
     * Represents a match containing two partners.
     */
    private static class Match {
        private final User a, b;

        public Match(User a, User b) {
            this.a = a;
            this.b = b;
        }

        public User[] getUsers() {
            return new User[]{a, b};
        }

        public String toString() {
            return a + " & " + b;
        }

        /**
         * @return if a user is one of the users in the match. Used for searching
         */
        public boolean contains(User user) {
            return a.equals(user) || b.equals(user);
        }

        /**
         * @return the partner of a user in this match. Used for determining whom one is taken by.
         */
        public User getOther(User user) {
            if (user.equals(a)) {
                return b;
            }
            else if (user.equals(b)) {
                return a;
            }
            throw new IllegalArgumentException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Match match = (Match) o;
            if (Objects.equals(a, match.a) && Objects.equals(b, match.b)) {
                return true;
            }
            else return Objects.equals(a, match.b) && Objects.equals(b, match.a);
        }
    }
}
