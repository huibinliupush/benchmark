package com.benchmark.fileio;

import jdk.internal.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.List;

public class WriteWithOutPageCache {
    public static void main(String[] args) throws Exception {
        long fileSize = 1024 * 1024 * 1024;
        List<DataSet> testDataSet = DataSet.loadTestDataSet();
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(512 * 1024 * 1024);
        Unsafe unsafe = Unsafe.getUnsafe();
        unsafe.setMemory(((DirectBuffer)directBuffer).address(), directBuffer.capacity(), (byte) 6);

        for (DataSet dataSet : testDataSet) {
            System.out.println("  *************** " + dataSet.name + " *********公众号：bin的技术小屋");

            String fileName = "mmapWrite" + dataSet.name;
            File mappedFile = new File(fileName);
            FileChannel mappedFileChannel = new RandomAccessFile(mappedFile, "rw").getChannel();
            MappedByteBuffer mappedByteBuffer = mappedFileChannel.map(MapMode.READ_WRITE, 0, fileSize);


            long start = System.currentTimeMillis();
            while (mappedByteBuffer.hasRemaining()) {
                directBuffer.position(0);
                directBuffer.limit(dataSet.size);
                mappedByteBuffer.put(directBuffer);
            }
            System.out.println("-- mmap 耗时：" + (System.currentTimeMillis() - start) + " ms");
            ((DirectBuffer) mappedByteBuffer).cleaner().clean();
            mappedFileChannel.close();
            mappedFile.delete();

            fileName = "fileChannelWrite" + dataSet.name;
            File file = new File(fileName);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(fileSize);
            FileChannel fileChannel = randomAccessFile.getChannel();

            start = System.currentTimeMillis();
            for (int i = 0; i < fileSize; i += dataSet.size) {
                directBuffer.position(0);
                directBuffer.limit(dataSet.size);
                fileChannel.write(directBuffer);
            }
            System.out.println("-- filechannel 耗时：" + (System.currentTimeMillis() - start) + " ms");
            fileChannel.close();
            file.delete();

        }
    }
}
