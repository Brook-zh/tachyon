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

package tachyon.client.keyvalue;

import java.io.IOException;

import tachyon.annotation.PublicApi;
import tachyon.exception.TachyonException;

/**
 * Interface to be implemented in classes that support iterating over key-value pairs.
 *
 * TODO(cc): Try to get rid of KeyValueIterable and KeyValueIterator when TachyonException becomes
 * a subclass of IOException.
 */
@PublicApi
public interface KeyValueIterable {
  /**
   * @return a {@link KeyValueIterator} for iterating over key-value pairs in the store
   * @throws IOException if a non-Tachyon error occurs
   * @throws TachyonException if a Tachyon error occurs
   */
  KeyValueIterator iterator() throws IOException, TachyonException;
}
