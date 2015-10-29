
public class HeaderQuestion {
	private int HEADER_SIZE=12;
	public byte[] formRequestHeader(){
		byte[] header = new byte[HEADER_SIZE];
		/*set ID */
		header[0] = 0;// since it needs random number so just give it all zeros
		header[1] = 0;
		/* set the first half flag: QR Opcode AA TC RD */
		header[2] = 1;
		/* set the second half flag: RA Z RCODE where RD = 1 and Z = 0  */
		header[3] = 0;
		header[4] = 0; //QDCount (2 Bytes)
		header[5] = 1;
		header[6] = 0; //ANCount (2 Bytes)
		header[7] = 0;
		header[8] = 0; //NSCount (2 Bytes)
		header[9] = 0;
		header[10] = 0; //ARCount (2 Bytes)
		header[11] = 0; 
		return header;
	}
	public byte[] formRequestQuestion(byte[] urlNameBytes, String qType){
		byte[] QName = urlNameBytes;
		// Create and format the QType portion of question
		byte[] QType = new byte[2];
		if (qType.equalsIgnoreCase("A")){
			QType[0] = 0;
			QType[1] = 1;
		} 
		else if (qType.equalsIgnoreCase("NS")){
			QType[0] = 0;
			QType[1] = 2;
		} 
		else if (qType.equalsIgnoreCase("MX")){
			// 15 mail exchange
			QType[0] = 0;
			QType[1] = (byte) (15 & 0xFF);
		}
		
		// Create QClass
		byte[] QClass = {0,1};
		
		//Create Question byte array
		byte[] Question = new byte[QName.length+QType.length+QClass.length];
		int qIndex = 0;
		//Fill in Question with QName, QType, QClass
		for(int i=0; i < QName.length; i++){
			Question[qIndex] = QName[i];
			qIndex++;
		}
		for(int i=0; i<QType.length; i++){
			Question[qIndex] = QType[i];
			qIndex++;
		}
		for(int i=0; i<QClass.length; i++){
			Question[qIndex]= QClass[i];
			qIndex++;
		}
		return Question;
	}
}
