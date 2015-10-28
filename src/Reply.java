public class Reply {
	private byte[] response;
	private String AA = "auth";
	private String nAA = "nonauth";
	
	Reply(byte[] resBytes){
		this.response = resBytes;
	}
	/* get the number of answers in the header */
	public int getAnsCount(){
		return Utils.bytesToInts(this.response, 6, 8);
	}
	public int getARCOUNT(){
		return  Utils.bytesToInts(this.response, 10, 12);
	}
	public String getAA(){
		System.out.println("Utils.getBitValue(this.response, 2) "+Utils.getBitValue(this.response, 2));
		if(Utils.getBitValue(this.response, 2) == 1){
			return AA;
		}
		return nAA;
	}
	public String getOffsetAfterAnswerName(){
		String name = new String();
		if (((response[DnsClient.index]&0b11000000) >> 6) == 0b11){ // Beginning name is Pointer (compressed data)

			name = getPointer(response, name);

		} else { // Beginning name not compressed
			int labelSize = Utils.bytesToInts(this.response,DnsClient.index,DnsClient.index+1);
			DnsClient.index++; // label size indicator is one byte large
			while (labelSize != 0){
				for (int i = 0; i < labelSize; i++){
					name = name + Utils.byteArrayToString(response,DnsClient.index,DnsClient.index+1);
					DnsClient.index++;// extracted a byte
				}
				name = name + '.';
				if (((response[DnsClient.index]&0b11000000) >> 6) == 0b11){ // Pointer (compressed data)
					name = getPointer(response,name);
					break;
				} else {
					labelSize = Utils.bytesToInts(response,DnsClient.index,DnsClient.index+1);
					DnsClient.index++;
				}
			}	
		}
		return name;
	}
	/* Utilized to extract pointers for compressed DNS packets offset must be correct*/
	public String getPointer (byte [] response, String name){
		
		int pointer = (response[DnsClient.index]&0b00111111) << 8; // Pointer 2 bytes long
		pointer = pointer | (response[DnsClient.index+1]&0xFF);

		int labelSize = Utils.bytesToInts(response,pointer,pointer+1);
		
		// Point to label data (or termination)
		pointer++;
		
		// Extract pointer data
		while (labelSize != 0){
			for (int i = 0; i < labelSize; i++){
				name = name + Utils.byteArrayToString(response,pointer,pointer+1);
				pointer++;
			}
			name = name + '.';
			if (((response[pointer]&0b11000000) >> 6) == 0b11){ // Pointer (compressed data)
				name = getPointerFromPointer(response,name, pointer);
				break;
			} else {
				labelSize = Utils.bytesToInts(response,pointer,pointer+1);
				pointer++;
			}
		}				
		
		// Pointer is two bytes large
		DnsClient.index += 2;
		return name;
	}
	public String getPointerFromPointer (byte [] response, String name, int pointerPrevious){
		
		int pointer = (response[pointerPrevious]&0b00111111) << 8; // Pointer 2 bytes long
		pointer = pointer | (response[pointerPrevious+1]&0xFF);

		int labelSize = Utils.bytesToInts(response,pointer,pointer+1);
		
		// Point to label data (or termination)
		pointer++;
		
		// Extract pointer data
		while (labelSize != 0){
			for (int i = 0; i < labelSize; i++){
				name = name + Utils.byteArrayToString(response,pointer,pointer+1);
				pointer++;
			}
			name = name + '.';
			if (((response[pointer]&0b11000000) >> 6) == 0b11){ // Pointer (compressed data)
				name = getPointerFromPointer(response,name, pointer);
				break;
			} else {
				labelSize = Utils.bytesToInts(response,pointer,pointer+1);
				pointer++;
			}
		}				
		
		return name;
	}
	public void getAnsType(){
		
		int ansType = getType();
		if (ansType == 0x0001){ // A Type RR
			getATypeRecord();
		} else if (ansType == 0x0002){ // NS Type RR
			getNSTypeRecord();
		} else if (ansType == 15){ // MX Type RR
			getMXTypeRecord();
		} else if (ansType == 0x0005){ // CNAME Type RR
			getCNAMETypeRecord();
		} else {
			System.out.println("ERROR\tUnexpected  response. Record Type 0x" + Integer.toHexString(ansType) + " is unrecognized");
		}
	}
	private int getType(){
		DnsClient.index+=2;
		return Utils.bytesToInts(this.response, DnsClient.index-2, DnsClient.index);
		
	}
	private int getTTL(){
		DnsClient.index+=4;
		return Utils.bytesToInts(this.response, DnsClient.index-4, DnsClient.index);
	}
	private int getCLASS(){
		DnsClient.index=DnsClient.index+=2;
		return Utils.bytesToInts(this.response, DnsClient.index-2, DnsClient.index);
	}
	private int getRDLENGTH(){
		DnsClient.index+=2;
		return Utils.bytesToInts(this.response, DnsClient.index-2, DnsClient.index);
	}
	private String getRDATA(){
		DnsClient.index+=4;
		String returnData = (this.response[DnsClient.index-4]&0xFF) + "."+
				(this.response[DnsClient.index-3]&0xFF) + "."+
				(this.response[DnsClient.index-2]&0xFF) +"."+
				(this.response[DnsClient.index-1]&0xFF);
		return returnData;
	}
	private void getATypeRecord(){
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH();//needs it to increment offset
		String rdata =getRDATA();
		System.out.println("IP\t" +rdata + "\t" + ttl + "\t" + getAA());
	}
	private void getCNAMETypeRecord(){
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH();//needs it to increment offset
		String ansData =getOffsetAfterAnswerName();
		System.out.println("CNAME\t" + ansData + "\t" + ttl + "\t" + getAA());
		
	}
	private void getNSTypeRecord(){
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH();//needs it to increment offset
		String ansData =getOffsetAfterAnswerName();
		System.out.println("NS\t" + ansData + "\t" + ttl + "\t" + getAA());
		
	}
	private void getMXTypeRecord(){
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH();//needs it to increment offset
		int prefData = getPrefDataMX();
		String exchangeData = getOffsetAfterAnswerName();
		System.out.println("MX\t" + prefData + "\t" + exchangeData + "\t" + ttl + "\t" + getAA());
	}
	private int getPrefDataMX(){
		DnsClient.index+=2;
		int prefData = Utils.bytesToInts(this.response, DnsClient.index-2, DnsClient.index);
		return prefData;
	}

}
