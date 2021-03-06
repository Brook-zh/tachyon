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

package tachyon.network.protocol;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;

import io.netty.buffer.ByteBuf;

/**
 * This represents an RPC request to read a block from a DataServer.
 */
@ThreadSafe
public final class RPCBlockReadRequest extends RPCRequest {
  private final long mBlockId;
  private final long mOffset;
  private final long mLength;
  private final long mLockId;
  private final long mSessionId;

  /**
   * Constructs a new RPC request to read a block from a DataServer.
   *
   * @param blockId the id of the block
   * @param offset the block offset to begin reading at
   * @param length the number of bytes to read
   * @param lockId the id of the block lock that is held by the client
   * @param sessionId the id of the client session
   */
  public RPCBlockReadRequest(long blockId, long offset, long length, long lockId, long sessionId) {
    mBlockId = blockId;
    mOffset = offset;
    mLength = length;
    mLockId = lockId;
    mSessionId = sessionId;
  }

  @Override
  public Type getType() {
    return Type.RPC_BLOCK_READ_REQUEST;
  }

  /**
   * Decodes the input {@link ByteBuf} into a {@link RPCBlockReadRequest} object and returns it.
   *
   * @param in the input {@link ByteBuf}
   * @return The decoded RPCBlockReadRequest object
   */
  public static RPCBlockReadRequest decode(ByteBuf in) {
    long blockId = in.readLong();
    long offset = in.readLong();
    long length = in.readLong();
    long lockId = in.readLong();
    long sessionId = in.readLong();
    return new RPCBlockReadRequest(blockId, offset, length, lockId, sessionId);
  }

  @Override
  public int getEncodedLength() {
    // 5 longs (mBLockId, mOffset, mLength, mLockId, mSessionId)
    return Longs.BYTES * 5;
  }

  @Override
  public void encode(ByteBuf out) {
    out.writeLong(mBlockId);
    out.writeLong(mOffset);
    out.writeLong(mLength);
    out.writeLong(mLockId);
    out.writeLong(mSessionId);
  }

  @Override
  public void validate() {
    Preconditions.checkState(mOffset >= 0, "Offset cannot be negative: %s", mOffset);
    Preconditions.checkState(mLength >= 0 || mLength == -1,
        "Length cannot be negative (except for -1): %s", mLength);
  }

  @Override
  public String toString() {
    return String.format("RPCBlockReadRequest(%s, %s, %s, %s, %s)",
        mBlockId, mOffset, mLength, mLockId, mSessionId);
  }

  /**
   * @return the id of the block
   */
  public long getBlockId() {
    return mBlockId;
  }

  /**
   * @return the number of bytes to read
   */
  public long getLength() {
    return mLength;
  }

  /**
   * @return the block offset to begin reading at
   */
  public long getOffset() {
    return mOffset;
  }

  /**
   * @return the id of the lock
   */
  public long getLockId() {
    return mLockId;
  }

  /**
   * @return the id of the session
   */
  public long getSessionId() {
    return mSessionId;
  }
}
