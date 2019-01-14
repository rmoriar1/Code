import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private double p_graph;
    private double p_malicious;
    private double p_txDistribution;
    private int numRounds;
    private boolean[] followees;
    private Set<Transaction> pendingTransactions;
    private HashMap<Transaction, Integer> txCount = new HashMap<>();

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
        Set<Transaction> cur = new HashSet<Transaction>(pendingTransactions);
        pendingTransactions.clear();
        return cur;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {

        for (Candidate c : candidates)
        {
            if (!pendingTransactions.contains(c.tx))
            {
                pendingTransactions.add(c.tx);
            }
        }
    }
}
