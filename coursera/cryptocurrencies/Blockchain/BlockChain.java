import java.util.*;
import java.io.*;
import java.lang.*;

// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

public class BlockChain {

    private HashMap<byte[], Node<Block>> blockHeights = new HashMap<>();
    private TransactionPool transactionPool = new TransactionPool();
    private int maxHeight = 1;
    private Node<Block> maxHeightNode;
    //private UTXOPool maxHeightUTXOPool;
    public class Node<Block> 
    {
        private Node<Block> prevBlock;
        private Block curBlock;
        private UTXOPool blockPool;
        private int height = 1;
        public Node(Block b, Node<Block> prev)
        {
            curBlock = b;
            prevBlock = prev;
            if (prev != null)
            {
                height = prev.getHeight() + 1;
            }
        }

        public Block getCurBlock()
        {
            return curBlock;
        }

        public UTXOPool getBlockPool()
        {
            return blockPool;
        }

        public int getHeight()
        {
            return height;
        }
    }
    public static final int CUT_OFF_AGE = 10;

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        Node<Block> gen = new Node<>(genesisBlock, null);
        UTXOPool genUTXO = new UTXOPool();
        TxHandler genTxHandler = new TxHandler(genUTXO);
        Transaction coinbase = genesisBlock.getCoinbase();
        for (Transaction.Output op : coinbase.getOutputs())
        {
            UTXO current = new UTXO(coinbase.getHash(), coinbase.getOutputs().indexOf(op));
            genUTXO.addUTXO(current, op);
        } 
        blockHeights.put(genesisBlock.getHash(), gen);
        gen.blockPool = genUTXO;
        maxHeightNode = gen;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        return maxHeightNode.getCurBlock();
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        return maxHeightNode.getBlockPool();
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        if (block.getPrevBlockHash() == null)
            return false;
        Node<Block> prevNode = blockHeights.get(block.getPrevBlockHash());
        if (prevNode == null)
            return false;
        if (prevNode.getHeight() < getMaxHeight() - CUT_OFF_AGE)
            return false;
        UTXOPool newUTXOPool = new UTXOPool(prevNode.blockPool);
        TxHandler handler = new TxHandler(newUTXOPool);
        Transaction[] txs = block.getTransactions().toArray(new Transaction[0]);
        Transaction[] rTxs = handler.handleTxs(txs);
        if (txs.length != rTxs.length)
            return false;
        Node<Block> newest = new Node<>(block, prevNode);
        newest.blockPool = handler.getUTXOPool();
        Transaction coinbase = block.getCoinbase();
        for (Transaction.Output op : coinbase.getOutputs())
        {
            UTXO current = new UTXO(coinbase.getHash(), coinbase.getOutputs().indexOf(op));
            newest.blockPool.addUTXO(current, op);
        }
        blockHeights.put(block.getHash(), newest);
        if (newest.getHeight() > maxHeight)
        {
            maxHeight = newest.getHeight();
            maxHeightNode = newest;
        }
        return true;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        transactionPool.addTransaction(tx);
    }
}