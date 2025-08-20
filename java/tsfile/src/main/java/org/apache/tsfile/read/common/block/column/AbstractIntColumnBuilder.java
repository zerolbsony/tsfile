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

import org.apache.tsfile.block.column.Column;
import org.apache.tsfile.block.column.ColumnBuilder;
import org.apache.tsfile.block.column.ColumnBuilderStatus;
import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.utils.DateUtils;
import org.apache.tsfile.utils.RamUsageEstimator;
import org.apache.tsfile.utils.TsPrimitiveType;
import org.apache.tsfile.write.UnSupportedDataTypeException;

import java.time.LocalDate;
import java.util.Arrays;

import static java.lang.Math.max;
import static org.apache.tsfile.read.common.block.column.ColumnUtil.calculateBlockResetSize;
import static org.apache.tsfile.utils.RamUsageEstimator.sizeOf;

public abstract class AbstractIntColumnBuilder implements ColumnBuilder {

  protected static final int INSTANCE_SIZE =
      (int) RamUsageEstimator.shallowSizeOfInstance(AbstractIntColumnBuilder.class);
  public static final IntColumn NULL_VALUE_BLOCK =
      new IntColumn(0, 1, new boolean[] {true}, new int[1]);

  protected final ColumnBuilderStatus columnBuilderStatus;
  protected boolean initialized;
  protected final int initialEntryCount;

  protected int positionCount;
  protected boolean hasNullValue;
  protected boolean hasNonNullValue;

  // it is assumed that these arrays are the same length
  protected boolean[] valueIsNull = new boolean[0];
  protected int[] values = new int[0];

  protected long retainedSizeInBytes;

  public AbstractIntColumnBuilder(ColumnBuilderStatus columnBuilderStatus, int expectedEntries) {
    this.columnBuilderStatus = columnBuilderStatus;
    this.initialEntryCount = max(expectedEntries, 1);

    updateDataSize();
  }

  @Override
  public int getPositionCount() {
    return positionCount;
  }

  @Override
  public ColumnBuilder writeInt(int value) {
    if (values.length <= positionCount) {
      growCapacity();
    }

    values[positionCount] = value;

    hasNonNullValue = true;
    positionCount++;
    if (columnBuilderStatus != null) {
      columnBuilderStatus.addBytes(AbstractIntColumn.SIZE_IN_BYTES_PER_POSITION);
    }
    return this;
  }

  /** Write an Object to the current entry, which should be the Integer type; */
  @Override
  public ColumnBuilder writeObject(Object value) {
    if (value instanceof Integer) {
      writeInt((Integer) value);
      return this;
    } else if (value instanceof LocalDate) {
      writeInt(DateUtils.parseDateExpressionToInt((LocalDate) value));
      return this;
    }
    throw new UnSupportedDataTypeException("IntegerColumn only support Integer data type");
  }

  @Override
  public ColumnBuilder write(Column column, int index) {
    return writeInt(column.getInt(index));
  }

  @Override
  public ColumnBuilder writeTsPrimitiveType(TsPrimitiveType value) {
    return writeInt(value.getInt());
  }

  @Override
  public ColumnBuilder appendNull() {
    if (values.length <= positionCount) {
      growCapacity();
    }

    valueIsNull[positionCount] = true;

    hasNullValue = true;
    positionCount++;
    if (columnBuilderStatus != null) {
      columnBuilderStatus.addBytes(AbstractIntColumn.SIZE_IN_BYTES_PER_POSITION);
    }
    return this;
  }

  @Override
  public Column build() {
    if (!hasNonNullValue) {
      return new RunLengthEncodedColumn(NULL_VALUE_BLOCK, positionCount);
    }
    if (getDataType() == TSDataType.INT32) {
      return new IntColumn(0, positionCount, hasNullValue ? valueIsNull : null, values);
    } else {
      return new DateColumn(0, positionCount, hasNullValue ? valueIsNull : null, values);
    }
  }

  @Override
  public abstract TSDataType getDataType();

  @Override
  public long getRetainedSizeInBytes() {
    return retainedSizeInBytes;
  }

  @Override
  public ColumnBuilder newColumnBuilderLike(ColumnBuilderStatus columnBuilderStatus) {
    return instance(columnBuilderStatus, calculateBlockResetSize(positionCount));
  }

  protected void growCapacity() {
    int newSize;
    if (initialized) {
      newSize = ColumnUtil.calculateNewArraySize(values.length);
    } else {
      newSize = initialEntryCount;
      initialized = true;
    }

    valueIsNull = Arrays.copyOf(valueIsNull, newSize);
    values = Arrays.copyOf(values, newSize);
    updateDataSize();
  }

  protected void updateDataSize() {
    retainedSizeInBytes = INSTANCE_SIZE + sizeOf(valueIsNull) + sizeOf(values);
    if (columnBuilderStatus != null) {
      retainedSizeInBytes += ColumnBuilderStatus.INSTANCE_SIZE;
    }
  }

  protected abstract AbstractIntColumnBuilder instance(
      ColumnBuilderStatus columnBuilderStatus, int expectedEntries);
}
