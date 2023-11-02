import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * The buffer pool class.
 * 
 * @author Yu-Kai Lo
 * @version 1.0
 */
public class BufferPool implements BufferPoolADT {
    private byte[][] buffer;
    private int[] blockIndices;
    private int numBuffers;
    private int totalLength;
    private int totalBlocks;
    private int[] bufferModified;
    private RandomAccessFile raf;
    private int blockSize = 4096;

    /**
     * Initial the buffer pool with random access file and number of buffers.
     * 
     * @param raf
     *            The file to read and write.
     * @param numBuffers
     *            The number of buffers.
     * @throws Exception
     */
    public BufferPool(RandomAccessFile raf, int numBuffers) throws Exception {
        this.raf = raf;
        this.numBuffers = numBuffers;
        this.buffer = new byte[numBuffers][blockSize];
        this.blockIndices = new int[numBuffers];
        this.totalLength = (int)raf.length();
        this.totalBlocks = totalLength / blockSize;
        this.bufferModified = new int[numBuffers];

        for (int i = 0; i < blockIndices.length; i++) {
            blockIndices[i] = -1;
        }

    }


    @Override
    public void insert(int blockIndex) throws Exception {
        if (bufferModified[numBuffers - 1] == 1) {
            writeBack(numBuffers - 1);
        }
        byte[] temp = buffer[numBuffers - 1];
        for (int i = numBuffers - 1; i > 0; i--) {
            blockIndices[i] = blockIndices[i - 1];
            buffer[i] = buffer[i - 1];
            bufferModified[i] = bufferModified[i - 1];
        }
        if (temp != buffer[0]) {
            buffer[0] = new byte[blockSize];
        }
        loadBlock(0, blockIndex);
    }


    /**
     * Wrap to bytes into one key for comparison.
     * 
     * @param block
     *            The byte block of the key.
     * @param index
     *            The index of the key.
     * @return The wrapped key.
     */
    public short getkey(byte[] block, int index) {
        byte[] bytes = new byte[] { block[index], block[index + 1] };
        return ByteBuffer.wrap(bytes).getShort();
    }


    @Override
    public byte[] getblock(int blockIndex) throws Exception {
        int index = searchBlock(blockIndex);
        if (index == -1) {
            insert(blockIndex);
        }
        else {
            byte[] blockToMove = buffer[index];
            int blockIndexToMove = blockIndices[index];
            int blockModification = bufferModified[index];
            for (int i = index; i > 0; i--) {
                buffer[i] = buffer[i - 1];
                blockIndices[i] = blockIndices[i - 1];
                bufferModified[i] = bufferModified[i - 1];
            }
            buffer[0] = blockToMove;
            blockIndices[0] = blockIndexToMove;
            bufferModified[0] = blockModification;
        }
        return buffer[0];
    }


    @Override
    public void dirtyblock(byte[] block, int blockIndex) throws Exception {

        int index = searchBlock(blockIndex);
        if (index == -1) {
            insert(blockIndex);
            buffer[0] = block;
            bufferModified[0] = 1;
        }
        else {
            buffer[index] = block;
            bufferModified[index] = 1;
        }

    }


    @Override
    public int blocksize() {
        return totalBlocks;
    }


    /**
     * Load blocks of bytes into buffer.
     * 
     * @param bufferIndex
     *            The index of the buffer pool.
     * @param blockIndex
     *            The index of the block.
     * @throws Exception
     */
    private void loadBlock(int bufferIndex, int blockIndex) throws Exception {

        buffer[bufferIndex] = new byte[blockSize];
        raf.seek(blockIndex * blockSize);
        raf.readFully(buffer[bufferIndex]);
        blockIndices[bufferIndex] = blockIndex;
        bufferModified[bufferIndex] = 0;
    }


    /**
     * Method to search the block if it exist in buffer.
     * 
     * @param blockIndex
     *            The block index to search for.
     * @return The block index in buffer.
     */
    private int searchBlock(int blockIndex) {
        for (int i = 0; i < numBuffers; i++) {
            if (blockIndices[i] == blockIndex) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Write byte block back to file.
     * 
     * @param blockIndex
     *            The index of the block.
     * @throws Exception
     */
    public void writeBack(int blockIndex) throws Exception {
        int fileIndex = blockSize * blockIndices[blockIndex];
        raf.seek(fileIndex);
        raf.write(buffer[blockIndex]);
    }


    /**
     * Flush all blocks in buffer back to the file.
     * 
     * @throws Exception
     */
    public void flushbuffer() throws Exception {
        for (int i = 0; i < blockIndices.length; i++) {
            if (bufferModified[i] == 1) {
                writeBack(i);
            }

        }
    }

}
