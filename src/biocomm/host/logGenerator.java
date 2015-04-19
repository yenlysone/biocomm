package biocomm.host;



import java.util.Date;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import biocomm.graphics.DynamicPlot;

public class logGenerator {
	

	


	
	public static void addLogEntry(File logFile, String content){
		
		try {
			PrintStream printedOutput = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile, true)));
			
			printedOutput.println(content);
			System.out.println(content);
			printedOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(logFile);
		}
		
	}
	
	

}
