
/**
 * {Project Description Here}
 */

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

/**
 * The class containing the main method.
 *
 * @author Yu-Kai Lo
 * @version v 1.0
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

public class Quicksort {
    private static int recCount = 0;
    /**
     * This method is used to generate a file of a certain size, containing a
     * specified number of records.
     *
     * @param filename
     *            the name of the file to create/write to
     * @param blockSize
     *            the size of the file to generate
     * @param format
     *            the format of file to create
     * @throws IOException
     *             throw if the file is not open and proper
     */
    public static void generateFile(
        String filename,
        String blockSize,
        char format)
        throws IOException {
        FileGenerator generator = new FileGenerator();
        String[] inputs = new String[3];
        inputs[0] = "-" + format;
        inputs[1] = filename;
        inputs[2] = blockSize;
        generator.generateFile(inputs);
    }


    static int findpivot(int i, int j) {
        int numGroups = (j - i) / 4 + 1;
        int middleGroupIndex = numGroups / 2;
        return (i / 4 + middleGroupIndex) * 4;
    }


    static void swap(
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
            bufferPool.modifyblock(byteArr, leftBlock);
        }
        else {
            byte[] left = bufferPool.getblock(leftBlock);
            byte[] right = bufferPool.getblock(rightBlock);

            for (int index = 0; index < 4; index++) {
                temp = left[leftIndex + index];
                left[leftIndex + index] = right[rightIndex + index];
                right[rightIndex + index] = temp;
            }
            bufferPool.modifyblock(right, rightBlock);
            bufferPool.modifyblock(left, leftBlock);
           
        }
    }

    static int partition(
        BufferPool bufferPool,
        int leftBlock,
        int leftIndex,
        int rightBlock,
        int rightIndex,
        short pivot)
        throws Exception {
        short leftKey = 0;
        short rightKey = 0;

//        System.out.println("Pivot: " + pivot);
        while (leftBlock * 4096 + leftIndex <= rightBlock * 4096 + rightIndex) {
//            recCount += 1;
//            if (recCount == 4) {
//                recCount = 3;
//                break;
//            }
//            System.out.println("=============================");
//            System.out.println("Rec Count: " + recCount);
//            System.out.println("=============================");
           
            byte[] leftBytes = bufferPool.getblock(leftBlock);
            byte[] rightBytes = bufferPool.getblock(rightBlock);

            leftKey = bufferPool.getkey(leftBytes, leftIndex);
            rightKey = bufferPool.getkey(rightBytes, rightIndex);

            while (leftBlock * 4096 + leftIndex <= rightBlock * 4096
                + rightIndex && leftKey < pivot) {
                leftIndex += 4;
                if (leftIndex >= 4096 && leftBlock == bufferPool.blocksize()
                    - 1) {
                    break;
                }
                else if (leftIndex >= 4096 && leftBlock != bufferPool
                    .blocksize() - 1) {
                    leftBlock += 1;
                    leftIndex = 0;
                    leftBytes = bufferPool.getblock(leftBlock);
                }
//                System.out.println("Left index ++...");
//                System.out.println("Left key = " + leftKey);
//                System.out.println("Left index = " + leftIndex);
//                System.out.println("Right key = " + rightKey);
//                System.out.println("Right index = " + rightIndex);
//                System.out.println("----------------------------");
                leftKey = bufferPool.getkey(leftBytes, leftIndex);
            }

            while (leftBlock * 4096 + leftIndex <= rightBlock * 4096
                + rightIndex && rightKey >= pivot) {
                rightIndex -= 4;

                if (rightIndex < 0 && rightBlock == 0) {
                    break;
                }
                else if (rightIndex < 0 && rightBlock != 0) {
                    rightBlock -= 1;
                    rightIndex = 4092;
                    rightBytes = bufferPool.getblock(rightBlock);
                }
//                System.out.println("Right index --...");
//                System.out.println("Left key = " + leftKey);
//                System.out.println("Left index = " + leftIndex);
//                System.out.println("Right key = " + rightKey);
//                System.out.println("Right index = " + rightIndex);
//                System.out.println("----------------------------");
                rightKey = bufferPool.getkey(rightBytes, rightIndex);
            }

            if (rightBlock * 4096 + rightIndex > leftBlock * 4096 + leftIndex) {
                swap(bufferPool, leftBlock, leftIndex, rightBlock, rightIndex);
//                System.out.println("Swapping......");
//                System.out.println("Left key = " + leftKey);
//                System.out.println("Left index = " + leftIndex);
//                System.out.println("Right key = " + rightKey);
//                System.out.println("Right index = " + rightIndex);
//                System.out.println("----------------------------");
            }
        }
        return leftBlock * 4096 + leftIndex;
    }
    
    
    static int[] threeWayPartition(BufferPool bufferPool, int leftBlock, int leftIndex, int rightBlock, int rightIndex, short pivot)
        throws Exception {
    int ltBlock = leftBlock, ltIndex = leftIndex;
    int gtBlock = rightBlock, gtIndex = rightIndex;
    int iBlock = leftBlock, iIndex = leftIndex;

    // Loop until i index crosses gt index
    while (iBlock < gtBlock || (iBlock == gtBlock && iIndex <= gtIndex)) {
        short key = bufferPool.getkey(bufferPool.getblock(iBlock), iIndex);

        if (key < pivot) {
            swap(bufferPool, ltBlock, ltIndex, iBlock, iIndex);
            // Move lt pointers forward
            ltIndex += 4;
            if (ltIndex >= 4096) {
                ltIndex = 0;
                ltBlock++;
            }
            // Move i pointers forward
            iIndex += 4;
            if (iIndex >= 4096) {
                iIndex = 0;
                iBlock++;
            }
        } else if (key > pivot) {
            swap(bufferPool, iBlock, iIndex, gtBlock, gtIndex);
            // Move gt pointers backward
            gtIndex -= 4;
            if (gtIndex < 0) {
                gtIndex = 4092;
                gtBlock--;
            }
        } else {
            // Move i pointers forward
            iIndex += 4;
            if (iIndex >= 4096) {
                iIndex = 0;
                iBlock++;
            }
        }
    }

    // Return both lt and gt indices (as block and index pairs)
    return new int[]{ltBlock, ltIndex, gtBlock, gtIndex};
}
    


//    static void quicksort(
//        BufferPool bufferPool,
//        int leftBlock,
//        int leftIndex,
//        int rightBlock,
//        int rightIndex)
//        throws Exception {
////        recCount += 1;
////        if (recCount >= 10) {
////            return;
////        }
//        int pivotindex = findpivot(leftBlock * 4096 + leftIndex, rightBlock
//            * 4096 + rightIndex);
//        int pivotBlock = pivotindex / 4096;
//        swap(bufferPool, pivotBlock, pivotindex % 4096, rightBlock, rightIndex);
//        short pivot = bufferPool.getkey(bufferPool.getblock(
//            rightBlock), rightIndex);
//        int inputRightIndex = rightIndex;
//        int inputRightBlock = rightBlock;
//        if (inputRightIndex - 4 < 0) {
//            inputRightIndex = 4092;
//            inputRightBlock -= 1;
//        }
//        int k = partition(bufferPool, leftBlock, leftIndex, inputRightBlock,
//            inputRightIndex, pivot);
//        int kBlock = k / 4096;
////        System.out.println("LeftBlock: " + leftBlock);
////        System.out.println("LeftIndex: " + leftIndex);
////        System.out.println("RightBlock: " + rightBlock);
////        System.out.println("RightIndex: " + rightIndex);
////        System.out.println("PivotBlock: " + pivotBlock);
////        System.out.println("PivotIndex: " + pivotindex);
////        System.out.println("Pivot: " + pivot);
////        System.out.println("KBlock: " + kBlock);
////        System.out.println("KIndex: " + k);
////        bufferPool.printAllIndex();
////        System.out.println("====================================");
//        swap(bufferPool, kBlock, k % 4096, rightBlock, rightIndex);
//        if (k - (leftBlock * 4096 + leftIndex) > 4) {
//            int newRightIndex = (k - 4) % 4096;
//            int newRightBlock = (k - 4) / 4096;
////            System.out.println("Go left...");
//            quicksort(bufferPool, leftBlock, leftIndex, newRightBlock,
//                newRightIndex);
//        } // Sort left partition
//        if (rightBlock * 4096 + rightIndex - k > 4) {
//            int newLeftIndex = (k + 4) % 4096;
//            int newLeftBlock = (k + 4) / 4096;
////            System.out.println("Go right...");
//            quicksort(bufferPool, newLeftBlock, newLeftIndex, rightBlock,
//                rightIndex);
//        } // Sort right partition
//    }
    
    static void quicksort(BufferPool bufferPool, int leftBlock, int leftIndex, int rightBlock, int rightIndex)
        throws Exception {
    if (rightBlock < leftBlock || (rightBlock == leftBlock && rightIndex <= leftIndex)) {
        // Base case: 1 element or invalid range
        return;
    }

    // Choose pivot and partition around it
    int pivotindex = findpivot(leftBlock * 4096 + leftIndex, rightBlock * 4096 + rightIndex);
    int pivotBlock = pivotindex / 4096;
    int pivotOffset = pivotindex % 4096;
    swap(bufferPool, pivotBlock, pivotOffset, rightBlock, rightIndex);
    short pivot = bufferPool.getkey(bufferPool.getblock(rightBlock), rightIndex);
    
    // Perform three-way partitioning
    int[] partitionResult = threeWayPartition(bufferPool, leftBlock, leftIndex, rightBlock, rightIndex, pivot);
    int ltBlock = partitionResult[0];
    int ltIndex = partitionResult[1];
    int gtBlock = partitionResult[2];
    int gtIndex = partitionResult[3];

    // Recursively sort elements less than pivot
    if (ltBlock > leftBlock || (ltBlock == leftBlock && ltIndex > leftIndex)) {
        quicksort(bufferPool, leftBlock, leftIndex, ltBlock, ltIndex - 4); // -4 to move to the previous record
    }

    // Recursively sort elements greater than pivot
    if (gtBlock < rightBlock || (gtBlock == rightBlock && gtIndex < rightIndex)) {
        quicksort(bufferPool, gtBlock, gtIndex + 4, rightBlock, rightIndex); // +4 to move to the next record
    }
}



    /**
     * @param args
     *            Command line parameters.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
// This is the main file for the program.
// try {
// generateFile("1Test.txt", "1", 'a');
// generateFile("1Test-b.txt", "1", 'b');
// generateFile("10Test.txt", "10", 'a');
// generateFile("10Test-b.txt", "10", 'b');
// generateFile("100Test.txt", "100", 'a');
// generateFile("100Test-b.txt", "100", 'b');
// generateFile("1000Test.txt", "1000", 'a');
// generateFile("1000Test-b.txt", "1000", 'b');
// generateFile("90Test.txt", "90", 'a');
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
        // This will generate a new two block byte file. 
//        generateFile("3Test.txt", "3", 'a');
        long startTime = System.currentTimeMillis();
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null");
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(args[0], "rw");
            BufferPool bufferPool = new BufferPool(raf, Integer.parseInt(
                args[1]));
            quicksort(bufferPool, 0, 0, bufferPool.blocksize() - 1, 4092);
            bufferPool.flushbuffer();
//            bufferPool.printAllIndex();
            raf.close();
            CheckFile checker = new CheckFile();
            System.out.println(checker.checkFile(args[0]));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
        System.out.println("Process time: " + (endTime - startTime));

    }

}
