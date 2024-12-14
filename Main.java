// 241RDB309 Orests Taskovs 6
// 241RDB087 Andris Andersons 5
// 241RDB193 Kristofers Stūris 11
// 241RDB057 Elīna Nazarova 5

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choiseStr;
        String sourceFile, resultFile, firstFile, secondFile;

        //testing
        //testing();

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


    private static void lz77Compress(byte[] input, FileOutputStream fos) throws IOException {
        int searchBuffSiz = 4096;
        int lookAheadBufferSize = 16;

        for (int i = 0; i < input.length;) {
            int bestOffset = 0;
            int bestLen = 0;
            byte nextByte = 0;
            
            // bufferitis prieks longest matchh
            int searchStart = Math.max(0, i - searchBuffSiz);
            for (int j = searchStart; j < i; j++) {
                int currLen = 0;
                
                // cik ilgi matcho
                while (i + currLen < input.length && 
                       currLen < lookAheadBufferSize && 
                       input[j + currLen] == input[i + currLen]) {
                    currLen++;
                }
                
                // update ja ir garaks match
                if (currLen > bestLen) {
                    bestLen = currLen;
                    bestOffset = i - j;
                    if (i + currLen < input.length) {
                        nextByte = input[i + currLen];
                    }
                }
            }
            
            // token
            if (bestLen > 0) {
                // 16-bit offset
                fos.write((bestOffset >> 8) & 0xFF);  // high byte
                fos.write(bestOffset & 0xFF);         // low byte
                fos.write(bestLen);
                fos.write(nextByte);
                i += bestLen + 1;
            } else {
                // Literal byte
                fos.write(0);  // zero offset (high byte)
                fos.write(0);  // zero offset (low byte)
                fos.write(0);  // zero length
                fos.write(input[i]);
                i++;
            }
        }
    }

    

    public static void comp(String sourceFile, String resultFile) {
        try {
            
            byte[] fileContent = Files.readAllBytes(Paths.get(sourceFile));
            
            
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {

                int originalSize = fileContent.length;
                fos.write((originalSize >> 24) & 0xFF);
                fos.write((originalSize >> 16) & 0xFF);
                fos.write((originalSize >> 8) & 0xFF);
                fos.write(originalSize & 0xFF);
                
                lz77Compress(fileContent, fos);
            }
            
            System.out.println("Compression complete.");
        } catch (IOException e) {
            System.out.println("Compression error: " + e.getMessage());
        }
    }

    private static byte[] lz77Decompress(byte[] compressed) {
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int buffIndex = 0;
        
        for (int i = 0; i < compressed.length; i += 4) {
            int highOffset = compressed[i] & 0xFF;
            int lowOffset = compressed[i + 1] & 0xFF;
            int offset = (highOffset << 8) | lowOffset;
            int length = compressed[i + 2] & 0xFF;
            int nextByte = compressed[i + 3] & 0xFF;
            
            if (offset == 0 && length == 0) {
                decompressed.write(nextByte);
                buff[buffIndex] = (byte)nextByte;
                buffIndex = (buffIndex + 1) % buff.length;
            } else {
                int startPos = (buffIndex - offset + buff.length) % buff.length;
                
                // kope matcvhing sekvenci
                for (int j = 0; j < length; j++) {
                    int copyByte = buff[(startPos + j) % buff.length] & 0xFF;
                    decompressed.write(copyByte);
                    buff[buffIndex] = (byte)copyByte;
                    buffIndex = (buffIndex + 1) % buff.length;
                }
                
                // pievieno byte
                decompressed.write(nextByte);
                buff[buffIndex] = (byte)nextByte;
                buffIndex = (buffIndex + 1) % buff.length;
            }
        }
        
        return decompressed.toByteArray();
    }

    public static void decomp(String sourceFile, String resultFile) {
        try (FileInputStream fin = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(resultFile)) {
            
            int originalSize = 
                (fin.read() << 24) | 
                (fin.read() << 16) | 
                (fin.read() << 8) | 
                fin.read();
            
            byte[] compCont = fin.readAllBytes();
            
            byte[] decompCont = lz77Decompress(compCont);
            
            // Lai nav extra char
            if (decompCont.length > originalSize) {
                fos.write(decompCont, 0, originalSize);
            } else {
                fos.write(decompCont);
            }
            
            System.out.println("Decompression complete.");
        } catch (IOException e) {
            System.out.println("Decompression error: " + e.getMessage());
        }
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
        System.out.println("241RDB309 Orests Taskovs 6");
        System.out.println("241RDB087 Andris Andersons 5");
        System.out.println("241RDB193 Kristofers Stūris 11");
        System.out.println("241RDB057 Elīna Nazarova 5");
    }
}

