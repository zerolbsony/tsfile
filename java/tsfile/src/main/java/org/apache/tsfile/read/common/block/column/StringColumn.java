/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tsfile.read.common.block.column;

import org.apache.tsfile.block.column.ColumnEncoding;
import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.utils.Binary;

import java.util.Optional;

public class StringColumn extends AbstractBinaryColumn {

  public StringColumn(int initialCapacity) {
    super(initialCapacity);
  }

  public StringColumn(int positionCount, Optional<boolean[]> valueIsNull, Binary[] values) {
    super(positionCount, valueIsNull, values);
  }

  StringColumn(int arrayOffset, int positionCount, boolean[] valueIsNull, Binary[] values) {
    super(arrayOffset, positionCount, valueIsNull, values);
  }

  // called by getRegion which already knows the underlying retainedSizeInBytes
  protected StringColumn(
      int arrayOffset,
      int positionCount,
      boolean[] valueIsNull,
      Binary[] values,
      long retainedSizeInBytes) {
    super(arrayOffset, positionCount, valueIsNull, values, retainedSizeInBytes);
  }

  @Override
  public TSDataType getDataType() {
    return TSDataType.STRING;
  }

  @Override
  public ColumnEncoding getEncoding() {
    return ColumnEncoding.BINARY_ARRAY;
  }

  @Override
  public AbstractBinaryColumn instance(
      int arrayOffset, int positionCount, boolean[] valueIsNull, Binary[] values) {
    return new StringColumn(arrayOffset, positionCount, valueIsNull, values);
  }

  @Override
  public AbstractBinaryColumn instance(
      int arrayOffset,
      int positionCount,
      boolean[] valueIsNull,
      Binary[] values,
      long retainedSizeInBytes) {
    return new StringColumn(arrayOffset, positionCount, valueIsNull, values, retainedSizeInBytes);
  }
}
