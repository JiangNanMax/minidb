package top.jiangnanmax.db;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.jiangnanmax.storage.DBFile;
import top.jiangnanmax.storage.Entry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Data
public class DB {

    private DBFile dbFile;
    private String dirPath;
    private Map<String, Long> indexes;
    private ReadWriteLock mu;

    public DB(String dirPath) {
        this.dbFile = new DBFile(dirPath);
        this.dirPath = dirPath;
        loadIndexesFromFile();
        mu = new ReentrantReadWriteLock();
    }

    public void put(String key, String value) {
        keyCheck(key);
        mu.writeLock().lock();
        long offset = dbFile.getOffset();
        Entry entry = new Entry(key.getBytes(), value == null ? new byte[0] : value.getBytes());
        dbFile.write(entry);
        indexes.put(key, offset);
        mu.writeLock().unlock();
    }

    public String get(String key) {
        keyCheck(key);
        mu.readLock().lock();
        Long offset = indexes.get(key);
        String value;
        if (offset == null) {
            value = null;
        } else {
            Entry entry = dbFile.read(offset);
            value = new String(entry.getValue().array());
        }
        mu.readLock().unlock();
        return value;
    }

    public boolean delete(String key) {
        keyCheck(key);
        mu.writeLock().lock();
        Long offset = indexes.get(key);
        boolean ans = false;
        if (offset != null) {
            Entry entry = new Entry(key.getBytes(), new byte[0], Entry.DELETED);
            dbFile.write(entry);
            indexes.remove(key);
            ans = true;
        }
        mu.writeLock().unlock();
        return ans;
    }

    public void close() {
        dbFile.close();
    }

    private void keyCheck(String key) {
        if (key == null) {
            log.warn("null key");
            throw new RuntimeException("key cannot be null");
        }
    }

    private void loadIndexesFromFile() {
        this.indexes = new HashMap<>();
        long offset = 0;
        for (; ;) {
            Entry entry = dbFile.read(offset);
            if (entry == null) {
                log.info("load indexes from file, end in offset: {}", offset);
                break;
            }
            String key = new String(entry.getKey().array());
            if (entry.getMark() == Entry.DELETED) {
                indexes.remove(key);
            } else {
                indexes.put(key, offset);
            }
            offset += entry.getEntrySize();
        }
    }
}
