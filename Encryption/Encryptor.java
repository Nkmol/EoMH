package Encryption;

/**
 * 
 * This class is not required since packets sent by server are not encrypted.
 * Client uses this routine to encrypt it's packets(except login info)
 *
 */

public class Encryptor {
    public static byte[] Encrypt(byte[] buffer)
    {
         
    	byte var,ch,cl;
        short LENGTH = (short)buffer.length;
        
        
        for (int iter = 0; iter < 3; iter++) //encryption requires 6 iterations
        {
            var = 0x00; //AL = 0
            cl = (byte)LENGTH;
            
            
            
            
            
            for (int i = 0; i < LENGTH; i++)
            {
                
                ch = buffer[i];             //MOV DL,ESI 
                ch = (byte)rol(ch&0xFF, 3); //ROL DL,3 
                ch += cl;                   //ADD DL,CL           
                ch = (byte)(ch ^ var);      //XOR DL,AL           
                var = ch;                   //MOV AL,DL           
                ch = (byte)ror(ch&0xFF, 1); //ROR DL,1                  
                ch = (byte)~ch;             //NOT DL             
                ch += 0x48;                 //ADD DL,48           
                buffer[i] = ch;             //MOV ESI,DL
                cl--;                       //DEC CL
                
            }
            
            var = 0x00;						//AL = 00
            cl = (byte)LENGTH;
            
            
            //every second time the encryption routine is slightly different
            
            for (int i = (LENGTH - 1); i >= 0; i--)
            {

                ch = buffer[i];             	//MOV DL,ESI                    
                ch = (byte)rol((ch&0xFF), 4); 	//ROL DL,4
                ch += cl;                   	//ADD DL,CL
                ch = (byte)(ch ^ var);      	//XOR DL,AL                                
                var = ch;                   	//MOV AL,DL                    
                ch = (byte)(ch ^ 0x13);     	//XOR DL,13                
                ch = (byte)ror(ch&0xFF, 3); 	//ROR DL,3                 
                buffer[i] = ch;             	//MOV ESI,DL
                cl--;                       	//DEC CL
                
            }
            
            
        }
        
        return buffer;
    }


    
    public  static int rol(int original, int bits)
    {
        int val = original;
        return ((val << bits) | (val >> (8 - bits)));
    }

    public static int ror(int original, int bits)
    {
        int val = original;
        return ((val >> bits) | (val << (8 - bits)));
    }
}
