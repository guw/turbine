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

package com.google.turbine.lower;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LowerIntegrationTest {

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> parameters() {
    String[] testCases = {
      "abstractenum.test",
      "access1.test",
      "anonymous.test",
      "asset.test",
      "outerparam.test",
      "basic_field.test",
      "basic_nested.test",
      "bcp.test",
      "builder.test",
      "byte.test",
      "byte2.test",
      "circ_cvar.test",
      "clash.test",
      "ctorvis.test",
      "cvar_qualified.test",
      "cycle.test",
      "default_fbound.test",
      "default_rawfbound.test",
      "default_simple.test",
      "enum1.test",
      "enumctor.test",
      "enumctor2.test",
      "enumimpl.test",
      "enumingeneric.test",
      "enuminner.test",
      "enumint.test",
      "enumint2.test",
      "enumint3.test",
      "enumint_byte.test",
      "enumint_objectmethod.test",
      "enumint_objectmethod2.test",
      "enumint_objectmethod_raw.test",
      "enuminthacks.test",
      "enumstat.test",
      "erasurebound.test",
      "existingctor.test",
      "extend_inner.test",
      "extends_bound.test",
      "extends_otherbound.test",
      "extendsandimplements.test",
      "extrainnerclass.test",
      "fbound.test",
      "firstcomparator.test",
      "fuse.test",
      "genericarrayfield.test",
      "genericexn.test",
      "genericexn2.test",
      "genericret.test",
      "hierarchy.test",
      "ibound.test",
      "icu.test",
      "icu2.test",
      "importinner.test",
      "innerctor.test",
      "innerenum.test",
      "innerint.test",
      "innerstaticgeneric.test",
      "interfacemem.test",
      "interfaces.test",
      "lexical.test",
      "lexical2.test",
      "lexical4.test",
      "list.test",
      "loopthroughb.test",
      "mapentry.test",
      "member.test",
      "mods.test",
      "morefields.test",
      "moremethods.test",
      "multifield.test",
      "nested.test",
      "nested2.test",
      "one.test",
      "outer.test",
      "packageprivateprotectedinner.test",
      "param_bound.test",
      "privateinner.test",
      "proto.test",
      "proto2.test",
      "qual.test",
      "raw.test",
      "raw2.test",
      "rawfbound.test",
      "rek.test",
      "samepkg.test",
      "self.test",
      "semi.test",
      "simple.test",
      "simplemethod.test",
      "string.test",
      "superabstract.test",
      "supplierfunction.test",
      "tbound.test",
      "typaram.test",
      "tyvarfield.test",
      "useextend.test",
      "vanillaexception.test",
      "varargs.test",
      "wild.test",
      "bytenoncanon.test",
      "canon.test",
      "genericnoncanon.test",
      "genericnoncanon1.test",
      "genericnoncanon10.test",
      "genericnoncanon2.test",
      "genericnoncanon3.test",
      "genericnoncanon4.test",
      "genericnoncanon5.test",
      "genericnoncanon6.test",
      "genericnoncanon8.test",
      "genericnoncanon9.test",
      "genericnoncanon_byte.test",
      "genericnoncanon_method3.test",
      "noncanon.test",
      "rawcanon.test",
      "wildboundcanon.test",
      "wildcanon.test",
      "annoconstvis.test",
      "const_byte.test",
      "const_char.test",
      "const_field.test",
      "const_types.test",
      "const_underscore.test",
      "constlevel.test",
      "constpack.test",
      "importconst.test",
      "const.test",
      "const_all.test",
      "const_arith.test",
      "const_conditional.test",
      "const_moreexpr.test",
      "const_multi.test",
      "field_anno.test",
      "annotation_bool_default.test",
      "annotation_class_default.test",
      "annotation_declaration.test",
      "annotation_enum_default.test",
      "annotations_default.test",
      "annouse.test",
      "annouse10.test",
      "annouse11.test",
      "annouse12.test",
      "annouse13.test",
      "annouse14.test",
      "annouse15.test",
      "annouse16.test",
      "annouse17.test",
      "annouse2.test",
      "annouse3.test",
      "annouse4.test",
      "annouse5.test",
      "annouse6.test",
      "annouse7.test",
      "annouse8.test",
      "annouse9.test",
      "annovis.test",
      "complex_param_anno.test",
      "enummemberanno.test",
      "innerannodecl.test",
      "source_anno_retention.test",
      "anno_nested.test",
      "nested_member_import.test",
      "nested_member_import_noncanon.test",
      "unary.test",
      "hex_int.test",
      "const_conv.test",
      "bmethod.test",
      "prim_class.test",
      "wild2.test",
      "wild3.test",
      "const_hiding.test",
    };
    List<Object[]> tests =
        ImmutableList.copyOf(testCases).stream().map(x -> new Object[] {x}).collect(toList());
    String testShardIndex = System.getenv("TEST_SHARD_INDEX");
    String testTotalShards = System.getenv("TEST_TOTAL_SHARDS");
    if (testShardIndex == null || testTotalShards == null) {
      return tests;
    }
    String shardFile = System.getenv("TEST_SHARD_STATUS_FILE");
    if (shardFile != null) {
      try {
        Files.write(Paths.get(shardFile), new byte[0]);
      } catch (IOException e) {
        throw new IOError(e);
      }
    }
    int index = Integer.parseInt(testShardIndex);
    int shards = Integer.parseInt(testTotalShards);
    return Lists.partition(tests, (tests.size() + shards - 1) / shards).get(index);
  }

  final String test;

  public LowerIntegrationTest(String test) {
    this.test = test;
  }

  static final ImmutableList<Path> BOOTCLASSPATH =
      ImmutableList.of(Paths.get(System.getProperty("java.home")).resolve("lib/rt.jar"));

  @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void test() throws Exception {

    IntegrationTestSupport.TestInput input =
        IntegrationTestSupport.TestInput.parse(
            new String(
                ByteStreams.toByteArray(getClass().getResourceAsStream("testdata/" + test)),
                UTF_8));

    ImmutableList<Path> classpathJar = ImmutableList.of();
    if (!input.classes.isEmpty()) {
      Map<String, byte[]> classpath =
          IntegrationTestSupport.runJavac(input.classes, null, BOOTCLASSPATH);
      Path lib = temporaryFolder.newFile("lib.jar").toPath();
      try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(lib))) {
        for (Map.Entry<String, byte[]> entry : classpath.entrySet()) {
          jos.putNextEntry(new JarEntry(entry.getKey() + ".class"));
          jos.write(entry.getValue());
        }
      }
      classpathJar = ImmutableList.of(lib);
    }

    Map<String, byte[]> expected =
        IntegrationTestSupport.runJavac(input.sources, classpathJar, BOOTCLASSPATH);

    Map<String, byte[]> actual =
        IntegrationTestSupport.runTurbine(input.sources, classpathJar, BOOTCLASSPATH);

    assertThat(IntegrationTestSupport.dump(IntegrationTestSupport.sortMembers(actual)))
        .isEqualTo(IntegrationTestSupport.dump(IntegrationTestSupport.canonicalize(expected)));
  }
}