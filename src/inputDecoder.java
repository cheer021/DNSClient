
import java.util.Arrays;
public class InputDecoder {
	public  int IPPARTS = 4;
	private  String ERRORIP="The ip/server address is not valid";
	private String ERROR_NOTNUM="The arguement contains non-number input: ";
	private String ERROR_NOTPOST="The arguement contains not positive number: ";
	private String[] s;
	private int timeout = 5;
	private String t_pre = "-t";
	private int retries = 3;
	private String r_pre="-r";
	private int port = 53;
	private String p_pre ="-p";
	private String server;
	private String url;
	private String MX_pre = "-mx";
	private String NS_pre = "-ns";
	private String type;
	InputDecoder(String[] input){

		s = input;
		if(validateInputIPServer()){
			server = s[s.length-2].substring(1);
		url =s[s.length-1];
		timeout = this.getTimeout();
		retries = this.getRetris();
		port = this.getPort();
		type = this.getType();
		}
		else{
			System.exit(0);
		}
		
		
	}
	public boolean validateInputIPServer(){
		if(s.length<2){
			return false;
		}
		else{
			if(validateServer(s[s.length-2])
					&&validateURL(s[s.length-1])){
				return true;
			}
			else{
				System.out.println(ERRORIP);
				return false;
			}
		}
	}
	private int getOptionNum(int index){
		if(isNum(s[index])){
			return Integer.parseInt(s[index]);
		}
		return 0;
	}
	private int getIndex(String prefix){
		if(Arrays.asList(s).indexOf(prefix)>-1){
			int index = Arrays.asList(s).indexOf(prefix)+1;
			return index;
		}
		return -1;
	}
	private boolean isNum(String s){
		if(s.matches("[-+]?\\d*\\.?\\d+")){
			if (Float.parseFloat(s)<0)
			{
				System.out.println(ERROR_NOTPOST+s);
				System.exit(0);
			}
			return true;
		}
		else{

			System.out.println(ERROR_NOTNUM+s);
			System.exit(0);
		}
		return false;
	}
	public  String getServer(){
		return server;
	}
	public  String getURL(){
		return url;
	}
	public int getTimeout(){
		if(getIndex(t_pre)>-1){
			timeout = getOptionNum(getIndex(t_pre));
			isNum(timeout+"");
		}
		return timeout;
	}
	public int getRetris(){
		if(getIndex(r_pre)>-1){
			retries = getOptionNum(getIndex(r_pre));
			isNum(retries+"");
		}
		return retries;
	}
	public int getPort(){
		if(getIndex(p_pre)>-1){
			port = getOptionNum(getIndex(p_pre));
			isNum(port+"");
		}
		return port;
	}
	public String getType(){
		if(getIndex(MX_pre)>-1){
			type = "MX";
		}
		else if(getIndex(NS_pre)>-1){
			type = "NS";
		}
		else{
			type = "A";
			}
		return type;
	}
	//the ip address should be like xxx.xxx.xxx 
	//where xxx is an int ranging from 0 to 255
	public  boolean validateServer(String ipAt){
		
		if(!(ipAt.charAt(0)=='@')){
			return false;
		}
		String ip = ipAt.substring(1);
		String[] splitedIp = ip.split("\\.");
		if(splitedIp.length!=IPPARTS){
			return false;
		}
		else{
			for(String s: splitedIp){
				//Check if the string is a numeric that ranges from 0 to 255
				if(isNum(s)){
					if(Integer.parseInt(s) > 255 || Integer.parseInt(s) <0 ){
						return false;
					}
				}
				else{
					return false;
				}
			}
		}
		
		return true;
	}
	public  boolean validateURL(String add){
		//check if it contains dot
		if(add.indexOf('.')>0){
			return true;
		}
		return false;
		
	}
	
	// Print configuration settings supplied by user
		public void printConfig(){
			System.out.println("*** Input Summary ***");
			System.out.println("Query Type: " + getType());
			System.out.println("Timeout Interval in ms: " + getTimeout());
			System.out.println("Number of Retries: " + getRetris());
			System.out.println("Port Number: " + getPort());
			System.out.println("Server Address: " + getServer());
			System.out.println("Lookup Name: " + this.url);
			
		}
}
