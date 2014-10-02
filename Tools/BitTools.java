package Tools;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import Encryption.Decryptor;

public class BitTools {
	
	public static void printPacket(ByteBuffer buf) {
		byte[] decrypted = new byte[buf.get(0)-8];
		buf.get(decrypted, 8, decrypted.length);
		decrypted = Decryptor.Decrypt(decrypted);
		for(int i=0;i<decrypted.length;i++) {
			System.out.printf("%02x ", (decrypted[i]&0xFF));
		}
	}
	
	public static int byteToInt(byte b) {
		return (int) b & 0xFF;
	}
		
	public static float byteArrayToFloat(byte[] b) {
		return Float.intBitsToFloat(((b[0]&0xff)<<24) | ((b[1]&0xff)<<16) | ((b[2]&0xff)<<8) | (b[3]&0xff));
	}
		
	public static int byteArrayToInt(byte[] b) {
		return (int)((b[3]&0xff)<<24) | ((b[2]&0xff)<<16) | ((b[1]&0xff)<<8) | (b[0]&0xff);
	}
	
	public static long byteArrayToLong(byte[] b) {
		long value = 0;
		for(int i = 0; i<b.length;i++) {
			value += ((long) b[i] & 0xffL) << (8 * i);
		}
		return value;
	}
		
		
	public static byte[] intToByteArray(int var) {
		return new byte[] {
					(byte)var,
					(byte)(var >>> 8),
					(byte)(var >>> 16),
	                (byte)(var >>> 24)
	    };           
	}
	
	public static byte[] longToByteArray(long var) {
		return new byte[] {
					(byte)var,
					(byte)(var >>> 8),
					(byte)(var >>> 16),
	                (byte)(var >>> 24),
	                (byte)(var >>> 32),
	                (byte)(var >>> 40),
	                (byte)(var >>> 48),
	                (byte)(var >>> 56)
	    };           
	}
	
	public static byte[] shortToByteArray(short sval) {
		return new byte[] {
				(byte)(sval & 0xff),
				(byte)((sval >> 8) & 0xff)	
		};
	}
	
	public static byte[] floatToByteArray(float f) {
		return intToByteArray(Float.floatToRawIntBits(f));
	}
	
	public static String stringtoHexString(String arg) {
		return String.format("%x", new BigInteger(arg.getBytes()));
	}
		
	public static byte[] stringToByteArray(String s) {
		return s.getBytes();
	}
	  
	public static String byteArrayToString(byte[] baww) {
		return new String(baww);
	}
}
