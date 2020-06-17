/* *****************************************************************************
 *  Name: Pascal
 *  Date:   June 2020
 *  Description: MoveToFront API
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> seq = initSeq();

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            int ix = seq.indexOf(ch) % R;
            assert ix >= 0 && ix < R;
            BinaryStdOut.write((byte) ix);
            seq.remove(ix);
            seq.add(0, ch);
        }
        BinaryStdOut.close(); // Or: BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Character> seq = initSeq();

        while (!BinaryStdIn.isEmpty()) {
            int ix = BinaryStdIn.readChar() % R;
            assert ix >= 0 && ix < R;
            char ch = seq.get(ix);
            BinaryStdOut.write(ch);
            seq.remove(ix);
            seq.add(0, ch);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else
            throw new IllegalArgumentException(
                    "Option not valid: only + for decode or - for encode");
    }

    private static LinkedList<Character> initSeq() {
        LinkedList<Character> seq = new LinkedList<Character>();
        for (char c = 0; c < R; c++) seq.add(c);
        return seq;
    }
}
