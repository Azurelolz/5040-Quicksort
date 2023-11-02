/**
 * ADT for buffer pools using the buffer-passing style
 * 
 * @author Yu-Kai Lo
 * @version 1.0
 */
public interface BufferPoolADT {

    /**
     * Insert the byte block into buffer pool.
     * 
     * @param blockIndex
     *            The index of the byte block.
     * @throws Exception
     */
    public void insert(int blockIndex) throws Exception;


    /**
     * Get the byte block from buffer pool.
     * 
     * @param blockIndex
     *            The index of the origin byte block.
     * @return The byte array from buffer pool.
     * @throws Exception
     */
    public byte[] getblock(int blockIndex) throws Exception;


    /**
     * Modify the byte block in buffer pool and mark as dirty.
     * 
     * @param block
     *            The modified block.
     * @param blockIndex
     *            The index of the block.
     * @throws Exception
     */
    public void dirtyblock(byte[] block, int blockIndex) throws Exception;


    /**
     * Get the number of the blocks.
     * 
     * @return The number of the blocks.
     */
    public int blocksize();
};
