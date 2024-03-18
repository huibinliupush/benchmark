package com.benchmark.fileio;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import jdk.internal.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.List;

public class WriteWithPageCache {
    public static void main(String[] args) throws Exception {
        File file = new File("WriteWithPageCache");
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        long fileSize = 1024 * 1024 * 1024;
        MappedByteBuffer mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 0, fileSize);
        final long address = ((DirectBuffer) (mappedByteBuffer)).address();
        Pointer pointer = new Pointer(address);
        int mlockR = LibC.INSTANCE.mlock(pointer, new NativeLong(fileSize));
        System.out.println("mlock result: " + mlockR);

        List<DataSet> testDataSet = DataSet.loadTestDataSet();
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(512 * 1024 * 1024);
        Unsafe unsafe = Unsafe.getUnsafe();
        unsafe.setMemory(((DirectBuffer)directBuffer).address(), directBuffer.capacity(), (byte) 6);

        for (DataSet dataSet : testDataSet) {
            System.out.println("  *************** "+ dataSet.name + " *********公众号：bin的技术小屋");
            long start = System.currentTimeMillis();
            while (mappedByteBuffer.hasRemaining()) {
                directBuffer.position(0);
                directBuffer.limit(dataSet.size);
                mappedByteBuffer.put(directBuffer);
            }
            System.out.println("-- mmap 耗时：" + (System.currentTimeMillis() - start) + " ms");

            start = System.currentTimeMillis();
            for (int i = 0; i < fileSize; i += dataSet.size) {
                directBuffer.position(0);
                directBuffer.limit(dataSet.size);
                fileChannel.write(directBuffer);
            }
            System.out.println("-- filechannel 耗时：" + (System.currentTimeMillis() - start)  + " ms");

            mappedByteBuffer.rewind();
            fileChannel.position(0);
        }


        int munlockR = LibC.INSTANCE.munlock(pointer, new NativeLong(fileSize));
        System.out.println("munlock result: " + munlockR);
        ((DirectBuffer) mappedByteBuffer).cleaner().clean();
        fileChannel.close();
        file.delete();
    }
}
