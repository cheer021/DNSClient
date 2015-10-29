public class Utils {
	/* convert a byte in a byte array to its bit value*/
	public static int getBitValue(byte[] b, int index) {
		int bitValue = b[index]&00000100;
		return bitValue;
	}
	/* convert the elements in a byte array to an int */
	public static int bytesToInts(byte[] bytesArray, int startIndex, int endIndex){
		int myint = 0;
		int shifts = (endIndex - 1) - startIndex;
		for (int i = startIndex; i < endIndex; i++){
			myint = myint | (bytesArray [i] & 0xFF) << (8*shifts);
			shifts = shifts - 1;
		}
		return myint;
	}
	/* convert a string to a byte array: mainly used for converting url string to byte array*/
	public static byte[] stringToByteArray(String url){
		String [] split = url.split("\\.");
		byte [] bytes = new byte [url.length()+2];
		int offset = 0;
		for (int i = 0; i < split.length; i++){
			bytes [offset] = (byte) split[i].length();
			offset ++;
			for (int j = 0; j < split[i].length(); j ++){
				bytes [offset] = (byte) split[i].charAt(j);
				offset ++;
			}
		}
		bytes [offset] = 0;
		return bytes;
	}
	/* convert a string to byte array where the string is the ip address*/
	public static byte[] stringNumberToBytesArray(String ip){
		String [] split = ip.split("\\.");
		byte [] bytes = new byte [4];
		for (int i = 0; i < split.length ; i++){
			int ipSegment = Integer.parseInt(split[i]);
			bytes[i] = (byte)(ipSegment & 0xFF);
		}
		return bytes;
	}
	/* convert the elements in byte array to string*/
	public static String byteArrayToString(byte [] bytes, int startIndex, int endIndex){
		String s = new String();
		for (int i = startIndex; i < endIndex; i++){
			s += Character.toString((char)(bytes[i] & 0xFF));
		}
		return s;
	}
	
	
}
