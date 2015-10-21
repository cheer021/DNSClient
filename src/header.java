
import java.nio.ByteBuffer;
import java.util.Random;

public class header {
	private short FLAG = (short) 0x0100;
	private short QDCOUNT_REQ = 1;
	private short ANCOUNT_REQ = 0;
	private short NSCOUNT_REQ = 0;
	private short ARCOUNT_REQ = 0;
	
	public ByteBuffer formRequestHeader(ByteBuffer head_buf){

		//this will generate numbers from 0 to Short.MAX_VALUE inclusive
		Random r = new Random();
		short id = (short) r.nextInt(Short.MAX_VALUE + 1);
		
		//put randomly generated 16-bit number ID into the buffer
		head_buf.putShort(id);
		
		//put flag into the buffer
		head_buf.putShort(FLAG);
		
		//put the rest of the shit 
		head_buf.putShort(QDCOUNT_REQ).putShort(ANCOUNT_REQ).putShort(NSCOUNT_REQ).putShort(ARCOUNT_REQ);
		
		//printoutBuffer(head_buf);
		return head_buf;
		
	}
	private void printoutBuffer(ByteBuffer bf){
		int i = 0;
		while(i<30){
			System.out.println(bf.get(i));
			i++;
		}
	}
}
