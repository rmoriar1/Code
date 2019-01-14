import edu.princeton.cs.algs4.TST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashSet;

public class BoggleSolver
{
    private HashSet<String> set = new HashSet<>();
    private final TST<Integer> trie = new TST<>();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary)
    {

        for (int i = 0; i < dictionary.length; i++)
        {
            String s = dictionary[i];
            trie.put(s, s.length());
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        set = new HashSet<>();
        for (int i = 0; i < board.rows(); i++)
        {
            for (int j = 0; j < board.cols(); j++)
            {
                String visited = "";
                String firstLetter = "" + board.getLetter(i, j);
                getStringRec(board, i, j, firstLetter, visited);
            }
        }
        return set;
    }

    private void getStringRec(BoggleBoard b, int row, int col, String cur, String visited)
    {
        if (visited.contains("(" + row + "," + col + ")"))
            return;
        if (cur.charAt(cur.length() - 1) == 'Q')
        {
            cur = cur + "U";
        }
        visited = visited + "(" + row + "," + col + ")";
        int rowLength = b.rows();
        int colLength = b.cols();
        int i = 0;
        if (trie.contains(cur))
        {
            if (!set.contains(cur) && cur.length() > 2)
            {
                set.add(cur);
            }
        }
        for (Object s : trie.keysWithPrefix(cur))
        {
            i++;
            break;
        }
        if (i == 0)
        {
            return;
        }
        if (row > 0)
        {
            String up = cur + b.getLetter(row - 1, col);
            String copy = visited;
            getStringRec(b, row - 1, col, up, copy);
        }
        if (row < rowLength - 1)
        {
            String down = cur + b.getLetter(row + 1, col);
            String copy = visited;
            getStringRec(b, row + 1, col, down, copy);
        }
        if (col > 0)
        {
            String left = cur + b.getLetter(row, col - 1);
            String copy = visited;
            getStringRec(b, row, col - 1, left, copy);
        }
        if (col < colLength - 1)
        {
            String right = cur + b.getLetter(row, col + 1);
            String copy = visited;
            getStringRec(b, row, col + 1, right, copy);
        }
        if (row > 0 && col > 0)
        {
            String upLeft = cur + b.getLetter(row - 1, col - 1);
            String copy = visited;
            getStringRec(b, row - 1, col - 1, upLeft, copy);
        }
        if (row < rowLength - 1 && col > 0)
        {
            String downLeft = cur + b.getLetter(row + 1, col - 1);
            String copy = visited;
            getStringRec(b, row + 1, col - 1, downLeft, copy);
        }
        if (row < rowLength - 1 && col < colLength - 1)
        {
            String downRight = cur + b.getLetter(row + 1, col + 1);
            String copy = visited;
            getStringRec(b, row + 1, col + 1, downRight, copy);
        }
        if (row > 0 && col < colLength - 1)
        {
            String upRight = cur + b.getLetter(row - 1, col + 1);
            String copy = visited;
            getStringRec(b, row - 1, col + 1, upRight, copy);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word)
    {
        if (!trie.contains(word))
            return 0;
        int len = word.length();
        if (len < 3)
            return 0;
        else if (len < 5)
            return 1;
        else if (len < 6)
            return 2;
        else if (len < 7)
            return 3;
        else if (len < 8)
            return 5;
        else return 11;
    }

    public static void main(String[] args) {
    In in = new In(args[0]);
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard(args[1]);
    int score = 0;
    for (String word : solver.getAllValidWords(board)) {
        StdOut.println(word);
        score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);
}

}