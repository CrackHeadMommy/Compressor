// 241RDB309 Orests Taskovs 6
// 241RDB087 Andris Andersons 5
// 241RDB193 Kristofers Stūris 11
// 241RDB057 Elīna Nazarova 5

import java.io.*;
import java.util.*;

public class Huffman {

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

    public static void comp(String sourceFile, String resultFile) {
        try {
            // Step 1: Read input file
            StringBuilder input = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                int b;
                while ((b = fis.read()) != -1) {
                    input.append((char) b);
                }
            }

            // Step 2: Calculate frequencies
            Map<Character, Integer> freqMap = new HashMap<>();
            for (char ch : input.toString().toCharArray()) {
                freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
            }

            // Step 3: Build Huffman Tree
            Node root = buildHuffmanTree(freqMap);

            // Step 4: Generate Huffman Codes
            Map<Character, String> huffmanCodes = new HashMap<>();
            generateHuffmanCodes(root, "", huffmanCodes);

            // Step 5: Compress the file
            StringBuilder compressedData = new StringBuilder();
            for (char ch : input.toString().toCharArray()) {
                compressedData.append(huffmanCodes.get(ch));
            }

            // Step 6: Save the Huffman tree and compressed data
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resultFile))) {
                oos.writeObject(freqMap); // Save frequency map
                oos.writeUTF(compressedData.toString()); // Save compressed binary data
            }

            System.out.println("File compressed successfully!");
        } catch (IOException e) {
            System.out.println("Error during compression: " + e.getMessage());
        }
    }

    public static void decomp(String sourceFile, String resultFile) {
        try {
            // Step 1: Read the compressed file
            Map<Character, Integer> freqMap;
            String compressedData;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sourceFile))) {
                freqMap = (Map<Character, Integer>) ois.readObject();
                compressedData = ois.readUTF();
            }

            // Step 2: Rebuild Huffman Tree
            Node root = buildHuffmanTree(freqMap);

            // Step 3: Decompress the data
            StringBuilder decompressedData = new StringBuilder();
            Node current = root;
            for (char bit : compressedData.toCharArray()) {
                current = (bit == '0') ? current.left : current.right;
                if (current.left == null && current.right == null) { // Leaf node
                    decompressedData.append(current.ch);
                    current = root; // Reset to root for the next character
                }
            }

            // Step 4: Write the decompressed data to the result file
            try (FileWriter writer = new FileWriter(resultFile)) {
                writer.write(decompressedData.toString());
            }

            System.out.println("File decompressed successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during decompression: " + e.getMessage());
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

    static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(Node other) {
            return this.freq - other.freq; // Compare nodes by frequency
        }
    }

    private static Node buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue(), null, null));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new Node('\0', left.freq + right.freq, left, right));
        }

        return pq.poll(); // Root of the Huffman tree
    }

    private static void generateHuffmanCodes(Node node, String code, Map<Character, String> huffmanCodes) {
        if (node == null) return;

        if (node.left == null && node.right == null) { // Leaf node
            huffmanCodes.put(node.ch, code);
        }

        generateHuffmanCodes(node.left, code + "0", huffmanCodes);
        generateHuffmanCodes(node.right, code + "1", huffmanCodes);
    }
}
