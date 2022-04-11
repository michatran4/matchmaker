package matchmaker;

import java.util.LinkedList;
import java.util.Objects;
import java.util.TreeSet;

public class User {
    private final String name;
    private final int[] answers; // defined number of answers
    private final TreeSet<Preference> preferences; // contains avg. of differences for a person
    // please be careful with overriding mappings. maps just won't work
    // a set will and can have comparable elements

    public User(String name, String[] answers) {
        this.name = name;
        this.answers = new int[answers.length];
        for (int i = 0; i < answers.length; i++) {
            this.answers[i] = Integer.parseInt(answers[i]);
        }
        preferences = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the answer index for a question, so its value can be calculated.
     * @param index the question number
     * @return the answer index for the question index
     */
    public int getAnswer(int index) {
        return answers[index];
    }

    /**
     * Determines if this user already has a preference calculated for the other user.
     * The preferences towards each other is the same, so no need to recalculate.
     * @param user the other user
     * @return if the current user already has his preference calculated
     */
    public boolean preferenceExists(User user) {
        for (Preference preference: preferences) {
            if (preference.user.name.equals(user.name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a preference for another user.
     */
    public void add(User user, double preference) {
        Preference p = new Preference(user, preference);
        preferences.add(p);
    }

    /**
     * Gets the user's preference for another user.
     */
    public double getPreference(User user) {
        for (Preference preference: preferences) {
            if (preference.user.equals(user)) {
                return preference.value;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * @return the preferences, containing a user object tied to a preference value
     */
    public LinkedList<Preference> getPreferences() {
        return new LinkedList<>(preferences);
    }

    public String preferencesToString() {
        if (preferences.isEmpty()) return "{}"; // for debugging, when it's initially empty
        StringBuilder s = new StringBuilder("{");
        for (Preference p: preferences) {
            s.append(p.value).append("=").append(p.user.toString()).append(", ");
        }
        return s.substring(0, s.length() - 2) + "}"; // remove last comma + space
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    public static class Preference implements Comparable<Preference> {
        public User user;
        private final double value;

        public Preference(User u, double p) {
            user = u;
            value = p;
        }

        public double getValue() {
            return value;
        }

        /**
         * Places preferences in order in the set.
         */
        public int compareTo(Preference other) {
            if (Double.compare(value, other.value) == 0) {
                // second comparator for tree set. include duplicate averages, but not names
                // must have this or the tree set won't add it
                return user.getName().compareTo(other.user.getName());
            }
            return Double.compare(value, other.value);
        }
    }
}
