package top.jiangnanmax.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

public class TestDB {

    private DB db;
    private String dirPath = "/tmp/minidb";

    private String keyHeader;
    private String valueHeader;

    private int loop;

    @Before
    public void setup() {
        db = new DB(dirPath);
        keyHeader = "test_key_";
        valueHeader = "test_value_";
        loop = 10000;
    }

    @Test
    public void testPutAndGet() {
        // put
        for (int i = 0; i < loop; i++) {
            db.put(keyHeader + i, valueHeader + i);
        }
        // then get and compare
        for (int i = 0; i < loop; i++) {
            String value = db.get(keyHeader + i);
            Assert.assertEquals(valueHeader + i, value);
        }
    }

    @Test
    public void testDelete() {
        // random delete
        int caseCount = 1000;
        HashSet<Integer> set = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < caseCount; i++) {
            int index = random.nextInt(loop);
            if (!set.contains(index)) {
                Assert.assertTrue(db.delete(keyHeader + index));
            }
            set.add(index);
        }
        // then ensure deleted
        set.forEach(i -> {
            Assert.assertNull(db.get(keyHeader + i));
        });
    }
    @Test
    public void testGetNoExistKey() {
        for (int i = 0; i < loop; i++) {
            String value = db.get("minidb" + i);
            Assert.assertNull(value);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testKeyCheck() {
        // null key
        String value = db.get(null);
        // should get runtime exception
    }

}
