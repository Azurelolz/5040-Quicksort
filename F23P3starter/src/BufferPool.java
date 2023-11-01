import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BufferPool implements BufferPoolADT {
    private byte[][] buffer;
    private int[] blockIndices;
    private int numBuffers;
    private int totalLength;
    private int totalBlocks;
    private int[] bufferModified;
    private RandomAccessFile raf;

    public BufferPool(RandomAccessFile raf, int numBuffers) throws Exception {
        this.raf = raf;
        this.numBuffers = numBuffers;
        this.buffer = new byte[numBuffers][4096];
        this.blockIndices = new int[numBuffers];
        this.totalLength = (int)raf.length();
        this.totalBlocks = totalLength / 4096;
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
            buffer[0] = new byte[4096];
        }
        loadBlock(0, blockIndex);
    }


    @Override
    public void getbytes(byte[] space, int sz, int pos) {
        // TODO Auto-generated method stub

    }


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
    public void dirtyblock(int block) {
        bufferModified[block] = 1;
    }


    @Override
    public int blocksize() {
        return totalBlocks;
    }


    public void modifyblock(byte[] block, int blockIndex) throws Exception {
        int index = searchBlock(blockIndex);
        if (index == -1) {
            insert(blockIndex);
            buffer[0] = block;
            dirtyblock(0);
        }
        else {
            buffer[index] = block;
            dirtyblock(index);
        }
        
    }


    private void loadBlock(int bufferIndex, int blockIndex) throws Exception {
        
        buffer[bufferIndex] = new byte[4096]; 
        raf.seek(blockIndex * 4096);
        raf.readFully(buffer[bufferIndex]);
        blockIndices[bufferIndex] = blockIndex;
        bufferModified[bufferIndex] = 0;
    }


    private int searchBlock(int blockIndex) {
        for (int i = 0; i < numBuffers; i++) {
            if (blockIndices[i] == blockIndex) {
                return i;
            }
        }
        return -1;
    }


    public int getTotalLength() {
        return totalLength;
    }


    public void writeBack(int position) throws Exception {
        int fileIndex = 4096 * blockIndices[position];
        raf.seek(fileIndex);
        raf.write(buffer[position]);
    }


    public void flushbuffer() throws Exception {
        for (int i = 0; i < blockIndices.length; i++) {
            if (bufferModified[i] == 1) {
                writeBack(i);
//                System.out.println("i = " + i);
            }

        }
    }
    
    public void printAllIndex() {
        System.out.println("Block indices: ");
        for (int i = 0; i < blockIndices.length; i++) {
            System.out.print(blockIndices[i] + " ");
        }
        System.out.print("\n");
    }

}
