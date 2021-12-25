package top.jiangnanmax.storage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

public class TestDBFile {

    private DBFile dbFile;
    private String dirPath = "/tmp/minidb";

    private String keyHeader;
    private String valueHeader;

    private int loop;

    @Before
    public void setup() {
        dbFile = new DBFile(dirPath);
        keyHeader = "test_key_";
        valueHeader = "test_value_";
        loop = 10000;
    }

    @Test
    public void testWriteAndRead() {
        byte[] key;
        byte[] value;
        long offset = dbFile.getOffset();
        for (int i = 0; i < loop; i++) {
            key = (keyHeader + i).getBytes();
            value = (valueHeader + i).getBytes();
            ByteBuffer keyBuffer = ByteBuffer.allocate(key.length);
            ByteBuffer valueBuffer = ByteBuffer.allocate(value.length);
            keyBuffer.put(key);
            valueBuffer.put(value);
            keyBuffer.flip();
            valueBuffer.flip();
            Entry entry = new Entry(keyBuffer, valueBuffer, Entry.NORMAL);
            dbFile.write(entry);
        }

        for (int i = 0; i < loop; i++) {
            key = (keyHeader + i).getBytes();
            value = (valueHeader + i).getBytes();
            ByteBuffer keyBuffer = ByteBuffer.allocate(key.length);
            ByteBuffer valueBuffer = ByteBuffer.allocate(value.length);
            keyBuffer.put(key);
            valueBuffer.put(value);
            keyBuffer.flip();
            valueBuffer.flip();
            Entry ans = new Entry(keyBuffer, valueBuffer, Entry.NORMAL);

            Entry entry = dbFile.read(offset);
            Assert.assertEquals(ans, entry);
            offset += entry.getEntrySize();
        }
    }

    @Test
    public void testWriteNullValue() {
        long offset = dbFile.getOffset();
        String key = "key_test_null_value";
        Entry out = new Entry(key.getBytes(), new byte[0], Entry.DELETED);
        dbFile.write(out);
        Entry in = dbFile.read(offset);
        Assert.assertEquals(out, in);
    }

    @Test
    public void testReadNullEntry() {
        long offset = dbFile.getOffset();
        Entry entry = dbFile.read(offset);
        Assert.assertNull(entry);
    }

}
