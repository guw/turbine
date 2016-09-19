/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.turbine.bytecode;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.turbine.model.Const;
import com.google.turbine.model.Const.ShortValue;
import com.google.turbine.model.Const.Value;
import java.util.List;

/** Class file writing. */
public class ClassWriter {

  private static final int MAGIC = 0xcafebabe;
  private static final int MINOR_VERSION = 0;
  // TODO(cushon): configuration?
  private static final int MAJOR_VERSION = 52;

  /** Writes a {@link ClassFile} to bytecode. */
  public static byte[] writeClass(ClassFile classfile) {
    ConstantPool pool = new ConstantPool();
    ByteArrayDataOutput output = ByteStreams.newDataOutput();
    output.writeShort(classfile.access());
    output.writeShort(pool.classInfo(classfile.name()));
    output.writeShort(pool.classInfo(classfile.superName()));
    output.writeShort(classfile.interfaces().size());
    for (String i : classfile.interfaces()) {
      output.writeShort(pool.classInfo(i));
    }
    output.writeShort(classfile.fields().size());
    for (ClassFile.FieldInfo f : classfile.fields()) {
      writeField(pool, output, f);
    }
    output.writeShort(classfile.methods().size());
    for (ClassFile.MethodInfo m : classfile.methods()) {
      writeMethod(pool, output, m);
    }
    writeAttributes(pool, output, LowerAttributes.classAttributes(classfile));
    return finishClass(pool, output);
  }

  private static void writeMethod(
      ConstantPool pool, ByteArrayDataOutput output, ClassFile.MethodInfo method) {
    output.writeShort(method.access());
    output.writeShort(pool.utf8(method.name()));
    output.writeShort(pool.utf8(method.descriptor()));
    writeAttributes(pool, output, LowerAttributes.methodAttributes(method));
  }

  private static void writeField(
      ConstantPool pool, ByteArrayDataOutput output, ClassFile.FieldInfo field) {
    output.writeShort(field.access());
    output.writeShort(pool.utf8(field.name()));
    output.writeShort(pool.utf8(field.descriptor()));
    writeAttributes(pool, output, LowerAttributes.fieldAttributes(field));
  }

  private static void writeAttributes(
      ConstantPool pool, ByteArrayDataOutput body, List<Attribute> attributes) {
    body.writeShort(attributes.size());
    for (Attribute attribute : attributes) {
      new AttributeWriter(pool, body).write(attribute);
    }
  }

  static void writeConstantPool(ConstantPool constantPool, ByteArrayDataOutput output) {
    output.writeShort(constantPool.nextEntry);
    for (ConstantPool.Entry e : constantPool.constants()) {
      output.writeByte(e.kind().tag());
      Value value = e.value();
      switch (value.constantTypeKind()) {
        case STRING:
          output.writeUTF(((Const.StringValue) value).value());
          break;
        case SHORT:
          output.writeShort(((ShortValue) value).value());
          break;
        default:
          throw new AssertionError(value.constantTypeKind());
      }
    }
  }

  private static byte[] finishClass(ConstantPool pool, ByteArrayDataOutput body) {
    ByteArrayDataOutput result = ByteStreams.newDataOutput();
    result.writeInt(MAGIC);
    result.writeShort(MINOR_VERSION);
    result.writeShort(MAJOR_VERSION);
    writeConstantPool(pool, result);
    result.write(body.toByteArray());
    return result.toByteArray();
  }
}
