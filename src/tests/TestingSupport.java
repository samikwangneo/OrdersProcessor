package tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;

import javax.swing.JOptionPane;

public class TestingSupport {
	
	/**
	 * Feel free to use the correctResults method while developing your own tests.
	 * Notice that if you define text files with some expected results, the text
	 * files must be named starting with "studentTest" and ending with the .txt extension. 
	 * If you don't name files this way, then the submit server will generate an authorization error.  
	 * @param filename
	 * @param results
	 * @return true if the contents of the file corresponds to those in results
	 */
	public static boolean correctResults(String filename, String results) {
        String officialResults="";
        try {
            BufferedReader fin = new BufferedReader(new FileReader(filename));
           
            String line;
            while ((line = fin.readLine()) != null) {
                officialResults += line + "\n";
            }
            
            fin.close();
        }catch (IOException e) {
            System.out.println("File opening failed.");
            return false;
        } 
        
        results = removeBlanks(results);
        officialResults = removeBlanks(officialResults);
        
        if (results.equals(officialResults)) {
            return true;
        }
        
        return false;
    }
	
	public static boolean sameContents(String firstFile, String secondFile) {
        try {
			if (removeBlanks(fileData(firstFile)).equals(removeBlanks(fileData(secondFile)))) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
        return false;
	}
	
	public static String fileData(String fileName) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		Scanner fileScanner = new Scanner(bufferedReader);

		while (fileScanner.hasNextLine())  
			stringBuffer.append(fileScanner.nextLine());

		fileScanner.close();

		return stringBuffer.toString();
    }
	
	public static String removeBlanks(String src) {
		return normalize(src);
	}
	
    public static String normalize(String in) {
        StringTokenizer st = new StringTokenizer(in);
        String retVal = new String();
        while (st.hasMoreTokens()) {
                retVal += st.nextToken();
        }
        return retVal;
    }
	
	public static boolean writeToFile(String filename, String message) {
		try {
			FileWriter output = new FileWriter(filename);
			output.write(message);
			output.close(); 
			
		} catch(IOException exception) { 
			System.out.println("ERROR: Writing to file " + filename + " failed.");
			return false;
		}
		return true;
	}
	
	/**
	 * Redirects standard input to be fileName 
	 * @param fileName
	 */
	public static void redirectStandardInputTo(String fileName) {
		InputStream myInput = null;
		try {
			myInput = new FileInputStream(fileName);
		}
		catch(FileNotFoundException e) { 
			System.out.println("File not found.");
		}
		System.setIn(myInput);
	}
	
	/**
	 * Resets standard/input output streams to default
	 */
	public static void resetStandardInputOutput() {
		System.setOut(System.out);
		System.setIn(System.in);
	}
	
	/**
	 * Redirects standard output to returned stream
	 * After running a program call toString on stream to
	 * get output. 
	 * @return stream
	 */
	public static ByteArrayOutputStream redirectStandardOutputToByteArrayStream() {
		ByteArrayOutputStream newOutput = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(newOutput);
		System.setOut(printStream);
		return newOutput;
	}
	
	/**
	 * Makes a file copy
	 * 
	 */
	public static boolean copyfile(String sourceFileName, String targetFileName) {
		File sourceFile = new File(sourceFileName);
		
		if (!sourceFile.exists()) {
			System.err.println(sourceFileName + " does not exist.");
			return false;
		}
		
		try {
			InputStream inputStream = new FileInputStream(sourceFileName);
			OutputStream outputStream = new FileOutputStream(targetFileName);

			int n;
			while ((n = inputStream.read()) != -1) {
				outputStream.write(n);
			}

			inputStream.close();
			outputStream.close();
		} catch(Exception e) {	
			System.err.println("In copyfile " + e.getMessage());
			return false;
		}

		return true;
	}
	
	/* Feel free to ignore the following variables and methods */
	/* They are used to set up the submit server */
	private static boolean generateOfficialResultsFlag = false;
	
	/* We use this string to prevent any hardcoding of results. */
	public static final String hardCodingPrevention = "END_OF_TEST";
	
	public static void generateOfficialResults(String resultsFilename, String officialResultsFilename) {
		/* Official use only (we used this to generate official result) */
		if (generateOfficialResultsFlag) {	
			/* Copying results to official results filename */
			if (!TestingSupport.copyfile(resultsFilename, officialResultsFilename)) {
				System.err.println("File copying failed");
			} else {
				JOptionPane.showMessageDialog(null, officialResultsFilename + " created. ");
			}
		}
	}
	
	public static void appendStringToFile(String filename, String stringToAppend) {
		boolean append = true;
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					filename, append));
			bufferedWriter.write(stringToAppend);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
}