package biocomm.host;

import java.io.File;
import java.util.Date;

import org.jfree.ui.RefineryUtilities;

import biocomm.graphics.DynamicPlot;
import biocomm.networking.DesktopActingServer;

/*
 * MAIN CLASS THAT WILL HANDLE THE CALLS
 */

public class BiocommApp {
	
	
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */

	public static void main(final String[] args) {

		//DESCRIPTION HEADER
		System.out.println(" ======================= BIOCOMM PROJECT STARTED =======================");
		System.out.println("TEAM FIRE CODE: Vers 0.1");
		
		//LOGGIN PROCESS START
		
		String currentInputSummaryDisplay = null;
		Date currentDateAndTime = new Date();
		
		currentInputSummaryDisplay = "######################### CURRENT LOGGING DATE : " + currentDateAndTime.toString().toUpperCase() + "##########################";
		logGenerator.addLogEntry(new File("biocomm.log"), currentInputSummaryDisplay);
		
		//MAKE SERVER CONNECTION THREAD
		DesktopActingServer dServerThread = new DesktopActingServer();
		dServerThread.start();
		
		//MAIN GUI START UP THREAD
		final DynamicPlot dynamicPlotGUI = new DynamicPlot("FIRECODE | SENIOR DESIGN");
		dynamicPlotGUI.pack();
	    RefineryUtilities.centerFrameOnScreen(dynamicPlotGUI);
	    dynamicPlotGUI.setVisible(true);
	    
	    int test = (int) Math.random();



	}

}
