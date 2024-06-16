package tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import junit.framework.TestCase;
import processor.*;

public class PublicTests extends TestCase {
	
	public void testSys1() throws Exception, Throwable {
		runProgramWithInput("pubTest1.txt");
	}
	
	public void testSys2() throws Exception, Throwable {
		runProgramWithInput("pubTest2.txt");
	}

	public void testSys3() throws Exception, Throwable {
		runProgramWithInput("pubTest3.txt");		
	}
	
	public void testSys4() throws Exception, Throwable {
		runProgramWithInput("pubTest4.txt");
	}
	
	public void testSys5() throws Exception, Throwable {
		runProgramWithInput("pubTest5.txt");
	}
	
	/**
	 * Executes a run of the OrdersProcessor program by reading the data
	 * in the specified file using input redirection.  The file inputFileName
	 * has the item's data file, whether multiple threads will be used,
	 * number of orders, base file name for the orders, and the 
	 * result file name.
	 * 
	 * @param inputFilename
	 * @throws Exception
	 * @throws Throwable
	 */
	private void runProgramWithInput(String inputFilename) throws Exception, Throwable {
		
		/* Retrieving the name of the results file */
		String resultsFilename = getResultsFilename(inputFilename);
		String officialResultsFilename = resultsFilename + ".expected.txt";
		
		/* Deleting results file (in case it exists) */
		File file = new File(resultsFilename);
		file.delete();

		/* Actual execution of the test by using input redirection and calling 
		/* OrdersProcessor.main(null) */
		TestingSupport.redirectStandardInputTo(inputFilename);
		OrdersProcessor.main(null);

		/* Adding a specified string to results to avoid hard coding of results */
		TestingSupport.appendStringToFile(resultsFilename, TestingSupport.hardCodingPrevention);
		
		/* Ignore this method call. We use it to generate results for the submit server */
		TestingSupport.generateOfficialResults(resultsFilename, officialResultsFilename);
		
		/* Checking if we got the right results */
		assertTrue(TestingSupport.sameContents(resultsFilename, officialResultsFilename));
	}
		
	/* Retrieves the name of the file to be used to store results */
	private static String getResultsFilename(String filename) throws FileNotFoundException {
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)));
		for (int i = 1; i <= 4; i++) { // Throwing away first four items
			scanner.next();
		}
		String name = scanner.next();
		scanner.close();
		
		return name;
	}

	/* This space available for rent */
	/* When I grow up I want to be a Ruby program */
}