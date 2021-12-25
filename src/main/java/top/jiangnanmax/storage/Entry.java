package top.jiangnanmax.storage;

import lombok.Data;
import top.jiangnanmax.util.BufferUtil;

import java.nio.ByteBuffer;
import java.util.Objects;

@Data
public class Entry {

    public static final int ENTRY_HEADER_SIZE = 8;
    public static final int NORMAL = 0x00000001;
    public static final int DELETED = 0x00000002;

    private ByteBuffer key;
    private ByteBuffer value;
    private int keySize;
    private int valueSize;
    private int mark;

    public Entry() {
    }

    public Entry(byte[] key, byte[] value) {
        this(ByteBuffer.wrap(key), ByteBuffer.wrap(value));
    }

    public Entry(byte[] key, byte[] value, int mark) {
        this(ByteBuffer.wrap(key), ByteBuffer.wrap(value), mark);
    }

    public Entry(ByteBuffer key, ByteBuffer value) {
        this(key, value, NORMAL);
    }

    public Entry(ByteBuffer key, ByteBuffer value, int mark) {
        this.key = key;
        this.value = value;
        this.keySize = key.capacity();
        this.valueSize = value.capacity();
        this.mark = mark;
    }

    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(getEntrySize());
        BufferUtil.writeUB3(buffer, keySize);
        BufferUtil.writeUB3(buffer, valueSize);
        BufferUtil.writeUB2(buffer, mark);
        buffer.put(key.array());
        buffer.put(value.array());
        buffer.flip();
        return buffer;
    }

    public Entry decodeHeader(ByteBuffer buffer) {
        keySize = BufferUtil.readUB3(buffer);
        valueSize = BufferUtil.readUB3(buffer);
        mark = BufferUtil.readUB2(buffer);
        return this;
    }

    public int getEntrySize() {
        return ENTRY_HEADER_SIZE + keySize + valueSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return keySize == entry.keySize && valueSize == entry.valueSize && mark == entry.mark &&
                Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, mark);
    }
}
