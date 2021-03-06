/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.worker.keyvalue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import tachyon.Constants;
import tachyon.Sessions;
import tachyon.client.keyvalue.ByteBufferKeyValuePartitionReader;
import tachyon.client.keyvalue.Index;
import tachyon.client.keyvalue.PayloadReader;
import tachyon.exception.BlockDoesNotExistException;
import tachyon.exception.InvalidWorkerStateException;
import tachyon.exception.TachyonException;
import tachyon.thrift.KeyValueWorkerClientService;
import tachyon.thrift.TachyonTException;
import tachyon.thrift.ThriftIOException;
import tachyon.util.io.BufferUtils;
import tachyon.worker.block.BlockWorker;
import tachyon.worker.block.io.BlockReader;

/**
 * RPC service handler on worker side to read a local key-value block.
 */
// TODO(binfan): move logic outside and make this a simple wrapper.
@ThreadSafe
public final class KeyValueWorkerClientServiceHandler implements KeyValueWorkerClientService.Iface {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  /** BlockWorker handler for access block info */
  private final BlockWorker mBlockWorker;

  /**
   * @param blockWorker the {@link BlockWorker}
   */
  public KeyValueWorkerClientServiceHandler(BlockWorker blockWorker) {
    mBlockWorker = Preconditions.checkNotNull(blockWorker);
  }

  @Override
  public long getServiceVersion() {
    return Constants.KEY_VALUE_WORKER_SERVICE_VERSION;
  }

  /**
   * Gets the value for {@code key} in the given block, or null if key is not found.
   *
   * @param blockId block Id
   * @param key key to fetch
   * @return value or null if not found
   * @throws TachyonTException if an exception in Tachyon occurs
   * @throws ThriftIOException if a non-Tachyon related exception occurs
   */
  @Override
  public ByteBuffer get(long blockId, ByteBuffer key) throws TachyonTException, ThriftIOException {
    try {
      ByteBuffer value = getInternal(blockId, key);
      if (value == null) {
        return ByteBuffer.allocate(0);
      }
      return copyAsNonDirectBuffer(value);
    } catch (TachyonException e) {
      throw e.toTachyonTException();
    } catch (IOException e) {
      throw new ThriftIOException(e.getMessage());
    }
  }

  private ByteBuffer copyAsNonDirectBuffer(ByteBuffer directBuffer) {
    // Thrift assumes the ByteBuffer returned has array() method, which is not true if the
    // ByteBuffer is direct. We make a non-direct copy of the ByteBuffer to return.
    return BufferUtils.cloneByteBuffer(directBuffer);
  }

  /**
   * Internal logic to get value from the given block.
   *
   * @param blockId Block Id
   * @param keyBuffer bytes of key
   * @return key found in the key-value block or null if not found
   * @throws IOException if read operation failed
   * @throws BlockDoesNotExistException if the worker is not serving this block
   */
  private ByteBuffer getInternal(long blockId, ByteBuffer keyBuffer)
      throws BlockDoesNotExistException, IOException {
    final long sessionId = Sessions.KEYVALUE_SESSION_ID;
    final long lockId = mBlockWorker.lockBlock(sessionId, blockId);
    try {
      return getReader(sessionId, lockId, blockId).get(keyBuffer);
    } catch (InvalidWorkerStateException e) {
      // We shall never reach here
      LOG.error("Reaching invalid state to get a key", e);
    } finally {
      mBlockWorker.unlockBlock(lockId);
    }
    return null;
  }

  private ByteBufferKeyValuePartitionReader getReader(long sessionId, long lockId, long blockId)
      throws InvalidWorkerStateException, BlockDoesNotExistException, IOException {
    BlockReader blockReader = mBlockWorker.readBlockRemote(sessionId, blockId, lockId);
    ByteBuffer fileBuffer = blockReader.read(0, blockReader.getLength());
    ByteBufferKeyValuePartitionReader reader = new ByteBufferKeyValuePartitionReader(fileBuffer);
    // TODO(binfan): clean fileBuffer which is a direct byte buffer
    return reader;
  }

  @Override
  public List<ByteBuffer> getNextKeys(long blockId, ByteBuffer currentKey, int numKeys)
      throws TachyonTException, ThriftIOException {
    try {
      final long sessionId = Sessions.KEYVALUE_SESSION_ID;
      final long lockId = mBlockWorker.lockBlock(sessionId, blockId);
      try {
        ByteBufferKeyValuePartitionReader reader = getReader(sessionId, lockId, blockId);
        Index index = reader.getIndex();
        PayloadReader payloadReader = reader.getPayloadReader();

        List<ByteBuffer> ret = Lists.newArrayListWithExpectedSize(numKeys);
        for (int i = 0; i < numKeys; i ++) {
          ByteBuffer nextKey = index.nextKey(currentKey, payloadReader);
          if (nextKey == null) {
            break;
          }
          ret.add(copyAsNonDirectBuffer(nextKey));
          currentKey = nextKey;
        }
        return ret;
      } catch (InvalidWorkerStateException e) {
        // We shall never reach here
        LOG.error("Reaching invalid state to get all keys", e);
      } finally {
        mBlockWorker.unlockBlock(lockId);
      }
      return Collections.emptyList();
    } catch (TachyonException e) {
      throw e.toTachyonTException();
    } catch (IOException e) {
      throw new ThriftIOException(e.getMessage());
    }
  }

  // TODO(cc): Try to remove the duplicated try-catch logic in other methods like getNextKeys.
  @Override
  public int getSize(long blockId) throws TachyonTException, ThriftIOException {
    try {
      final long sessionId = Sessions.KEYVALUE_SESSION_ID;
      final long lockId = mBlockWorker.lockBlock(sessionId, blockId);
      try {
        return getReader(sessionId, lockId, blockId).size();
      } catch (InvalidWorkerStateException e) {
        // We shall never reach here
        LOG.error("Reaching invalid state to get size", e);
      } finally {
        mBlockWorker.unlockBlock(lockId);
      }
      return 0;
    } catch (TachyonException e) {
      throw e.toTachyonTException();
    } catch (IOException e) {
      throw new ThriftIOException(e.getMessage());
    }
  }
}
