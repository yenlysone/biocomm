package biocomm.networking;

import java.io.*;
import java.net.*;

public class DesktopActingServer extends Thread{

	//the value to be plot for each channel 0-7
	public static int[] iCurrentADCValue = new int[8];
	
	public static int iCurrentChannel = 0;
	
	public static String sCurrentSensorName = null;
	
	private static String[] aDataSetParser = null;
	
	
	public DesktopActingServer()
	{
		
	}
	
	public void run(){
		
		
		System.out.println("SERVER CONNECTION THREAD STARTED");
		
		try {
			serverConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void serverConnection() throws Exception {

		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[32];
		byte[] sendData = new byte[32];
		
		
		

		while (true) {
			 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
             serverSocket.receive(receivePacket);
             
             String sDataSetTransfer = new String(receivePacket.getData());

             InetAddress IPAddress = receivePacket.getAddress();
             int port = receivePacket.getPort();
             System.out.println("Sender's Socket: "+IPAddress.toString()+ ": "+port);
             
             //Separate Received DataSet
             aDataSetParser = sDataSetTransfer.split("-");
             String sCurrentSensorName = aDataSetParser[0];
             int iCurrentChannel = Integer.parseInt(aDataSetParser[1]);
             String sDigitTransfer = aDataSetParser[2];
             
             // Remove Any Non-Digit
             String sTrimmedDigit = sDigitTransfer.replaceAll("[^0-9.]", "");
             
             //Parse received value as integer
             iCurrentADCValue[iCurrentChannel] = Integer.parseInt(sTrimmedDigit) ;
             
             //Debug
             System.out.println("VERIFY: " + Integer.parseInt(sTrimmedDigit) +" = "+ iCurrentADCValue[iCurrentChannel]);
             System.out.println("DATASET: \nSensor Name: " +sCurrentSensorName
             		+"\nChannel: "+iCurrentChannel
             		+"\nCurrent Value: "+iCurrentADCValue[iCurrentChannel]);
             
             
             String sADCConfirmationResponse = Integer.toString(iCurrentADCValue[iCurrentChannel]);

             
             sendData = sADCConfirmationResponse.getBytes();
             DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, IPAddress, port);
             serverSocket.send(sendPacket);
		}
	}

	public void closeSocket() {

	}

}
