
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    
    private static final int Alph_size = 256;
    
    public HuffEncodedRes comp(final String data) {
        final int[] freq = buildFreqTable(data);
        final Node root = buildHuffmanTree(freq);
        final Map<Character, String> lookupTable = buildLookupTable(root);
        
        return new HuffEncodedRes(generateEncodedData(data, lookupTable, root));
        
    }
        
    private static String generateEncodedData(String data, Map<Character, String> lookupTable) {
        final StringBuilder builder = new StringBuilder();
        for(final char character : data.toCharArray()){
            builder.append(lookupTable.get(character));
        }
        return builder.toString();
    }
        
        private static Map<Character, String> buildLookupTable(final Node root){

        final Map<Character, String> lookupTable = new HashMap<>();

        buildLookupTableImpl(root, "", lookupTable);
                return lookupTable;
            }
        
            
        
    private static void buildLookupTableImpl(final Node node, final String s, final Map<Character, String> lookupTable) {
        
        if(!node.isLeaf()) {
            buildLookupTableImpl(node.left, s + '0', lookupTable);
            buildLookupTableImpl(node.right, s + '1', lookupTable);
        } else {
            lookupTable.put(node.character, s);
        }

    }
        
            private static Node buildHuffmanTree(int[] freq){

        final PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        for(char i = 0; i < Alph_size; i++){
            if(freq[i] > 0){
                priorityQueue.add(new Node(i, freq[i], null, null));
            }
        }

        if(priorityQueue.size() == 1){
            priorityQueue.add(new Node('\0', 1, null, null));
        }

        while(priorityQueue.size() > 1) {
            final Node left = priorityQueue.poll();
            final Node right = priorityQueue.poll();
            final Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        
        }
        return priorityQueue.poll();
    }


    private static int[] buildFreqTable(final String data){
        final int[] freq = new int[Alph_size];
        for (final char character : data.toCharArray()) {
            freq[character]++;

        }
        return freq;
    }



    public String decomp(final HuffEncodedRes result) {
        return null;
        
    }

    static class Node implements Comparable<Node>{
        private final char character;
        private final int frequency;
        private final Node left;
        private final Node right;

        private Node(final char character, final int frequency, final Node left, final Node right){
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf(){
            return this.left == null && this.right == null;
        }

        @Override
        public int compareTo(Huffman.Node that) {
            final int frequencyComparison = Integer.compare(this.frequency, that.frequency);
            if(frequencyComparison != 0){
                return frequencyComparison;
            }
            return Integer.compare(this.character, that.character);
        }
    }



    static class HuffEncodedRes {
        
        final Node root;
        final String encodedData;
        
        HuffEncodedRes(final String encodedData, final Node root) {
            this.root = root;
            this.encodedData = encodedData;
        }
    }

    public static void main(String[] args) {
        final String test = "abcdeffg";
        final int[] freqTab = buildFreqTable(test);
        final Node n = buildHuffmanTree(freqTab);
        final Map<Character, String> lookup = buildLookupTable(n);
        System.out.println(n);
    }
}