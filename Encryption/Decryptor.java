package Encryption;

/**
 * 
 * Decrypts packets sent by client
 *
 */


public class Decryptor {
    public static byte[] Decrypt(byte[] buffer)
    { 
          
    byte DL = 0x00;
    byte cl;
    
    short vsize = (short)(buffer.length);

    for (int i = 1; i <= 3; i++)
    {
        cl = 1;
     for (int j = 0; j < vsize; j++)
	 {
        DL = (byte)((int)cAL(buffer, j, 2, vsize) ^ (int)cAL(buffer, j + 1, 2, vsize));	
		DL -= cl;
        DL = (byte)ror((int)(DL&0xFF), 4);
        // DL = (int)Integer.rotateRight((int)DL, 4);
		buffer[j] = DL;
		cl++;
	 }
        cl = 1;
     for (int j = vsize -1; j >= 0; j--)
	 {
        DL = (byte)((int)cAL(buffer, j, 1, vsize) ^ (int)cAL(buffer, j - 1, 1, vsize));
		DL -= cl;
        DL = (byte)ror((int)(DL&0xFF), 3);
        // DL = (int)Integer.rotateRight((int)DL, 3);
		buffer[j] = DL;
		cl++;
	 }
    }
     return buffer;
    }


    public static byte cAL(byte[] buffer, int n, int rt, short vsize)
    {
     if ((n == vsize) || (n < 0)) return 0x00;
     byte DL = buffer[n];

        if (rt == 1)
        {
	        DL -= 0x48;
	        DL = (byte)~DL;
            DL = (byte)rol((int)(DL&0xFF), 1);
            // DL = (int)Integer.rotateLeft((int)DL, 1);  
	    return DL;
        }
        else
        {
            DL = (byte)rol((int)(DL&0xFF), 3);
            // DL = (int)Integer.rotateLeft((int)DL, 3);
	        DL = (byte)(DL ^ 0x13);
	        return DL;
        }

    }
    public  static int rol(int original, int bits)
    {
        int val = (int)original;
        return (int)((val << bits) | (val >> (8 - bits)));
    }

    public static int ror(int original, int bits)
    {
        int val = (int)original;
        return (int)((val >> bits) | (val << (8 - bits)));
    }
}
