import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import matchmaker.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFiles {
    /**
     * Validates the survey data file that has sets, each containing one question and multiple
     * answers.
     * This should be done before survey file generation to prove the answer file was concatenated
     * properly.
     * Questions shouldn't be empty, and answers should not be empty or have a vertical bar.
     */
    @Test
    public void testSurveyData() throws IOException { // Test splits
        String file = "src/survey/create/ans.dat"; // file that sets are in
        String content = Files.readString(Path.of(file), StandardCharsets.US_ASCII);
        String[] sets = content.split("\n\n");
        for (String s: sets) {
            assertTrue(s.contains("\n")); // test all sets separate the question and answers with \n
            String question = s.split("\n")[0];
            String answers = s.split("\n")[1];
            assertTrue(question.trim().length() > 0); // make sure questions aren't empty
            for (String a: answers.split(" \\| ")) { // make sure answers aren't empty
                assertTrue(a.trim().length() > 0);
            }
        }
    }

    /**
     * Validates the list of answer counts for survey questions.
     */
    @Test
    public void testSurveyCounts() throws IOException {
        String file = "data/survey.dat"; // file that has the list of counts
        String content = Files.readString(Path.of(file), StandardCharsets.US_ASCII);
        String[] counts = content.split(",");
        for (String number: counts) {
            assertTrue(number.trim().length() > 0); // make sure counts are proper
            int num = Integer.parseInt(number);
            assertTrue(num >= 2); // questions should have at least 2 answers each
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
