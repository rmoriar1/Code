import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.LinkedList;

public class MoveToFront 
{
    private static final int EXTENDED_ASCII = 256;
    /* apply move-to-front encoding, reading from standard
    input and writing to standard output */
    public static void encode() 
    {
        LinkedList<Character> ll = new LinkedList<>();
        for (int i = 0; i < EXTENDED_ASCII; i++)
        {
            char c = (char) i;
            ll.addLast(c);
        }
        while (!BinaryStdIn.isEmpty())
        {
            char c = BinaryStdIn.readChar();
            int j = ll.indexOf(c);
            ll.remove(j);
            ll.addFirst(c);
            byte b = (byte) j;
            BinaryStdOut.write(b);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    /* apply move-to-front decoding, reading from standard 
    input and writing to standard output */
    public static void decode()
    {
        LinkedList<Character> ll = new LinkedList<>();
        for (int i = 0; i < EXTENDED_ASCII; i++)
        {
                char c = (char) i;
                ll.addLast(c);
        }
        while (!BinaryStdIn.isEmpty())
        {
            int i = BinaryStdIn.readChar();
            char c = ll.get(i);
            BinaryStdOut.write(ll.remove(i));
            ll.addFirst(c);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args)
    {
        if (args[0].equals("-"))
        {
            encode();
        }
        else 
            decode();
    }
}
