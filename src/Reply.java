public class Reply {
	private byte[] response;
	private String AA = "auth";
	private String nAA = "nonauth";

	Reply(byte[] resBytes) {
		this.response = resBytes;
	}
	/* get the number of answers in the header */
	public int getAnsCount() {
		return Utils.bytesToInts(this.response, 6, 8);
	}
	public int getARCOUNT() {
		return Utils.bytesToInts(this.response, 10, 12);
	}
	/* return a string that indicates if it is AA */
	public String getAA() {
		//System.out.println("Utils.getBitValue(this.response, 2) "+Utils.getBitValue(this.response, 2));
		if (Utils.getBitValue(this.response, 2) == 1) {
			return AA;
		}
		return nAA;
	}
	public String getOffsetAfterAnswerName() {
		String hostname = new String();
		//the name is compressed
		if (((response[DnsClient.index] & 0b11000000) >> 6) == 0b11) {
			hostname = getPointer(response, hostname);
		}
		//the name is not compressed
		else {
			int labelSize = Utils.bytesToInts(this.response, DnsClient.index, DnsClient.index + 1);
			//Increment the index because the label size is one byte long
			DnsClient.index++;
			while (labelSize != 0) {
				for (int i = 0; i < labelSize; i++) {
					hostname = hostname + Utils.byteArrayToString(response, DnsClient.index, DnsClient.index + 1);
					DnsClient.index++; 
				}
				hostname = hostname + '.';
				if (((response[DnsClient.index] & 0b11000000) >> 6) == 0b11) { 
					hostname = getPointer(response, hostname);
					break;
				} else {
					labelSize = Utils.bytesToInts(response, DnsClient.index, DnsClient.index + 1);
					DnsClient.index++;
				}
			}
		}
		return hostname;
	}
	/* extract the pointer */
	public String getPointer(byte[] response, String hostname) {
		// Shift by 8 because point is 2 bytes long
		int pointer = (response[DnsClient.index] & 0b00111111) << 8; 
		pointer = pointer | (response[DnsClient.index + 1] & 0xFF);

		int labelSize = Utils.bytesToInts(response, pointer, pointer + 1);
		pointer++;
		// Extract pointer data
		while (labelSize != 0) {
			for (int i = 0; i < labelSize; i++) {
				hostname = hostname + Utils.byteArrayToString(response, pointer, pointer + 1);
				pointer++;
			}
			hostname = hostname + '.';
			if (((response[pointer] & 0b11000000) >> 6) == 0b11) { 
				hostname = getPointerFromPointer(response, hostname, pointer);
				break;
			} else {
				labelSize = Utils.bytesToInts(response, pointer, pointer + 1);
				pointer++;
			}
		}

		// Pointer is two bytes large
		DnsClient.index += 2;
		return hostname;
	}
	public String getPointerFromPointer(byte[] response, String hostname, int pointerPrevious) {
		// Shift by 8 because point is 2 bytes long
		int pointer = (response[pointerPrevious] & 0b00111111) << 8; 
		pointer = pointer | (response[pointerPrevious + 1] & 0xFF);

		int labelSize = Utils.bytesToInts(response, pointer, pointer + 1);
		pointer++;

		// Extract pointer data
		while (labelSize != 0) {
			for (int i = 0; i < labelSize; i++) {
				hostname = hostname + Utils.byteArrayToString(response, pointer, pointer + 1);
				pointer++;
			}
			hostname = hostname + '.';
			if (((response[pointer] & 0b11000000) >> 6) == 0b11) { // Pointer (compressed data)
				hostname = getPointerFromPointer(response, hostname, pointer);
				break;
			} else {
				labelSize = Utils.bytesToInts(response, pointer, pointer + 1);
				pointer++;
			}
		}

		return hostname;
	}
	public void getAnsType() {

		int ansType = getType();
		// A Type
		if (ansType == 0x0001) { 
			getATypeRecord();
		} 
		 // NS Type
		else if (ansType == 0x0002) {
			getNSTypeRecord();
		} 
		// MX Type
		else if (ansType == 15) { 
			getMXTypeRecord();
		} 
		// CNAME Type
		else if (ansType == 0x0005) { 
			getCNAMETypeRecord();
		}
		//ERROR case where the type is not recognized 
		else {
			System.out.println("ERROR: Unexpected  response. Type " + Integer.toHexString(ansType) + " is unrecognized");
		}
	}
	private int getType() {
		DnsClient.index += 2;
		return Utils.bytesToInts(this.response, DnsClient.index - 2, DnsClient.index);

	}
	private int getTTL() {
		DnsClient.index += 4;
		return Utils.bytesToInts(this.response, DnsClient.index - 4, DnsClient.index);
	}
	private int getCLASS() {
		DnsClient.index = DnsClient.index += 2;
		return Utils.bytesToInts(this.response, DnsClient.index - 2, DnsClient.index);
	}
	private int getRDLENGTH() {
		DnsClient.index += 2;
		return Utils.bytesToInts(this.response, DnsClient.index - 2, DnsClient.index);
	}
	private String getRDATA() {
		DnsClient.index += 4;
		String returnData = (this.response[DnsClient.index - 4] & 0xFF) + "." + (this.response[DnsClient.index - 3] & 0xFF) + "." + (this.response[DnsClient.index - 2] & 0xFF) + "." + (this.response[DnsClient.index - 1] & 0xFF);
		return returnData;
	}
	private void getATypeRecord() {
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH(); //needs it to increment offset
		String rdata = getRDATA();
		System.out.println("IP\t" + rdata + "\t" + ttl + "\t" + getAA());
	}
	private void getCNAMETypeRecord() {
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH(); //needs it to increment offset
		String ansData = getOffsetAfterAnswerName();
		System.out.println("CNAME\t" + ansData + "\t" + ttl + "\t" + getAA());

	}
	private void getNSTypeRecord() {
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH(); //needs it to increment offset
		String ansData = getOffsetAfterAnswerName();
		System.out.println("NS\t" + ansData + "\t" + ttl + "\t" + getAA());

	}
	private void getMXTypeRecord() {
		getCLASS(); //Needs it to increment offset
		int ttl = getTTL();
		getRDLENGTH(); //needs it to increment offset
		int prefData = getPrefDataMX();
		String exchangeData = getOffsetAfterAnswerName();
		System.out.println("MX\t" + prefData + "\t" + exchangeData + "\t" + ttl + "\t" + getAA());
	}
	private int getPrefDataMX() {
		DnsClient.index += 2;
		int prefData = Utils.bytesToInts(this.response, DnsClient.index - 2, DnsClient.index);
		return prefData;
	}

}