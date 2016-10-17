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

package com.google.turbine.binder.bound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.turbine.binder.sym.ClassSymbol;
import com.google.turbine.binder.sym.FieldSymbol;
import com.google.turbine.binder.sym.MethodSymbol;
import com.google.turbine.binder.sym.TyVarSymbol;
import com.google.turbine.model.Const;
import com.google.turbine.tree.Tree;
import com.google.turbine.tree.Tree.MethDecl;
import com.google.turbine.type.Type;
import java.lang.annotation.RetentionPolicy;

/** A bound node that augments {@link HeaderBoundClass} with type information. */
public interface TypeBoundClass extends HeaderBoundClass {

  /** The super-class type. */
  Type.ClassTy superClassType();

  ImmutableMap<TyVarSymbol, TyVarInfo> typeParameterTypes();

  /** Declared fields. */
  ImmutableList<FieldInfo> fields();

  /** Declared methods. */
  ImmutableList<MethodInfo> methods();

  /** Retention policy for annotation declarations, {@code null} for other declarations. */
  RetentionPolicy retention();

  /** A type parameter declaration. */
  class TyVarInfo {
    private final Type superClassBound;
    private final ImmutableList<Type> interfaceBounds;

    public TyVarInfo(Type superClassBound, ImmutableList<Type> interfaceBounds) {
      this.superClassBound = superClassBound;
      this.interfaceBounds = interfaceBounds;
    }

    /** A class bound, or {@code null}. */
    public Type superClassBound() {
      return superClassBound;
    }

    /** Interface type bounds. */
    public ImmutableList<Type> interfaceBounds() {
      return interfaceBounds;
    }
  }

  /** A field declaration. */
  class FieldInfo {
    private final FieldSymbol sym;
    private final Type type;
    private final int access;
    private final ImmutableList<AnnoInfo> annotations;

    private final Tree.VarDecl decl;
    private final Const.Value value;

    public FieldInfo(
        FieldSymbol sym,
        Type type,
        int access,
        ImmutableList<AnnoInfo> annotations,
        Tree.VarDecl decl,
        Const.Value value) {
      this.sym = sym;
      this.type = type;
      this.access = access;
      this.annotations = annotations;
      this.decl = decl;
      this.value = value;
    }

    /** The field symbol. */
    public FieldSymbol sym() {
      return sym;
    }

    /** The field name. */
    public String name() {
      return sym.name();
    }

    /** The field type. */
    public Type type() {
      return type;
    }

    /** Access bits. */
    public int access() {
      return access;
    }

    /** The field's declaration. */
    public Tree.VarDecl decl() {
      return decl;
    }

    /** The constant field value. */
    public Const.Value value() {
      return value;
    }

    /** Declaration annotations. */
    public ImmutableList<AnnoInfo> annotations() {
      return annotations;
    }
  }

  /** An annotation use. */
  class AnnoInfo {
    private final ClassSymbol sym;
    private final ImmutableList<Tree.Expression> args;
    private final ImmutableMap<String, Const> values;

    public AnnoInfo(
        ClassSymbol sym, ImmutableList<Tree.Expression> args, ImmutableMap<String, Const> values) {
      this.sym = sym;
      this.args = args;
      this.values = values;
    }

    /** Arguments, either assignments or a single expression. */
    public ImmutableList<Tree.Expression> args() {
      return args;
    }

    /** Bound element-value pairs. */
    public ImmutableMap<String, Const> values() {
      return values;
    }

    /** The annotation's declaration. */
    public ClassSymbol sym() {
      return sym;
    }
  }

  /** A declared method. */
  class MethodInfo {
    private final MethodSymbol sym;
    private final ImmutableMap<TyVarSymbol, TyVarInfo> tyParams;
    private final Type returnType;
    private final ImmutableList<ParamInfo> parameters;
    private final ImmutableList<Type> exceptions;
    private final int access;
    private final Const defaultValue;
    private final MethDecl decl;
    private final ImmutableList<AnnoInfo> annotations;

    public MethodInfo(
        MethodSymbol sym,
        ImmutableMap<TyVarSymbol, TyVarInfo> tyParams,
        Type returnType,
        ImmutableList<ParamInfo> parameters,
        ImmutableList<Type> exceptions,
        int access,
        Const defaultValue,
        MethDecl decl,
        ImmutableList<AnnoInfo> annotations) {
      this.sym = sym;
      this.tyParams = tyParams;
      this.returnType = returnType;
      this.parameters = parameters;
      this.exceptions = exceptions;
      this.access = access;
      this.defaultValue = defaultValue;
      this.decl = decl;
      this.annotations = annotations;
    }

    /** The method symbol. */
    public MethodSymbol sym() {
      return sym;
    }

    /** The method name. */
    public String name() {
      return sym.name();
    }

    /** The type parameters */
    public ImmutableMap<TyVarSymbol, TyVarInfo> tyParams() {
      return tyParams;
    }

    /** Type return type, possibly {#link Type#VOID}. */
    public Type returnType() {
      return returnType;
    }

    /** The formal parameters. */
    public ImmutableList<ParamInfo> parameters() {
      return parameters;
    }

    /** Thrown exceptions. */
    public ImmutableList<Type> exceptions() {
      return exceptions;
    }

    /** Access bits. */
    public int access() {
      return access;
    }

    /** The default value of an annotation interface method. */
    public Const defaultValue() {
      return defaultValue;
    }

    /** The declaration. */
    public MethDecl decl() {
      return decl;
    }

    /** Declaration annotations. */
    public ImmutableList<AnnoInfo> annotations() {
      return annotations;
    }
  }

  /** A formal parameter declaration. */
  class ParamInfo {
    private final Type type;
    private final ImmutableList<AnnoInfo> annotations;
    private final boolean synthetic;

    public ParamInfo(Type type, ImmutableList<AnnoInfo> annotations, boolean synthetic) {
      this.type = type;
      this.annotations = annotations;
      this.synthetic = synthetic;
    }

    /** The parameter type. */
    public Type type() {
      return type;
    }

    /**
     * Returns true if the parameter is synthetic, e.g. the enclosing instance parameter in an inner
     * class constructor.
     */
    public boolean synthetic() {
      return synthetic;
    }

    /** Parameter annotations. */
    public ImmutableList<AnnoInfo> annotations() {
      return annotations;
    }
  }
}