import org.junit.Test;
import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class QuicksortTest extends TestCase {
    private CheckFile fileChecker;

    /**
     * Sets up the tests that follow. In general, used for initialization.
     */
    public void setUp() {
        fileChecker = new CheckFile();
    }


    /**
     * This method is a demonstration of the file generator and file checker
     * functionality. It calles generateFile to create a small "ascii" file.
     * It then calls the file checker to see if it is sorted (presumably not
     * since we don't call a sort method in this test, so we assertFalse).
     *
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    @Test
    public void testFileGenerator() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("testinput.txt", "1", 'a');
        // In a real test we would call the sort
        // Quicksort.main(args);
        // In a real test, the following would be assertTrue()
        assertFalse(fileChecker.checkFile("testinput.txt"));
        Quicksort.generateFile("testinput-b.txt", "1", 'b');
        assertFalse(fileChecker.checkFile("testinput-b.txt"));
    }


    /**
     * Get code coverage of the class declaration.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testQInit() throws Exception {
        Quicksort tree = new Quicksort();
        assertNotNull(tree);
        Quicksort.main(null);
        fail();
    }



    /**
     * Test the main quicksort class.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSort() throws Exception {
        String[] args = new String[3];
        args[0] = "testinput.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort tree = new Quicksort();
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("testinput.txt"));
    }
}
