import org.junit.Test;
import student.TestCase;

/**
 * @author Yu-Kai Lo
 * @version v 1.0
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
     * Test the main quicksort class with 1 ASCII block and 1 buffer.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortOneAsciiOnebuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "1Test.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("1Test.txt", "1", 'a');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("1Test.txt"));
    }


    /**
     * Test the main quicksort class with 1 Binary block and 1 buffer.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortOneBinOnebuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "1Test-b.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("1Test-b.txt", "1", 'b');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("1Test-b.txt"));
    }


    /**
     * Test the main quicksort class with 10 ASCII blocks and 1 buffer.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortTenAsciiOnebuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "10Test.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("10Test.txt", "10", 'a');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("10Test.txt"));
    }


    /**
     * Test the main quicksort class with 10 Binary blocks and 1 buffer.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortTenBinOnebuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "10Test-b.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("10Test-b.txt", "10", 'b');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("10Test-b.txt"));
    }


    /**
     * Test the main quicksort class with 10 ASCII blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortTenAsciiTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "10Test.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("10Test.txt", "10", 'a');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("10Test.txt"));
    }


    /**
     * Test the main quicksort class with 10 Binary blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortTenBinTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "10Test-b.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("10Test-b.txt", "10", 'b');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("10Test-b.txt"));
    }


    /**
     * Test the main quicksort class with 100 ASCII blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortHundredAsciiTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "100Test.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("100Test.txt", "100", 'a');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("100Test.txt"));
    }


    /**
     * Test the main quicksort class with 100 Binary blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortHundredBinTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "100Test-b.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("100Test-b.txt", "100", 'b');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("100Test-b.txt"));
    }


    /**
     * Test the main quicksort class with 1000 ASCII blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortThousandAsciiTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "1000Test.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("1000Test.txt", "1000", 'a');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("1000Test.txt"));
    }


    /**
     * Test the main quicksort class with 1000 Binary blocks and 10 buffers.
     * 
     * @throws Exception
     */
    @Test
    public void testQuickSortThousandBinTenbuffer() throws Exception {
        String[] args = new String[3];
        args[0] = "1000Test-b.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("1000Test-b.txt", "1000", 'b');
        Quicksort.main(args);
        CheckFile checker = new CheckFile();
        assertTrue(checker.checkFile("1000Test-b.txt"));
    }
}
