// 241RDB309 Orests Taskovs 6
// 241RDB087 Andris Andersons 5
// 241RDB193 Kristofers Stūris 11
// 241RDB057 Elīna Nazarova 5

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choiseStr;
        String sourceFile, resultFile, firstFile, secondFile;

        loop: while (true) {

            choiseStr = sc.next();

            switch (choiseStr) {
                case "comp":
                    System.out.print("source file name: ");
                    sourceFile = sc.next();
                    System.out.print("archive name: ");
                    resultFile = sc.next();
                    comp(sourceFile, resultFile);
                    break;
                case "decomp":
                    System.out.print("archive name: ");
                    sourceFile = sc.next();
                    System.out.print("file name: ");
                    resultFile = sc.next();
                    decomp(sourceFile, resultFile);
                    break;
                case "size":
                    System.out.print("file name: ");
                    sourceFile = sc.next();
                    size(sourceFile);
                    break;
                case "equal":
                    System.out.print("first file name: ");
                    firstFile = sc.next();
                    System.out.print("second file name: ");
                    secondFile = sc.next();
                    System.out.println(equal(firstFile, secondFile));
                    break;
                case "about":
                    about();
                    break;
                case "exit":
                    break loop;
            }
        }

        sc.close();
    }


    public static void LZ77 (String sourceFile, String resultFile){
        
        try {
            FileInputStream fin = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(resultFile);

            System.out.println("File opened successfully!");

            byte searchBuffer[] = new byte[30000];
            byte lookAheadBuffer[] = new byte[500];

            int lenghtOfSearchBuffer = searchBuffer.length;


            for(int i = 0; i < lookAheadBuffer.length-1; i++){          //fills look ahead buffer
                lookAheadBuffer[i] = (byte)fin.read();
            }


            while(lookAheadBuffer[0] != -1){

                short tokenOffset = 0;
                short tokenLength = 0;
                byte tokenByte = ' ';

                for(int i = 0; i < lenghtOfSearchBuffer-1; i++){
                
                    byte searchBufferByte = searchBuffer[i];
                    byte lookAheadBufferByte = lookAheadBuffer[0];
    
    
                    if(searchBufferByte == lookAheadBufferByte){

                        int k = 0;
                        int potentionalTokenLength = 0;
    
                        while(searchBuffer[i+k] == lookAheadBuffer[k] && i+k < searchBuffer.length - 1){
                            k++;
                            potentionalTokenLength++;
                        }
    
                        if(potentionalTokenLength > tokenLength){
                            tokenLength = (short)potentionalTokenLength;
                            tokenOffset = (short)(lenghtOfSearchBuffer - i);
                            tokenByte = lookAheadBuffer[k];
                        }
    
    
                    }else if (tokenLength == 0){
                        tokenLength = 0;
                        tokenOffset = 0;
                        tokenByte = lookAheadBuffer[0];
                    }
    
                }
    

                //write the token into a new file
                fos.write(tokenOffset);
                fos.write(' ');
                fos.write(tokenLength);
                fos.write(' ');
                fos.write(tokenByte);
                fos.write(' ');

                
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

            fin.close();
            fos.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }


    public static void comp(String sourceFile, String resultFile) {

        LZ77(sourceFile, resultFile);

        //todo implement huffman here or call it in LZ77 code end idk do as you will

    }

    public static void decomp(String sourceFile, String resultFile) {
        // implement this method
    }

    public static void size(String sourceFile) {
        try {
            FileInputStream f = new FileInputStream(sourceFile);
            System.out.println("size: " + f.available());
            f.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static boolean equal(String firstFile, String secondFile) {
        try {
            FileInputStream f1 = new FileInputStream(firstFile);
            FileInputStream f2 = new FileInputStream(secondFile);
            int k1, k2;
            byte[] buf1 = new byte[1000];
            byte[] buf2 = new byte[1000];
            do {
                k1 = f1.read(buf1);
                k2 = f2.read(buf2);
                if (k1 != k2) {
                    f1.close();
                    f2.close();
                    return false;
                }
                for (int i = 0; i < k1; i++) {
                    if (buf1[i] != buf2[i]) {
                        f1.close();
                        f2.close();
                        return false;
                    }

                }
            } while (!(k1 == -1 && k2 == -1));
            f1.close();
            f2.close();
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static void about() {
        // insert information about authors
        System.out.println("241RDB309 Orests Taskovs 6");
        System.out.println("241RDB087 Andris Andersons 5");
        System.out.println("241RDB193 Kristofers Stūris 11");
        System.out.println("241RDB057 Elīna Nazarova 5");
    }
}
