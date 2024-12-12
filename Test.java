import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Test {

    public static void tokenMakingTest(String s1, String s2) {
        String SearchBufferTest = s1;   // String SearchBufferTest = "sir sid eastman easily t";
        String LookAheadTest = s2;      // String LookAheadTest = "eases sea sick seals    ";

        int lenghtOfBuffer = SearchBufferTest.length();

        int tokenOffset = 0;
        int tokenLength = 0;
        char tokenChar = ' ';

        String tokenString = "";

        for (int i = 0; i < lenghtOfBuffer; i++) { // iterates through the entire buffer

            String searchBufferChar = SearchBufferTest.substring(i, i + 1);
            String lookAheadChar = LookAheadTest.substring(0, 1);

            if (searchBufferChar.equals(lookAheadChar)) {

                System.out.println("position they are equal at = " + i);

                int k = 0;
                int possibleTokenLength = 0;
                while (SearchBufferTest.substring(i + k, i + k + 1).equals(LookAheadTest.substring(k, k + 1))) { // iterates through the following characters as long as they match in both buffers

                    k++;
                    possibleTokenLength++;
                }
                if (possibleTokenLength > tokenLength) {
                    tokenLength = possibleTokenLength;
                    tokenString = SearchBufferTest.substring(i, i + tokenLength);
                    tokenChar = LookAheadTest.charAt(tokenLength);
                    tokenOffset = lenghtOfBuffer - i;
                }
            }
        }

        System.out.println("Token = ( " + tokenOffset + ", " + tokenLength + ", " + tokenChar + " )");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the file name: ");
        String fileName = scanner.nextLine(); // Read the file name from the user

        try (FileInputStream fin = new FileInputStream(fileName)) {
            System.out.println("File opened successfully!");
            // You can add logic here to process the file contents if needed
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        // Example calls to the token-making function
        tokenMakingTest("sir sid eatman easily t", "eases sea sick seals   ");
        System.out.println("======================================");

        tokenMakingTest(" sid eatman easily teas", "es sea sick seals   ");
        System.out.println("======================================");

        tokenMakingTest("sid eatman easily tease", "s sea sick seals   ");
        System.out.println("======================================");

        tokenMakingTest("id eatman easily teases", " sea sick seals   ");
        System.out.println("======================================");

        tokenMakingTest("d eatman easily teases ", "sea sick seals   ");

        scanner.close();
    }
}
