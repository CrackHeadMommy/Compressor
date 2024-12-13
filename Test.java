import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

    public static void tokenMakingTest(String fileName) {

        /*
        String test = "sir sid eastman easily t";
        byte test1[] = new byte[60];

        test1 = test.getBytes();

        for(byte b : test1){
            System.out.print(b + " ");
        }
        */

        //byte test2[] = {115, 105, 114, 32, 115, 105, 100, 32, 101, 97, 115, 116, 109, 97, 110, 32, 101, 97, 115, 105, 108, 121, 32, 116};

        
        try {
            FileInputStream fin = new FileInputStream(fileName);
            System.out.println("File opened successfully!");

            byte searchBuffer[] = new byte[30000];
            byte lookAheadBuffer[] = new byte[500];

            int lenghtOfSearchBuffer = searchBuffer.length;


            for(int i = 0; i < lookAheadBuffer.length-1; i++){          //fills look ahead buffer
                lookAheadBuffer[i] = (byte)fin.read();
            }



            int tokenCount = 0;



                while(lookAheadBuffer[0] != -1){                                                                                                                                //while(fin.read() != -1){


                short tokenOffset = 0;
                short tokenLength = 0;
                byte tokenByte = ' ';


                for(int i = 0; i < lenghtOfSearchBuffer-1; i++){
                
                    byte searchBufferByte = searchBuffer[i];
                    byte lookAheadBufferByte = lookAheadBuffer[0];
    
    
                    if(searchBufferByte == lookAheadBufferByte){
                        

                        //System.out.println("duplicate found");


                        int k = 0;
                        int potentionalTokenLength = 0;
    
                        while(searchBuffer[i+k] == lookAheadBuffer[k] && i+k < searchBuffer.length - 1){     //&& k < lookAheadBuffer.length - 1                                                                                                            //while(searchBuffer[i+k] == lookAheadBuffer[k] && i+k < 499){
                            k++;
                            potentionalTokenLength++;
                            //System.out.println("duplicate found");
                        }
    
                        if(potentionalTokenLength > tokenLength){
                            tokenLength = (short)potentionalTokenLength;
                            tokenOffset = (short)(lenghtOfSearchBuffer - i);
                            tokenByte = lookAheadBuffer[k];

                            //System.out.println("Test token = ( " + tokenOffset + ", " + tokenLength + ", " + tokenByte + " )");
                        }
    
    
                    }else if (tokenLength == 0){

                        tokenLength = 0;
                        tokenOffset = 0;
                        tokenByte = lookAheadBuffer[0];                                         //tokenByte = lookAheadBuffer[1];
                    }
    
    
    
    
    
    
                }
    
                tokenCount++;
                System.out.println("Token = ( " + tokenOffset + ", " + tokenLength + ", " + tokenByte + " )");
                


                //sliding window here
                if(tokenLength == 0){
                    for(int i = 0; i < searchBuffer.length - 1; i++){
                        searchBuffer[i] = searchBuffer[i+1]; 
                    }
                    searchBuffer[searchBuffer.length-1] = lookAheadBuffer[0];
    
                    for(int i = 0; i < lookAheadBuffer.length - 1; i++){
                        lookAheadBuffer[i] = lookAheadBuffer[i+1]; 
                    }
                    lookAheadBuffer[lookAheadBuffer.length - 1] = (byte)fin.read();
                }else{
                    for(int k = 0; k < tokenLength+1; k++){
                        for(int i = 0; i < searchBuffer.length - 1; i++){
                            searchBuffer[i] = searchBuffer[i+1]; 
                        }
                        searchBuffer[searchBuffer.length-1] = lookAheadBuffer[0];
        
                        for(int i = 0; i < lookAheadBuffer.length - 1; i++){
                            lookAheadBuffer[i] = lookAheadBuffer[i+1]; 
                        }
                        lookAheadBuffer[lookAheadBuffer.length - 1] = (byte)fin.read();
                    }
                }
            }

            System.out.println("tokenCount = " + tokenCount);
            


            fin.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        
    }

    public static void main(String[] args) {
        
        tokenMakingTest("File1.html");

    }
}
