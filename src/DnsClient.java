import java.net.DatagramSocket;
import java.net.*;

public class DnsClient {
	public static int index;
	public static void main(String[] args) throws Exception {
		
		InputDecoder myInput = new InputDecoder(args);
		myInput.printConfig();
		Query myQuery = new Query(myInput.getURL(), myInput.getType());
		myQuery.formRequestQuery();
		
		//Create a UDP socket
		DatagramSocket clientSocket = new DatagramSocket();
		
		// Allocate buffers for the data to be sent and received
		byte[] sendData = myQuery.getQuery();
		byte[] receiveData = new byte[1024];
		
		String server = myInput.getServer();
		byte[] svcBytes = Utils.stringNumberToBytesArray(server);
		
		InetAddress ipAddress = InetAddress.getByAddress(svcBytes);
		
		// Create a UDP packet to be sent to the server
		// This involves specifying the sender's address and port number
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, myInput.getPort());

		clientSocket.send(sendPacket);
		
		//Set time out 
	    clientSocket.setSoTimeout(myInput.getTimeout());
	    
	    //Create a packet structure to store data sent back by the server
	    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    
	    // Receive data from the server  
	    int AttemptedTries = 0;
	    
	    /*set start timer */
	    long startTime = System.nanoTime();
	    
	    // receive data until reaches max tries
	    while(AttemptedTries< myInput.getRetris()){        
            try {
        	 	clientSocket.receive(receivePacket);
                break;
            }
            catch (SocketTimeoutException e) {
            	//send the packet
        	    clientSocket.send(sendPacket);
        	    //print out time out exception
                System.out.println("Timeout Reached Error!!! " + e);
                AttemptedTries++;
            }
        }
	    if (AttemptedTries >= myInput.getRetris()){
			System.out.println("ERROR: Maximum number of the max retries " + myInput.getRetris() + " exceeded");
			// Close the socket
			clientSocket.close();
			System.exit(0);
		}
	    /* set End time */
	    long endTime = System.nanoTime();
	    
	    /*Compute the duration */
	    double durationInS =((endTime - startTime)/1000000000.);
	    
	    System.out.println("\nReceive Packet Took " +durationInS + " seconds with " +AttemptedTries + " retrie(s).");
	    
	    /* Post Process the reply */
	    byte[] rvcDataBytes = receivePacket.getData();
	    
	    /* Answer Section */
	    Reply myReply = new Reply(rvcDataBytes);
	    if(myReply.getAnsCount()==0){
	    	System.out.println("\nRESULT: NOT FOUND");
	    	clientSocket.close();
			System.exit(0);
	    }
	    
	    /* print out records */
	    System.out.println("\n*** Answer Section : "+myReply.getAnsCount()+" Record(s) ***");
	    
	    index = sendData.length;
	    
	    for(int i= 1; i<=myReply.getAnsCount(); i++){
	    	myReply.getOffsetAfterAnswerName();
	    	myReply.getAnsType();
	    }
	    /*print additional records */
	    System.out.println("\n***Additional  Section : " + myReply.getARCOUNT() + " record(s)***");
	    for(int i=0; i < myReply.getARCOUNT();i++){
	    	myReply.getOffsetAfterAnswerName();
	    	myReply.getAnsType();
	    }
	    /* close the socket*/
	    clientSocket.close();
		System.exit(0);
	    
	    
	}

}
