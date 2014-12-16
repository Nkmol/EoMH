package Parser;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Parser {

	public static int convertBytesToInteger(ArrayList<Short> shortList, int firstIndex){
		return shortList.get(firstIndex)+shortList.get(firstIndex+1)*256+shortList.get(firstIndex+2)*65536+shortList.get(firstIndex+3)*16777216;
	}
	
	public static int convertBytesToSmall(ArrayList<Short> shortList, int firstIndex){
		return shortList.get(firstIndex)+shortList.get(firstIndex+1)*256;
	}
	
	public static int convertReversedBytesToSmall(ArrayList<Short> shortList, int firstIndex){
		return shortList.get(firstIndex+1)+shortList.get(firstIndex)*256;
	}
	
	public static int convertBytesToByte(ArrayList<Short> shortList, int firstIndex){
		return shortList.get(firstIndex);
	}
	
	public static float convertBytesToFloat(ArrayList<Short> shortList, int firstIndex){
		byte[] bytes={shortList.get(firstIndex).byteValue(),shortList.get(firstIndex+1).byteValue(),shortList.get(firstIndex+2).byteValue(),shortList.get(firstIndex+3).byteValue()};
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	public static int convertBytesToSignedSmall(ArrayList<Short> shortList, int firstIndex){
		int val=shortList.get(firstIndex)+shortList.get(firstIndex+1)*256;
		if(val>32767)
			val-=65536;
		return val;
	}
	
	public static float convertBytesToIntegerDivisor(ArrayList<Short> shortList, int firstIndex, int divisor){
		float val=(float)((shortList.get(firstIndex)+shortList.get(firstIndex+1)*256+shortList.get(firstIndex+2)*65536+shortList.get(firstIndex+3)*16777216))/divisor;
		return val;
	}
	
	public static int writeIntegerString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		int num=shortList.get(firstIndex)+shortList.get(firstIndex+1)*256+shortList.get(firstIndex+2)*65536+shortList.get(firstIndex+3)*16777216;
		string=new Integer(num).toString();
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
		return num;
	}
	
	public static int writeSmallString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		int num=shortList.get(firstIndex)+shortList.get(firstIndex+1)*256;
		string=new Integer(num).toString();
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
		return num;
	}
	
	public static int writeReversedSmallString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		int num=shortList.get(firstIndex+1)+shortList.get(firstIndex)*256;
		string=new Integer(num).toString();
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
		return num;
	}
	
	public static int writeByteString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		int num=shortList.get(firstIndex);
		string=new Integer(shortList.get(firstIndex)).toString();
		string=formatKoreanString(string);
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
		return num;
	}
	
	public static void writeFloatString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		byte[] bytes={shortList.get(firstIndex).byteValue(),shortList.get(firstIndex+1).byteValue(),shortList.get(firstIndex+2).byteValue(),shortList.get(firstIndex+3).byteValue()};
		string=new Float((ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat())).toString();
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
	}
	
	public static void writeString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex, int length){
		String string="";
		short tmp;
		for(int i=0;i<length;i++){
			tmp=shortList.get(firstIndex+i);
			if(tmp==0)
				tmp=32;
			string+=(char)tmp;
		}
		try{
			string=formatKoreanString(string);
			out.write(string);
			out.write(", ");
		}catch(Exception e){}
	}
	
	public static void writeSignedSmallString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex){
		String string;
		String add;
		int val=shortList.get(firstIndex)+shortList.get(firstIndex+1)*256;
		if(val>32767)
			val-=65536;
		string=new Integer(val).toString();
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
	}
	
	public static void writeIntegerDivisorString(BufferedWriter out, ArrayList<Short> shortList, int firstIndex, int divisor){
		String string;
		String add;
		double num=shortList.get(firstIndex)+shortList.get(firstIndex+1)*256+shortList.get(firstIndex+2)*65536+shortList.get(firstIndex+3)*16777216;
		string=Double.toString(num/divisor);
		add="";
		for(int i=string.length();i<10;i++)
			add+=" ";
		try{
			out.write(string+add);
			out.write(", ");
		}catch(Exception e){}
	}
	
	public static String formatKoreanString(String string){
		short shortdata[]=new short[string.length()];
		char chardata[]=new char[string.length()];
		for(int i=0;i<string.length();i++){
			shortdata[i]=(short)string.charAt(i);
			if(shortdata[i]>126 || shortdata[i]<32)
				shortdata[i]=35;
			chardata[i]=(char)shortdata[i];
		}
		return String.copyValueOf(chardata);
	}
	
}
