

public class inputDecoder {
	public static  int IPPARTS = 4;
	private  String ERRORIP="The ip or server address is not valid";
	private String[] s;
	inputDecoder(String[] input){
		s = input;
	}
	public boolean validateInput(){
		if(s.length<2){
			return false;
		}
		else{
			if(validateIP(s[0])&&validateAddress(s[1])){
				return true;
			}
			else{
				System.out.println(ERRORIP);
				return false;
			}
		}
	}
	
	public  String getServer(){
		return s[0].substring(1);
	}
	public  String getURL(){
		return s[1];
	}
	public static boolean validateIP(String ipAt){
		//the ip address should be like xxx.xxx.xxx 
		//where xxx is an int ranging from 0 to 2555
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
				//Check if the string is numeric
				if(s.matches("[-+]?\\d*\\.?\\d+")){
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
	public static boolean validateAddress(String add){
		return true;
	}

}
