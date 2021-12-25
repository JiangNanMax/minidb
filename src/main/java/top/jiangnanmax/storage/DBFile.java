package top.jiangnanmax.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
@Data
public class DBFile {

    public static final String fileName = "minidb.data";

    private RandomAccessFile file;
    private FileChannel channel;
    private long offset;

    public DBFile(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fullPath = path + File.separatorChar + fileName;
            this.file = new RandomAccessFile(fullPath, "rw");
            this.channel = this.file.getChannel();
            this.offset = this.file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Entry read(long offset) {
        ByteBuffer headerBuffer = ByteBuffer.allocate(Entry.ENTRY_HEADER_SIZE);
        try {
            channel.position(offset);
            int readBytes = channel.read(headerBuffer);
            // -1 for EOF
            if (readBytes == -1) {
                log.info("read to end of file");
                return null;
            }
            if (readBytes != Entry.ENTRY_HEADER_SIZE) {
                log.warn("read entry from offset: {} failed", offset);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        headerBuffer.flip();
        Entry entry = new Entry();
        entry.decodeHeader(headerBuffer);
        ByteBuffer keyBuffer = ByteBuffer.allocate(entry.getKeySize());
        ByteBuffer valueBuffer = ByteBuffer.allocate(entry.getValueSize());
        try {
            channel.read(keyBuffer);
            channel.read(valueBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyBuffer.flip();
        valueBuffer.flip();
        entry.setKey(keyBuffer);
        entry.setValue(valueBuffer);
        return entry;
    }

    public void write(Entry entry) {
        ByteBuffer buffer = entry.encode();
        try {
            channel.position(offset);
            int cnt = channel.write(buffer);
            offset += cnt;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            channel.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
