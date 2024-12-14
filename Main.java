// 241RDB309 Orests Taskovs 6
// 241RDB087 Andris Andersons 5
// 241RDB193 Kristofers Stūris 11
// 241RDB057 Elīna Nazarova 5

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public static void testing (){

        try {
            FileInputStream fin = new FileInputStream("testing3.html");

            for(int i = 0; i < 40; i++){
                System.out.println(fin.read());
            }



            fin.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

    }


    private static void lz77Compress(byte[] input, FileOutputStream fos) throws IOException {
        int searchBufferSize = 4096;
        int lookAheadBufferSize = 16;

        for (int i = 0; i < input.length;) {
            int bestOffset = 0;
            int bestLength = 0;
            byte nextByte = 0;
            
            // Search in the search buffer for the longest match
            int searchStart = Math.max(0, i - searchBufferSize);
            for (int j = searchStart; j < i; j++) {
                int currentLength = 0;
                
                // Check how long the match continues
                while (i + currentLength < input.length && 
                       currentLength < lookAheadBufferSize && 
                       input[j + currentLength] == input[i + currentLength]) {
                    currentLength++;
                }
                
                // Update best match if current match is longer
                if (currentLength > bestLength) {
                    bestLength = currentLength;
                    bestOffset = i - j;
                    if (i + currentLength < input.length) {
                        nextByte = input[i + currentLength];
                    }
                }
            }
            
            // Write token
            if (bestLength > 0) {
                // Write 16-bit offset
                fos.write((bestOffset >> 8) & 0xFF);  // high byte
                fos.write(bestOffset & 0xFF);         // low byte
                fos.write(bestLength);
                fos.write(nextByte);
                i += bestLength + 1;
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
            // Read entire file content
            byte[] fileContent = Files.readAllBytes(Paths.get(sourceFile));
            
            // Open output stream
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {
                // Write original file size to help with potential truncation
                int originalSize = fileContent.length;
                fos.write((originalSize >> 24) & 0xFF);
                fos.write((originalSize >> 16) & 0xFF);
                fos.write((originalSize >> 8) & 0xFF);
                fos.write(originalSize & 0xFF);
                
                // Perform compression
                lz77Compress(fileContent, fos);
            }
            
            System.out.println("Compression complete.");
        } catch (IOException e) {
            System.out.println("Compression error: " + e.getMessage());
        }
    }

    private static byte[] lz77Decompress(byte[] compressed) {
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bufferIndex = 0;
        
        for (int i = 0; i < compressed.length; i += 4) {
            // Extract token components
            int highOffset = compressed[i] & 0xFF;
            int lowOffset = compressed[i + 1] & 0xFF;
            int offset = (highOffset << 8) | lowOffset;
            int length = compressed[i + 2] & 0xFF;
            int nextByte = compressed[i + 3] & 0xFF;
            
            if (offset == 0 && length == 0) {
                // Literal byte
                decompressed.write(nextByte);
                buffer[bufferIndex] = (byte)nextByte;
                bufferIndex = (bufferIndex + 1) % buffer.length;
            } else {
                // Back-reference
                int startPos = (bufferIndex - offset + buffer.length) % buffer.length;
                
                // Copy matching sequence
                for (int j = 0; j < length; j++) {
                    int copyByte = buffer[(startPos + j) % buffer.length] & 0xFF;
                    decompressed.write(copyByte);
                    buffer[bufferIndex] = (byte)copyByte;
                    bufferIndex = (bufferIndex + 1) % buffer.length;
                }
                
                // Add next byte
                decompressed.write(nextByte);
                buffer[bufferIndex] = (byte)nextByte;
                bufferIndex = (bufferIndex + 1) % buffer.length;
            }
        }
        
        return decompressed.toByteArray();
    }

    public static void decomp(String sourceFile, String resultFile) {
        try (FileInputStream fin = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(resultFile)) {
            
            // Read original file size
            int originalSize = 
                (fin.read() << 24) | 
                (fin.read() << 16) | 
                (fin.read() << 8) | 
                fin.read();
            
            // Read compressed content
            byte[] compressedContent = fin.readAllBytes();
            
            // Decompress
            byte[] decompressedContent = lz77Decompress(compressedContent);
            
            // Trim to original size to handle potential extra characters
            if (decompressedContent.length > originalSize) {
                fos.write(decompressedContent, 0, originalSize);
            } else {
                fos.write(decompressedContent);
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
        // insert information about authors
        System.out.println("241RDB309 Orests Taskovs 6");
        System.out.println("241RDB087 Andris Andersons 5");
        System.out.println("241RDB193 Kristofers Stūris 11");
        System.out.println("241RDB057 Elīna Nazarova 5");
    }
}

