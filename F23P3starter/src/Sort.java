/**
 * The Sort class is the main quick sort process, including quicksort,
 * findpivot, swap, and partition.
 * 
 * @author Yu-Kai Lo
 * @version 1.0
 */
public class Sort {
    private final int blockSize = 4096;

    /**
     * Find the pivot index for quick sort.
     * 
     * @param i
     *            The left index.
     * @param j
     *            The right index.
     * @return The pivot index.
     */
    private int findpivot(int i, int j) {
        int numGroups = (j - i) / 4 + 1;
        int middleGroupIndex = numGroups / 2;
        return (i / 4 + middleGroupIndex) * 4;
    }


    /**
     * Swap the two groups of key and data.
     * 
     * @param bufferPool
     *            The buffer pool.
     * @param leftBlock
     *            The left block of the right key.
     * @param leftIndex
     *            The left index of the right key.
     * @param rightBlock
     *            The right block of the right key.
     * @param rightIndex
     *            The right index of the right key.
     * @throws Exception
     */
    private void swap(
        BufferPool bufferPool,
        int leftBlock,
        int leftIndex,
        int rightBlock,
        int rightIndex)
        throws Exception {
        byte temp;

        if (leftBlock == rightBlock) {
            byte[] byteArr = bufferPool.getblock(leftBlock);
            for (int index = 0; index < 4; index++) {
                temp = byteArr[leftIndex + index];
                byteArr[leftIndex + index] = byteArr[rightIndex + index];
                byteArr[rightIndex + index] = temp;
            }
            bufferPool.dirtyblock(byteArr, leftBlock);
        }
        else {
            byte[] left = bufferPool.getblock(leftBlock);
            byte[] right = bufferPool.getblock(rightBlock);

            for (int index = 0; index < 4; index++) {

                temp = left[leftIndex + index];
                left[leftIndex + index] = right[rightIndex + index];
                right[rightIndex + index] = temp;
            }
            bufferPool.dirtyblock(right, rightBlock);
            bufferPool.dirtyblock(left, leftBlock);

        }
    }


    /**
     * The three-way partition for the quick sort.
     * Implement three-way instead of the standard partition
     * to avoid the stack over flow error.
     * 
     * @param bufferPool
     *            The buffer pool.
     * @param leftBlock
     *            The left block of the right key.
     * @param leftIndex
     *            The left index of the right key.
     * @param rightBlock
     *            The right block of the right key.
     * @param rightIndex
     *            The right index of the right key.
     * @param pivot
     *            The key of the pivot.
     * @return An integer array including lessBlock, lessIndex, greaterBlock,
     *         and greaterIndex
     * @throws Exception
     */
    public int[] threeWayPartition(
        BufferPool bufferPool,
        int leftBlock,
        int leftIndex,
        int rightBlock,
        int rightIndex,
        short pivot)
        throws Exception {
        int lessBlock = leftBlock;
        int lessIndex = leftIndex;
        int greaterBlock = rightBlock;
        int greaterIndex = rightIndex;
        int currentBlock = leftBlock;
        int currentIndex = leftIndex;

        while (currentBlock < greaterBlock || (currentBlock == greaterBlock
            && currentIndex <= greaterIndex)) {
            short key = bufferPool.getkey(bufferPool.getblock(currentBlock),
                currentIndex);

            if (key < pivot) {
                swap(bufferPool, lessBlock, lessIndex, currentBlock,
                    currentIndex);
                lessIndex += 4;
                if (lessIndex >= blockSize) {
                    lessIndex = 0;
                    lessBlock++;
                }
                currentIndex += 4;
                if (currentIndex >= blockSize) {
                    currentIndex = 0;
                    currentBlock++;
                }
            }
            else if (key > pivot) {
                swap(bufferPool, currentBlock, currentIndex, greaterBlock,
                    greaterIndex);
                greaterIndex -= 4;
                if (greaterIndex < 0) {
                    greaterIndex = blockSize - 4;
                    greaterBlock--;
                }
            }
            else {
                currentIndex += 4;
                if (currentIndex >= blockSize) {
                    currentIndex = 0;
                    currentBlock++;
                }
            }
        }
        return new int[] { lessBlock, lessIndex, greaterBlock, greaterIndex };
    }


    /**
     * The main quick sort method.
     * 
     * @param bufferPool
     *            The buffer pool.
     * @param leftBlock
     *            The left block of the right key.
     * @param leftIndex
     *            The left index of the right key.
     * @param rightBlock
     *            The right block of the right key.
     * @param rightIndex
     *            The right index of the right key.
     * @throws Exception
     */
    public void quicksort(
        BufferPool bufferPool,
        int leftBlock,
        int leftIndex,
        int rightBlock,
        int rightIndex)
        throws Exception {
        if (rightBlock < leftBlock || (rightBlock == leftBlock
            && rightIndex <= leftIndex)) {
            return;
        }

        int pivotIndex = findpivot(leftBlock * blockSize + leftIndex, rightBlock
            * blockSize + rightIndex);
        int pivotBlock = pivotIndex / blockSize;
        int pivotOffset = pivotIndex % blockSize;
        swap(bufferPool, pivotBlock, pivotOffset, rightBlock, rightIndex);
        short pivot = bufferPool.getkey(bufferPool.getblock(rightBlock),
            rightIndex);

        int[] partitionResult = threeWayPartition(bufferPool, leftBlock,
            leftIndex, rightBlock, rightIndex, pivot);
        int lessBlock = partitionResult[0];
        int lessIndex = partitionResult[1];
        int greaterBlock = partitionResult[2];
        int greaterIndex = partitionResult[3];

        if (lessBlock > leftBlock || (lessBlock == leftBlock
            && lessIndex > leftIndex)) {
            int newLessIndex = lessIndex - 4;
            int newLessBlock = lessBlock;
            if (newLessIndex < 0) {
                newLessIndex = blockSize - 4;
                newLessBlock -= 1;
            }

            quicksort(bufferPool, leftBlock, leftIndex, newLessBlock,
                newLessIndex);
        }

        if (greaterBlock < rightBlock || (greaterBlock == rightBlock
            && greaterIndex < rightIndex)) {
            int newGreaterIndex = greaterIndex + 4;
            int newGreaterBlock = greaterBlock;
            if (newGreaterIndex >= blockSize) {
                newGreaterIndex = 0;
                newGreaterBlock += 1;
            }
            quicksort(bufferPool, newGreaterBlock, newGreaterIndex, rightBlock,
                rightIndex);
        }
    }

}
