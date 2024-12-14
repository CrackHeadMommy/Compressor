public class Huffman {
    
    private static final int Alph_size = 256;
    
    public HuffEncodedRes comp(final String data) {
        return null;
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

    static class HuffEncodedRes {

    }

    public static void main(String[] args) {
        final String test = "abcdeffg";
        final int[] freqTab = buildFreqTable(test);
        System.out.println(freqTab);
    }
}