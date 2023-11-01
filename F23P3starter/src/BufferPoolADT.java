// ADT for buffer pools using the buffer-passing style

import java.io.IOException;

public interface BufferPoolADT {

    // Copy "sz" bytes from "space" to position "pos" in the buffered storage
    public void insert(int blockIndex) throws Exception;


    // Return pointer to the requested block
    public byte[] getblock(int block) throws Exception;


    // Set the dirty bit for the buffer holding "block"
    public void dirtyblock(int block);


    // Tell the size of a buffer
    public int blocksize();
};
