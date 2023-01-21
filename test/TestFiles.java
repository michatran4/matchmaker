import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFiles {
    /**
     * Validates the survey data file that has sets, each containing one question and multiple
     * answers.
     * Questions and answers cannot be empty.
     */
    @Test
    public void testSurveyData() throws IOException { // Test splits
        String file = "data/survey.txt"; // file that sets are in
        String content = Files.readString(Path.of(file), StandardCharsets.US_ASCII);
        String[] sets = content.split("\n\n");
        for (String set: sets) {
            assertTrue(set.contains("\n")); // test if set has a newline
            for (String s: set.split("\n")) { // make sure questions/answers aren't empty
                assertTrue(s.trim().length() > 0);
            }
            assertTrue(set.split("\n").length >= 3); // at least one question and 2 answers
        }
    }

    /**
     * Validates the list of survey question weights.
     */
    @Test
    public void testSurveyWeights() throws IOException {
        String file = "data/weights.dat"; // file that has the list of counts
        String content = Files.readString(Path.of(file), StandardCharsets.US_ASCII);
        String[] counts = content.split(" ");
        for (String number: counts) {
            assertTrue(number.trim().length() > 0); // nonempty weight
            double num = Double.parseDouble(number);
            assertTrue(num > 0); // valid weight
        }
    }

    /**
     * Make sure the number of submitted surveys is even so that you can match an even number of
     * people.
     */
    @Test
    public void testUserCount() {
        int count = Objects.requireNonNull(new File("data/users").list()).length;
        assertEquals(0, count % 2);
    }
}
