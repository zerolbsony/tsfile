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

import java.util.Optional;

@SuppressWarnings("java:S3012")
public class DateColumn extends AbstractIntColumn {

  public DateColumn(int initialCapacity) {
    super(initialCapacity);
  }

  public DateColumn(int positionCount, Optional<boolean[]> valueIsNull, int[] values) {
    super(positionCount, valueIsNull, values);
  }

  DateColumn(int arrayOffset, int positionCount, boolean[] valueIsNull, int[] values) {
    super(arrayOffset, positionCount, valueIsNull, values);
  }

  @Override
  public TSDataType getDataType() {
    return TSDataType.DATE;
  }

  @Override
  public ColumnEncoding getEncoding() {
    return ColumnEncoding.INT32_ARRAY;
  }

  @Override
  public AbstractIntColumn instance(
      int arrayOffset, int positionCount, boolean[] valueIsNull, int[] values) {
    return new DateColumn(arrayOffset, positionCount, valueIsNull, values);
  }
}
