package test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

public class TestWriter extends TestCase {

    private String fileName = "test.txt";
    private String fileName1 = "test1.txt";
    private String fileName2 = "test2.txt";
    private String fileName3 = "test3.txt";
    private String fileName4 = "test4.txt";
    private String fileName5 = "test5.txt";

    public TestWriter(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    public void whenWriteStringUsingBufferedWritter_thenCorrect() throws IOException{
        String str = "Hello";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName3));
        writer.write(str);
        writer.close();
    }
    
    public void whenAppendStringUsingBufferedWritter_thenOldContentShouldExistToo() throws IOException{
        String str = "World";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName3 , true));
        writer.append(' ');
        writer.append(str);
        writer.close();
    }
    
    public void whenWriteFormattedStringUsingPrintWriter_thenOutputShouldBeFormatted() throws IOException{
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Product name is %s and its price is %d $","iPhone",1000);
        printWriter.close();
    }
    
    public void whenWriteStringUsingFileOutputStream_thenCorrect() throws IOException{
        String str = "Hello";
        FileOutputStream outputStream = new FileOutputStream(fileName3);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public void whenWriteStringWithDataOutputStream_thenReadShouldBeTheSame() throws IOException {
        String value = "Hello";
        FileOutputStream fos = new FileOutputStream(fileName1);
        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
        outStream.writeUTF(value);
        outStream.close();
        
        String result;
        FileInputStream fis = new FileInputStream(fileName1);
        DataInputStream reader = new DataInputStream(fis);
        result = reader.readUTF();
        reader.close();
        
        assertEquals(value, result);
    }

    public void whenWriteToPositionAndEditIt_thenItShouldChange() {
        int data1 = 2014;
        int data2 = 1500;
        testWriteToPosition(data1, 4);
        assertEquals(data1, testReadFromPosition(4));
        testWriteToPosition(data2, 4);
        assertEquals(data2, testReadFromPosition(4));
    }


    public void whenTryToLockFile_thenItShouldBeLocked() throws IOException {
        RandomAccessFile stream = new RandomAccessFile(fileName4, "rw");
        FileChannel channel = stream.getChannel();

        FileLock lock = null;
        try {
            lock = channel.tryLock();
        } catch (OverlappingFileLockException e) {
            stream.close();
            channel.close();
        }
        stream.writeChars("test lock");
        lock.release();
        
        stream.close();
        channel.close();
    }

    
    
    public void whenWriteStringUsingFileChannel_thenReadShouldBeTheSame() throws IOException{
        RandomAccessFile stream = new RandomAccessFile(fileName5, "rw");
        FileChannel channel = stream.getChannel();
        String value = "Hello";
        byte[] strBytes = value.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);        
        stream.close();
        channel.close();
        
        RandomAccessFile reader = new RandomAccessFile(fileName5, "r");
        assertEquals(value , reader.readLine());
        reader.close();
        
    }
    
    public void whenWriteToTmpFile_thenCorrect() throws IOException{
        String toWrite = "Hello";
        File tmpFile = File.createTempFile("test", ".tmp");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(toWrite);
        writer.close();
        
        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        assertEquals(toWrite, reader.readLine());
        reader.close();
    }
    
    public void whenWriteUsingJava7_thenCorrect() throws IOException{
        String str = "Hello";
        
        Path path = Paths.get(fileName3);
        byte[] strToBytes = str.getBytes();
        
        Files.write(path, strToBytes);
        
        String read = Files.readAllLines(path).get(0);
        assertEquals(str, read);
    }
    
    // use RandomAccessFile to write data at specific position in the file
    public void testWriteToPosition(int data, long position) {
        try {
            RandomAccessFile writer = new RandomAccessFile(fileName2, "rw");
            writer.seek(position);
            writer.writeInt(data);
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }



    // use RandomAccessFile to read data from specific position in the file
    public int testReadFromPosition(long position) {
        int result = 0;
        try {
            RandomAccessFile reader = new RandomAccessFile(fileName2, "r");
            reader.seek(position);
            result = reader.readInt();
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return result;
    }

   
   

    
}
