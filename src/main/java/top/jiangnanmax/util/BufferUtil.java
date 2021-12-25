package top.jiangnanmax.util;

import java.nio.ByteBuffer;

public class BufferUtil {

    public static void writeUB3(ByteBuffer buffer, int i) {
        buffer.put((byte) (i >> 16));
        buffer.put((byte) (i >> 8));
        buffer.put((byte) (i & 0xff));
    }

    public static void writeUB2(ByteBuffer buffer, int i) {
        buffer.put((byte) (i >> 8));
        buffer.put((byte) (i & 0xff));
    }

    public static byte read(ByteBuffer buffer) {
        return buffer.get();
    }

    public static int readUB2(ByteBuffer buffer) {
        int i = (read(buffer) & 0xff) << 8;
        i |= (read(buffer) & 0xff);
        return i;
    }

    public static int readUB3(ByteBuffer buffer) {
        int i = (read(buffer) & 0xff) << 16;
        i |= (read(buffer) & 0xff) << 8;
        i |= (read(buffer) & 0xff);
        return i;
    }

}
