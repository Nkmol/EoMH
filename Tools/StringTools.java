package Tools;

import java.util.regex.Pattern;

public class StringTools {
	
	public static String HexStringToString(String s){		 
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
	 	 
		for(int i=0; i<s.length()-1; i+=2 ){
			String output = s.substring(i, (i + 2));
		    int decimal = Integer.parseInt(output, (16&0xFF));
		    sb.append((char)decimal); 
		    temp.append(decimal);
		}
	 
		return sb.toString();
	}
	
	public static boolean isInteger(String s) {
		return Pattern.matches("^\\d*$", s);
	}
	
}
