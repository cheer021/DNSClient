
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
public class Query {
	
	private String queryType;
	private int timeout;
	private int retries;
	private int port;
	private short QClass;
	private String url;
	private String server;
	private byte[] sendData;
	
	public Query(String[] args){
		queryType = "A";
		timeout = 5;
		port = 53;
		retries =3;
		QClass = 1;
		inputDecoder myInput = new inputDecoder(args);
		if(myInput.validateInput()){
			server = myInput.getServer();
			url = myInput.getURL();
		}
		else{
			System.exit(0);
		}
		
	}
	public void formRequestQuery(){
		String[] labels = url.split("\\.");
		int numLabels = labels.length;
		int numChars = 0;
		for(String s : labels){
			numChars += s.length();
		}
		
		// Allocate buffers for the data to be sent and received
		sendData = new byte[22 + numChars];	
		
		ByteBuffer bf = ByteBuffer.wrap(sendData);
		
		//generate request Header
		header myHeader = new header();
		bf = myHeader.formRequestHeader(bf);
		for(int i =0; i<numLabels; i++){
			// Write length in first half byte
			bf.put((byte) labels[i].length());
			
			// Write each byte of label
			byte[] data = labels[i].getBytes(StandardCharsets.UTF_8);
			for(byte b : data){
				bf.put(b);
			}
		}
		
		short QType;
		switch(queryType){
			case "NS":
				QType = 2;
				break;
			case "MX":
				QType = 15;
				break;
			default:
				QType = 1;
		}
		
		bf.putShort(QType).putShort(QClass);
	}
	
	public byte[] getQuery(){
		return sendData;
	}
	// Print byte array in Hex
	public void printHexQuery() {
		if(sendData.length == 0){
			System.out.println("No query exists");
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : sendData) {
			sb.append(String.format("%02X ", b));
		}
		System.out.println(sb.toString());
	}
	// Print configuration settings supplied by user
	public void printConfig(){
		System.out.println("Query Type: " + this.queryType);
		System.out.println("Timeout Interval: " + this.timeout);
		System.out.println("Number of Retries: " + this.retries);
		System.out.println("Port Number: " + this.port);
		System.out.println("Server Address: " + server);
		System.out.println("Lookup Name: " + url);
	}
	

}
