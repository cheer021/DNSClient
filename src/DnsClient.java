
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.*;

public class DnsClient {

	public static void main(String[] args) throws Exception {
		
		Query myQuery = new Query(args);
		myQuery.formRequestQuery();
		myQuery.printConfig();
		myQuery.printHexQuery();
		//Create a UDP socket
		// (Note, when no port number is specified, the OS will assign an arbitrary one)
		DatagramSocket clientSocket = new DatagramSocket();
		
		// Allocate buffers for the data to be sent and received
		byte[] sendData = myQuery.getQuery();
		byte[] receiveData = new byte[1024];
	
		InetAddress ipAddress = InetAddress.getByName("8.8.8.8");
		
		// Create a UDP packet to be sent to the server
		// This involves specifying the sender's address and port number
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
		
		//send the packet
	    clientSocket.send(sendPacket);
	    
	    //Create a packet structure to store data sent back by the server
	    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    
	    // Receive data from the server
	 	clientSocket.receive(receivePacket);
	 	
	    String rcvd = "rcvd from " + receivePacket.getAddress() + ", " + receivePacket.getPort() + ": "
		          + new String(receivePacket.getData(), 0, receivePacket.getLength());
		System.out.println("From Server: " + rcvd);
		
	    clientSocket.close();
		
	}

}
