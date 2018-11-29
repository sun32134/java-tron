package org.tron.core.db2.common;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.iq80.leveldb.WriteOptions;
import org.tron.common.storage.leveldb.LevelDbDataSourceImpl;
import org.tron.core.config.args.Args;
import org.tron.core.db.common.WrappedByteArray;
import org.tron.core.db.common.iterator.DBIterator;

public class LevelDB implements DB<byte[], byte[]> {
  @Getter
  private LevelDbDataSourceImpl db;
  private WriteOptions writeOptions = new WriteOptions()
      .sync(Args.getInstance().getStorage().isDbSync());

  public LevelDB(String parentName, String name) {
    db = new LevelDbDataSourceImpl(parentName, name);
    db.initDB();
  }

  @Override
  public byte[] get(byte[] key) {
    return db.getData(key);
  }

  @Override
  public void put(byte[] key, byte[] value) {
    db.putData(key, value);
  }

  @Override
  public void putAll(Map<byte[], byte[]> map) {
    db.updateByBatch(map, writeOptions);
  }

  @Override
  public long size() {
    return db.getTotal();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public void remove(byte[] key) {
    db.deleteData(key);
  }

  @Override
  public void clear() {
    reset();
  }

  @Override
  public Map<byte[], byte[]> asMap() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DBIterator iterator() {
    return db.iterator();
  }

  public void flush(Map<WrappedByteArray, WrappedByteArray> batch) {
    Map<byte[], byte[]> rows = batch.entrySet().stream()
        .map(e -> Maps.immutableEntry(e.getKey().getBytes(), e.getValue().getBytes()))
        .collect(HashMap::new, (m, k) -> m.put(k.getKey(), k.getValue()), HashMap::putAll);
    db.updateByBatch(rows, writeOptions);
  }

  public void close() {
    db.closeDB();
  }

  public void reset() {
    db.resetDb();
  }
}
