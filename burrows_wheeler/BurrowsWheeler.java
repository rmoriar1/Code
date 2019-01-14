import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler 
{
    private static final int EXTENDED_ASCII = 256;
    /* apply Burrows-Wheeler transform, reading from standard 
    input and writing to standard output */
    public static void transform() 
    {
        String s = BinaryStdIn.readString();
        int length = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < length; i++)
        {
            if (csa.index(i) == 0)
            {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < length; i++)
        {
            BinaryStdOut.write(s.charAt((csa.index(i) + length - 1) % length));
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    /* apply Burrows-Wheeler inverse transform, reading from 
    standard input and writing to standard output */
    public static void inverseTransform() 
    {
        int x = BinaryStdIn.readInt();
        ArrayList<Character> al = new ArrayList<>();
        int [] count = new int[EXTENDED_ASCII + 1];
        while (!BinaryStdIn.isEmpty())
        {
            char c = BinaryStdIn.readChar();
            al.add(c);
            count[c + 1]++;
        }
        int n = al.size();
        for (int r = 0; r < EXTENDED_ASCII; r++)
        {
            count[r+1] += count[r];
        }
        char [] aux = new char [n];
        int [] next = new int[n];
        for (int i = 0; i < n; i++)
        {
            char c = al.get(i);
            aux[count[c]] = al.get(i);
            next[count[c]] = i;
            count[c]++;
        }
        BinaryStdOut.write((char) aux[x]);
        for (int i = 0; i < n - 1; i++)
        {
            BinaryStdOut.write((char) aux[next[x]]);
            x = next[x];
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args)
    {
        if (args[0].equals("-"))
        {
            transform();
        }
        else if (args[0].equals("+"))
        {
            inverseTransform();
        }
        else
            throw new IllegalArgumentException();
    }
}