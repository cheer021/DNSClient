
public class Query {

	private String url;
	private byte[] sendData;
	private String type;
	public Query(String u, String qType){
		this.url = u;
		this.type = qType;
	}
	public void formRequestQuery(){
		//generate request Header
		HeaderQuestion myHQ = new HeaderQuestion();
		byte[] myReqHeader = myHQ.formRequestHeader();
		
		byte[] myStringToByte = Utils.stringToByteArray(url);
		
		byte[] myReqQuestion = myHQ.formRequestQuestion(myStringToByte, this.type);
		
		// Allocate buffers for the data to be sent
		sendData = new byte[myReqHeader.length + myReqQuestion.length];	
		
		for (int i = 0; i < sendData.length; ++i)
		{
			sendData[i] = i < myReqHeader.length ? myReqHeader[i] : myReqQuestion[i - myReqHeader.length];
		}
	}
	
	public byte[] getQuery(){
		return this.sendData;
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
	

}
