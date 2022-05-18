package matchmaker;

import java.util.LinkedList;
import java.util.Objects;
import java.util.TreeSet;

public class User {
    private final String name, priv, pub;
    private final int id;
    private final int[] answers; // defined number of answers
    private final TreeSet<Preference> preferences; // contains avg. of differences for a person
    // please be careful with overriding mappings. maps just won't work
    // a set will and can have comparable elements

    public User(String name, int id, String priv, String pub, int[] answers) {
        this.name = name;
        this.id = id;
        this.priv = priv;
        this.pub = pub;
        this.answers = new int[answers.length];
        System.arraycopy(answers, 0, this.answers, 0, answers.length);
        preferences = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    /**
     * @return contact information to give to the matched users
     */
    public String getPub() {
        return pub;
    }

    /**
     * Returns the answer index for a question, so its value can be calculated.
     *
     * @param index the question number
     * @return the answer index for the question index
     */
    public int getAnswer(int index) {
        return answers[index];
    }

    /**
     * Determines if this user already has a preference calculated for the other user with a
     * linear search.
     * The preferences towards each other is the same, so no need to recalculate.
     *
     * @param user the other user
     * @return if the current user already has his preference calculated
     */
    public boolean preferenceExists(User user) {
        for (Preference preference: preferences) {
            if (preference.user.id == user.id) {
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
        return Objects.equals(id, user.id);
    }

    public static class Preference implements Comparable<Preference> {
        private final double value;
        public final User user;

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
                // must have this or the tree set won't add it. This doesn't matter too much
                return Integer.compare(user.id, other.user.id);
            }
            return Double.compare(value, other.value);
        }
    }
}
