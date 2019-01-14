import java.util.ArrayList;

public class TxHandler {

    private UTXOPool ledger;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        ledger = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) 
    {
        // IMPLEMENT THIS
        double sumOutput = 0;
        double sumInput = 0;
        ArrayList<UTXO> utxos = new ArrayList<>();
        for (Transaction.Output op : tx.getOutputs())
        {
            if (op.value < 0)
                return false;
            sumOutput += op.value;
        }
        for (Transaction.Input ip : tx.getInputs())
        {
            UTXO utxo = new UTXO(ip.prevTxHash, ip.outputIndex);
            Transaction.Output output = ledger.getTxOutput(utxo);
            if (!ledger.contains(utxo))
                return false;
            if (!Crypto.verifySignature(output.address, 
                tx.getRawDataToSign(utxos.size()), ip.signature))
                return false;
            if (utxos.contains(utxo))
                return false;
            utxos.add(new UTXO(ip.prevTxHash, ip.outputIndex));
            sumInput += output.value;
        }
        if (sumOutput > sumInput)
            return false;
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> accepted = new ArrayList<>();
        for (Transaction t : possibleTxs)
        {
            if (isValidTx(t))
            {
                accepted.add(t);
                for (Transaction.Output op : t.getOutputs())
                {
                    UTXO current = new UTXO(t.getHash(), t.getOutputs().indexOf(op));
                    ledger.addUTXO(current, op);
                }
                for (Transaction.Input ip : t.getInputs())
                {
                    UTXO current = new UTXO(ip.prevTxHash, ip.outputIndex);
                    ledger.removeUTXO(current);
                }
            }        
        }
        Transaction[] acceptedTxs = new Transaction[accepted.size()];
        for (int i = 0; i < accepted.size(); i++)
        {
            acceptedTxs[i] = accepted.get(i);
        }
        return acceptedTxs;
    }
}
