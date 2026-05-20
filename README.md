# Hello-Java

A hands-on, topic-by-topic walkthrough of the **Java** programming language
(targeting **Java 21 LTS**). Every topic is a runnable `.java` file with theory
in the top Javadoc and worked examples in `main()`. The expected console output
is also included as comments inside each `main()` so you can verify your run.

> **Project SDK: JDK 21** — set in `.idea/misc.xml`. Files under
> [`src/Phase9_ModernJavaAndModules/ModernJava`](src/Phase9_ModernJavaAndModules/ModernJava) and several others use
> Java 21 features (pattern matching for switch, record patterns, sequenced
> collections, virtual threads, …).

> Each section below links straight to the source file(s) in this repository.

---

## Learning Phases at a Glance

Work through the phases in order. Within a phase, files are self-contained —
read them in the order they appear.

| Phase | Theme | Sections |
|---|---|---|
| **0** | [Setup & First Programs](#phase-0--setup--first-programs) | Hello world, JVM/JDK/JRE, Input / Output |
| **1** | [Core Language](#phase-1--core-language) | Types, variables, operators, control flow, enums |
| **2** | [Methods, Arrays, Strings](#phase-2--methods-arrays-strings) | The everyday building blocks |
| **3** | [Object Orientation](#phase-3--object-orientation) | Classes, interfaces, nested classes |
| **4** | [Errors & Type Safety](#phase-4--errors--type-safety) | Exceptions, Optional, Generics, Annotations |
| **5** | [Collections, Lambdas & Streams](#phase-5--collections-lambdas--streams) | Data structures + functional pipelines |
| **6** | [Runtime, Memory, Regex, Reflection](#phase-6--runtime-memory-regex-reflection) | Below the surface |
| **7** | [Concurrency](#phase-7--concurrency) | Threads, synchronization, virtual threads |
| **8** | [Practical APIs](#phase-8--practical-apis) | File I/O, Date & Time, HTTP Client |
| **9** | [Modern Java & Modules](#phase-9--modern-java--modules) | Java 10 → 21 features, JPMS |

Estimated pace if you read one phase per week: 9 weeks. Bear down on the
projects (Banking, Employee, Face Detection, Snake Game) as you reach them —
they're where the concepts click.

---

## Phase 0 — Setup & First Programs

> Get a JDK installed, understand what the JDK / JRE / JVM are, write your
> first `main()`, and figure out how Java reads and writes the console.

### First Programs

| Topic | Source |
|-------|--------|
| Hello World | [HelloWorld.java](src/Phase0_SetupAndFirstPrograms/HelloWorld.java) |
| Is `main` compulsory? | [IsMainCompulsory.java](src/Phase0_SetupAndFirstPrograms/IsMainCompulsory.java) |
| Class name vs file name | [ClassNameMyth/Hello.java](src/Phase0_SetupAndFirstPrograms/ClassNameMyth/Hello.java) |

### Foundations & Tooling

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction to Java | [Introduction.java](src/Phase0_SetupAndFirstPrograms/Introduction/Introduction.java) |
| 2 | Download and Install Java | [InstallJava.java](src/Phase0_SetupAndFirstPrograms/Installation/InstallJava.java) |
| 3 | JDK vs JRE vs JVM | [JDKvsJREvsJVM.java](src/Phase0_SetupAndFirstPrograms/JDKvsJREvsJVM/JDKvsJREvsJVM.java) |
| 4 | Taking Input | [Scanner](src/Phase0_SetupAndFirstPrograms/Input/ScannerInput.java) · [BufferedReader](src/Phase0_SetupAndFirstPrograms/Input/BufferedReaderInput.java) · [Console](src/Phase0_SetupAndFirstPrograms/Input/ConsoleInput.java) |
| 5 | Printing Output | [PrintMethods.java](src/Phase0_SetupAndFirstPrograms/Output/PrintMethods.java) · [FormattedOutput.java](src/Phase0_SetupAndFirstPrograms/Output/FormattedOutput.java) |

---

## Phase 1 — Core Language

> Identifiers, keywords, types, variables, operators, control flow, enums.
> Master this and every later phase becomes easier.

| # | Topic | Source |
|---|-------|--------|
| 1 | Identifiers | [Identifiers.java](src/Phase1_CoreLanguage/Identifiers/Identifiers.java) |
| 2 | Keywords | [Keywords.java](src/Phase1_CoreLanguage/Keywords/Keywords.java) |
| 3 | Data Types (Intro) | [DataTypesIntro.java](src/Phase1_CoreLanguage/DataTypes/DataTypesIntro.java) |
| 4 | Primitive Data Types | [byte](src/Phase1_CoreLanguage/PrimitiveDataTypes/ByteExample.java) · [short](src/Phase1_CoreLanguage/PrimitiveDataTypes/ShortExample.java) · [int](src/Phase1_CoreLanguage/PrimitiveDataTypes/IntegerExample.java) · [long](src/Phase1_CoreLanguage/PrimitiveDataTypes/LongExample.java) · [float](src/Phase1_CoreLanguage/PrimitiveDataTypes/FloatExample.java) · [double](src/Phase1_CoreLanguage/PrimitiveDataTypes/DoubleExample.java) · [char](src/Phase1_CoreLanguage/PrimitiveDataTypes/CharExample.java) · [boolean](src/Phase1_CoreLanguage/PrimitiveDataTypes/BooleanExample.java) |
| 5 | Wrapper Classes | [WrapperClassesIntro.java](src/Phase1_CoreLanguage/WrapperClasses/WrapperClassesIntro.java) · [AutoboxingUnboxing.java](src/Phase1_CoreLanguage/WrapperClasses/AutoboxingUnboxing.java) |
| 6 | Variables | [Instance](src/Phase1_CoreLanguage/Variables/InstanceVariableExample.java) · [Local](src/Phase1_CoreLanguage/Variables/LocalVariableExample.java) · [Static](src/Phase1_CoreLanguage/Variables/StaticVariableExample.java) · [Scope](src/Phase1_CoreLanguage/Variables/ScopeOfVariable.java) |
| 7 | Operators | [Arithmetic](src/Phase1_CoreLanguage/Operators/ArithmeticOperators.java) · [Relational](src/Phase1_CoreLanguage/Operators/RelationalOperators.java) · [Logical](src/Phase1_CoreLanguage/Operators/LogicalOperators.java) · [Bitwise & Shift](src/Phase1_CoreLanguage/Operators/BitwiseOperators.java) · [Assignment](src/Phase1_CoreLanguage/Operators/AssignmentOperators.java) · [Unary](src/Phase1_CoreLanguage/Operators/UnaryOperators.java) · [Ternary](src/Phase1_CoreLanguage/Operators/TernaryOperator.java) · [instanceof](src/Phase1_CoreLanguage/Operators/InstanceofOperator.java) |
| 8 | Decision Making | [if](src/Phase1_CoreLanguage/DecisionMaking/IfStatement.java) · [if-else](src/Phase1_CoreLanguage/DecisionMaking/IfElseStatement.java) · [if-else-if](src/Phase1_CoreLanguage/DecisionMaking/IfElseIfStatement.java) · [nested-if](src/Phase1_CoreLanguage/DecisionMaking/NestedIfStatement.java) · [switch](src/Phase1_CoreLanguage/DecisionMaking/SwitchCaseStatement.java) |
| 9 | Loops and Jump Statements | [for](src/Phase1_CoreLanguage/Loops/ForLoop.java) · [while](src/Phase1_CoreLanguage/Loops/WhileLoop.java) · [do-while](src/Phase1_CoreLanguage/Loops/DoWhileLoop.java) · [enhanced for](src/Phase1_CoreLanguage/Loops/ForEachOrEnhanceLoop.java) · [infinite loop](src/Phase1_CoreLanguage/Loops/InfiniteLoop.java) · [for loop notes](src/Phase1_CoreLanguage/Loops/ForLoopImportant.java) · [break](src/Phase1_CoreLanguage/DecisionMaking/JumpStatement/BreakStatement.java) · [continue](src/Phase1_CoreLanguage/DecisionMaking/JumpStatement/ContinueStatement.java) · [return](src/Phase1_CoreLanguage/DecisionMaking/JumpStatement/ReturnStatement.java) · [labels](src/Phase1_CoreLanguage/DecisionMaking/JumpStatement/LabelStatement.java) |
| 10 | Type Conversion | [Automatic](src/Phase1_CoreLanguage/Conversion/AutomaticTypeConversion.java) · [Explicit](src/Phase1_CoreLanguage/Conversion/ExplicitTypeConversion.java) · [Widening](src/Phase1_CoreLanguage/Conversion/WideningPrimitive.java) |
| 11 | Comments | [Single-line](src/Phase1_CoreLanguage/Comments/SingleLineComments.java) · [Multi-line](src/Phase1_CoreLanguage/Comments/MultiLineComment.java) · [Documentation (Javadoc)](src/Phase1_CoreLanguage/Comments/DocumentationComments.java) |
| 12 | Enumerations | [Intro](src/Phase1_CoreLanguage/Enumerations/InsideClassEnum.java) · [Outside class](src/Phase1_CoreLanguage/Enumerations/OutsideClassEnum.java) · [Custom values](src/Phase1_CoreLanguage/Enumerations/EnumWithCustomizedVal.java) · [Constructor](src/Phase1_CoreLanguage/Enumerations/EnumConstructor.java) · [Methods](src/Phase1_CoreLanguage/Enumerations/EnumMethods.java) · [In switch](src/Phase1_CoreLanguage/Enumerations/EnumWithSwitchCase.java) · [Main inside enum](src/Phase1_CoreLanguage/Enumerations/MainInsideEnum.java) |
| 13 | Special Keywords | [final](src/Phase1_CoreLanguage/SpecialKeyword/FinalKeywordExample.java) |
| 14 | Extras / Interview Tidbits | [Facts about null](src/Phase1_CoreLanguage/Extra/FactsAboutNull.java) · [Underscore in numbers](src/Phase1_CoreLanguage/Extra/UnderscoreInNumber.java) · [Does Java support goto?](src/Phase1_CoreLanguage/Extra/IsJavaSupportGoto.java) · [Object memory allocation](src/Phase1_CoreLanguage/Extra/ObjectMemoryAllocation.java) · [Function currying](src/Phase1_CoreLanguage/Extra/FunctionCurrying.java) · [Binary search](src/Phase1_CoreLanguage/Extra/BinarySearch.java) · [Sorting](src/Phase1_CoreLanguage/Extra/Sorting.java) |

---

## Phase 2 — Methods, Arrays, Strings

> The bread-and-butter library types and the keyword you'll use the most.
> Strings are immutable; arrays are fixed-size; methods are how you organise
> behaviour.

### Methods

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (syntax, overloading, pass-by-value, recursion) | [MethodIntroduction.java](src/Phase2_MethodsArraysStrings/Methods/MethodIntroduction.java) |
| 2 | Static Methods vs Instance Methods | [StaticVsInstanceMethods.java](src/Phase2_MethodsArraysStrings/Methods/StaticVsInstanceMethods.java) |
| 3 | Access Modifiers (public / protected / package-private / private) | [AccessModifiers.java](src/Phase2_MethodsArraysStrings/Methods/AccessModifiers.java) |
| 4 | Command Line Arguments (`String[] args`) | [CommandLineArguments.java](src/Phase2_MethodsArraysStrings/Methods/CommandLineArguments.java) |
| 5 | Variable Arguments (Varargs `...`) | [Varargs.java](src/Phase2_MethodsArraysStrings/Methods/Varargs.java) |
| 6 | Method References (`::`) — Java 8+ | [MethodReferences.java](src/Phase2_MethodsArraysStrings/Methods/MethodReferences.java) |
| 7 | Interface Methods — `default` / `static` / `private` — Java 8/9 | [InterfaceMethods.java](src/Phase2_MethodsArraysStrings/Methods/InterfaceMethods.java) |

### Arrays

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (declare, init, iterate, defaults, pitfalls) | [ArrayIntroduction.java](src/Phase2_MethodsArraysStrings/Arrays/ArrayIntroduction.java) |
| 2 | Multi-Dimensional Arrays (2D, 3D, `deepToString`) | [MultiDimensionalArrays.java](src/Phase2_MethodsArraysStrings/Arrays/MultiDimensionalArrays.java) |
| 3 | Jagged Arrays (varying row lengths, Pascal's triangle) | [JaggedArrays.java](src/Phase2_MethodsArraysStrings/Arrays/JaggedArrays.java) |
| 4 | `java.util.Arrays` utility class (sort, search, copy, fill, equals, stream) | [ArraysClass.java](src/Phase2_MethodsArraysStrings/Arrays/ArraysClass.java) |
| 5 | Final Arrays (reference vs contents, immutability patterns) | [FinalArrays.java](src/Phase2_MethodsArraysStrings/Arrays/FinalArrays.java) |

### Strings

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (pool, intern, char[] bridge, compact strings) | [StringIntroduction.java](src/Phase2_MethodsArraysStrings/Strings/StringIntroduction.java) |
| 2 | Why Strings are Immutable (pool, threads, security, hash caching) | [StringImmutability.java](src/Phase2_MethodsArraysStrings/Strings/StringImmutability.java) |
| 3 | String Concatenation (`+`, `concat`, `join`, `StringJoiner`, perf trap) | [StringConcatenation.java](src/Phase2_MethodsArraysStrings/Strings/StringConcatenation.java) |
| 4 | String Methods (every important method by category) | [StringMethods.java](src/Phase2_MethodsArraysStrings/Strings/StringMethods.java) |
| 5 | StringBuffer Class (synchronized, full method reference) | [StringBufferClass.java](src/Phase2_MethodsArraysStrings/Strings/StringBufferClass.java) |
| 6 | StringBuilder Class (non-synchronized, full method reference) | [StringBuilderClass.java](src/Phase2_MethodsArraysStrings/Strings/StringBuilderClass.java) |
| 7 | String vs StringBuffer vs StringBuilder (table + benchmark) | [StringVsBufferVsBuilder.java](src/Phase2_MethodsArraysStrings/Strings/StringVsBufferVsBuilder.java) |
| 8 | Modern Features — Java 11/12/15/21 (`strip`, `repeat`, `lines`, `transform`, text blocks, `case String s when …`) | [ModernStringFeatures.java](src/Phase2_MethodsArraysStrings/Strings/ModernStringFeatures.java) |

---

## Phase 3 — Object Orientation

> Java's organising paradigm — classes, objects, and the four pillars
> (abstraction, encapsulation, inheritance, polymorphism). Then interfaces,
> the second pillar of abstraction, and nested classes that finish the
> picture.

### OOP Concepts

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (the four pillars in one mini-demo) | [OopIntroduction.java](src/Phase3_ObjectOrientation/Introduction/OopIntroduction.java) |
| 2 | Classes & Objects | [Initializing object](src/Phase3_ObjectOrientation/ClassesAndObject/InitializingObject.java) · [Ways to create an object](src/Phase3_ObjectOrientation/ClassesAndObject/WaysToCreateObject.java) |
| 3 | Constructors (default / parameterized / overloading / chaining / copy / private) | [Constructors.java](src/Phase3_ObjectOrientation/Constructors/Constructors.java) |
| 4 | Object Class (equals / hashCode / toString / clone / getClass / records) | [ObjectClassMethods.java](src/Phase3_ObjectOrientation/ObjectClass/ObjectClassMethods.java) |
| 5 | Abstraction | [Abstract class](src/Phase3_ObjectOrientation/Abstraction/AbstractClassExample.java) · [Interfaces](src/Phase3_ObjectOrientation/Abstraction/InterfaceExample.java) · [Abstract vs Interface](src/Phase3_ObjectOrientation/Abstraction/AbstractClassVsInterface.java) |
| 6 | Encapsulation | [TestEncapsulation.java](src/Phase3_ObjectOrientation/Encapsulation/TestEncapsulation.java) |
| 7 | Inheritance | [Intro](src/Phase3_ObjectOrientation/Inheritance/InheritanceJava.java) · [Single](src/Phase3_ObjectOrientation/Inheritance/TypesOfInheritance/SingleInheritance.java) · [Multi-level](src/Phase3_ObjectOrientation/Inheritance/TypesOfInheritance/MultiLevelInheritance.java) · [Hierarchical](src/Phase3_ObjectOrientation/Inheritance/TypesOfInheritance/HierarchicalInheritance.java) · [Multiple (interfaces)](src/Phase3_ObjectOrientation/Inheritance/TypesOfInheritance/MultipleInheritance.java) · [Hybrid](src/Phase3_ObjectOrientation/Inheritance/TypesOfInheritance/HybridInheritance.java) |
| 8 | Polymorphism (overloading + overriding + dynamic dispatch + covariant returns) | [Polymorphism.java](src/Phase3_ObjectOrientation/Polymorphism/Polymorphism.java) |
| 9 | Packages and Imports (single / wildcard / static, package-private access) | [PackagesAndImports.java](src/Phase3_ObjectOrientation/Packages/PackagesAndImports.java) |
| 10 | Sealed Classes — Java 17+ (`sealed` / `non-sealed` / `permits`) | [SealedClassesDemo.java](src/Phase3_ObjectOrientation/SealedClasses/SealedClassesDemo.java) |
| 11 | **Project: Simple Banking Application** | [Account](src/Phase3_ObjectOrientation/BankingApp/Account.java) · [SavingsAccount](src/Phase3_ObjectOrientation/BankingApp/SavingsAccount.java) · [CheckingAccount](src/Phase3_ObjectOrientation/BankingApp/CheckingAccount.java) · [Transaction (record)](src/Phase3_ObjectOrientation/BankingApp/Transaction.java) · [Bank](src/Phase3_ObjectOrientation/BankingApp/Bank.java) · **[Runner: BankingApp.java](src/Phase3_ObjectOrientation/BankingApp/BankingApp.java)** |
| 12 | Serialization (extra) | [Demo 1](src/Phase3_ObjectOrientation/SerializationDeserialization/SerializationDeserializationDemoOne.java) · [Demo 2](src/Phase3_ObjectOrientation/SerializationDeserialization/SerializationDeserializationDemoTwo.java) |

### Interfaces

| # | Topic | Source |
|---|-------|--------|
| 1 | Interfaces — full tour (abstract / default / static / private members, multiple impl, interface inheritance) | [InterfaceIntro.java](src/Phase3_ObjectOrientation/Interfaces/InterfaceIntro.java) |
| 2 | Class vs Interface (side-by-side, real-world JDK pattern) | [ClassVsInterface.java](src/Phase3_ObjectOrientation/Interfaces/ClassVsInterface.java) · [longer version](src/Phase3_ObjectOrientation/Abstraction/AbstractClassVsInterface.java) |
| 3 | Functional Interface (SAM, `@FunctionalInterface`, `java.util.function`, composition, lambdas, comparators) | [FunctionalInterfaceDemo.java](src/Phase3_ObjectOrientation/Interfaces/FunctionalInterfaceDemo.java) |
| 4 | Nested Interface (inside class, inside interface, `private` nested) | [NestedInterface.java](src/Phase3_ObjectOrientation/Interfaces/NestedInterface.java) |
| 5 | Marker Interface (`Serializable`, custom marker, annotation alternative, generic upper bound) | [MarkerInterface.java](src/Phase3_ObjectOrientation/Interfaces/MarkerInterface.java) |
| 6 | Sealed Interfaces — Java 17+ (`permits`, `non-sealed`, exhaustive switch) | [SealedInterfaceDemo.java](src/Phase3_ObjectOrientation/Interfaces/SealedInterfaceDemo.java) |
| 7 | **Project: Employee Management System** | [Employee (sealed)](src/Phase3_ObjectOrientation/EmployeeApp/Employee.java) · [FullTimeEmployee](src/Phase3_ObjectOrientation/EmployeeApp/FullTimeEmployee.java) · [PartTimeEmployee](src/Phase3_ObjectOrientation/EmployeeApp/PartTimeEmployee.java) · [Contractor](src/Phase3_ObjectOrientation/EmployeeApp/Contractor.java) · [Intern](src/Phase3_ObjectOrientation/EmployeeApp/Intern.java) · [Promotable](src/Phase3_ObjectOrientation/EmployeeApp/Promotable.java) · [Auditable (marker)](src/Phase3_ObjectOrientation/EmployeeApp/Auditable.java) · [EmployeeFilter (functional)](src/Phase3_ObjectOrientation/EmployeeApp/EmployeeFilter.java) · [EmployeeRepository](src/Phase3_ObjectOrientation/EmployeeApp/EmployeeRepository.java) · **[Runner: EmployeeApp.java](src/Phase3_ObjectOrientation/EmployeeApp/EmployeeApp.java)** |

### Nested & Inner Classes

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (the four kinds at a glance) | [NestedClassesIntroduction.java](src/Phase3_ObjectOrientation/NestedClasses/NestedClassesIntroduction.java) |
| 2 | Static Nested Classes (Builder, nested records, hidden helpers) | [StaticNestedClass.java](src/Phase3_ObjectOrientation/NestedClasses/StaticNestedClass.java) |
| 3 | Inner (member) Classes (`Outer.this`, leak risk, iterators) | [InnerClass.java](src/Phase3_ObjectOrientation/NestedClasses/InnerClass.java) |
| 4 | Local & Anonymous Classes (capture rules, lambda vs anonymous) | [LocalAndAnonymousClass.java](src/Phase3_ObjectOrientation/NestedClasses/LocalAndAnonymousClass.java) |

---

## Phase 4 — Errors & Type Safety

> Mechanisms for handling things going wrong, making "absent values" explicit
> in the type system, parameterising types safely, and decorating code with
> metadata that tools can read.

### Exception Handling

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (hierarchy, checked vs unchecked, stack trace) | [ExceptionIntroduction.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/ExceptionIntroduction.java) |
| 2 | Try-Catch Block (single / multiple / multi-catch / nested / `finally`) | [TryCatchBlock.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/TryCatchBlock.java) |
| 3 | `final`, `finally`, `finalize` — three confusable keywords | [FinalFinallyFinalize.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/FinalFinallyFinalize.java) |
| 4 | `throw` and `throws` — raise vs declare, propagation, re-throw | [ThrowAndThrows.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/ThrowAndThrows.java) |
| 5 | Custom Exceptions (checked + unchecked, carrying extra data) | [CustomException.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/CustomException.java) |
| 6 | Chained Exceptions (`getCause`, `initCause`, suppressed vs cause) | [ChainedException.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/ChainedException.java) |
| 7 | Null Pointer Exceptions (six causes, helpful NPE Java 14+, `Optional`) | [NullPointerExceptions.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/NullPointerExceptions.java) |
| 8 | Exception Handling with Method Overriding (the `throws` rule) | [ExceptionInOverriding.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/ExceptionInOverriding.java) |
| 9 | Try-with-resources — Java 7+ / 9+ (`AutoCloseable`, suppressed exceptions) | [TryWithResources.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/TryWithResources.java) |
| 10 | Best Practices (top-10 dos & don'ts with code) | [ExceptionBestPractices.java](src/Phase4_ErrorsAndTypeSafety/ExceptionHandling/ExceptionBestPractices.java) |

### `Optional<T>`

| # | Topic | Source |
|---|-------|--------|
| 1 | `Optional<T>` (creation, transforms, anti-patterns, primitive variants) | [OptionalDemo.java](src/Phase4_ErrorsAndTypeSafety/OptionalType/OptionalDemo.java) |

### Generics

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (before/after, type parameter conventions) | [GenericsIntroduction.java](src/Phase4_ErrorsAndTypeSafety/Generics/GenericsIntroduction.java) |
| 2 | Generic Classes (single + multi-param, inheritance, diamond) | [GenericClasses.java](src/Phase4_ErrorsAndTypeSafety/Generics/GenericClasses.java) |
| 3 | Generic Methods (type inference, method-level type params) | [GenericMethods.java](src/Phase4_ErrorsAndTypeSafety/Generics/GenericMethods.java) |
| 4 | Generic Interfaces (Comparable, Function, custom contracts) | [GenericInterfaces.java](src/Phase4_ErrorsAndTypeSafety/Generics/GenericInterfaces.java) |
| 5 | Bounded Type Parameters (`<T extends X>`, multi-bound `&`) | [BoundedTypeParameters.java](src/Phase4_ErrorsAndTypeSafety/Generics/BoundedTypeParameters.java) |
| 6 | Wildcards (`?`, `? extends`, `? super`) | [Wildcards.java](src/Phase4_ErrorsAndTypeSafety/Generics/Wildcards.java) |
| 7 | PECS Principle (Producer Extends, Consumer Super) | [PecsPrinciple.java](src/Phase4_ErrorsAndTypeSafety/Generics/PecsPrinciple.java) |
| 8 | Type Erasure (runtime behaviour, bridge methods, reflection) | [TypeErasure.java](src/Phase4_ErrorsAndTypeSafety/Generics/TypeErasure.java) |
| 9 | Generic Restrictions (no primitives / no `new T()` / no generic arrays / no parameterised `instanceof`) | [GenericRestrictions.java](src/Phase4_ErrorsAndTypeSafety/Generics/GenericRestrictions.java) |
| 10 | Recursive Type Bounds (`<T extends Comparable<T>>`, self-typed builders, `Enum<E extends Enum<E>>`) | [RecursiveTypeBounds.java](src/Phase4_ErrorsAndTypeSafety/Generics/RecursiveTypeBounds.java) |
| 11 | Heap Pollution + `@SafeVarargs` | [HeapPollutionAndSafeVarargs.java](src/Phase4_ErrorsAndTypeSafety/Generics/HeapPollutionAndSafeVarargs.java) |
| 12 | Modern Generics — Java 7 → 21 (diamond, `var`, generic records, sealed generic interfaces, **generic record patterns in switch**) | [ModernGenerics.java](src/Phase4_ErrorsAndTypeSafety/Generics/ModernGenerics.java) |

### Annotations

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (the four families, retention levels) | [AnnotationsIntroduction.java](src/Phase4_ErrorsAndTypeSafety/Annotations/AnnotationsIntroduction.java) |
| 2 | Built-In Annotations (`@Override`, `@Deprecated`, `@SuppressWarnings`, `@FunctionalInterface`, `@SafeVarargs`) | [BuiltInAnnotations.java](src/Phase4_ErrorsAndTypeSafety/Annotations/BuiltInAnnotations.java) |
| 3 | Custom Annotations (`@Retention`, `@Target`, `@Repeatable`, `@Inherited`, type-use) | [CustomAnnotations.java](src/Phase4_ErrorsAndTypeSafety/Annotations/CustomAnnotations.java) |
| 4 | Runtime Annotations (reading via reflection, mini AOP) | [RuntimeAnnotations.java](src/Phase4_ErrorsAndTypeSafety/Annotations/RuntimeAnnotations.java) |

---

## Phase 5 — Collections, Lambdas & Streams

> Pick the right container, then transform data declaratively with lambdas
> and the Stream API.

### Collections

#### 0. Framework Overview

| Topic | Source |
|---|---|
| Framework Introduction (hierarchy diagram, big-O cheatsheet) | [CollectionsIntroduction.java](src/Phase5_CollectionsLambdasStreams/Collections/CollectionsIntroduction.java) |
| Modern Features — Java 8 → 21 (factories, Collectors, `Stream.toList`, **Sequenced Collections**) | [ModernCollections.java](src/Phase5_CollectionsLambdasStreams/Collections/ModernCollections.java) |

#### 1. Core Interfaces

| Topic | Source |
|---|---|
| `Collection` Interface | [CollectionInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/CollectionInterface.java) |
| `List` Interface | [ListInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/ListInterface.java) |
| `Set` Interface | [SetInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/SetInterface.java) |
| `Queue` Interface | [QueueInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/QueueInterface.java) |
| `Deque` Interface | [DequeInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/DequeInterface.java) |
| `Map` Interface | [MapInterface.java](src/Phase5_CollectionsLambdasStreams/Collections/MapInterface.java) |

#### 2. List Implementations

| Topic | Source |
|---|---|
| `ArrayList` | [ArrayListDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ArrayListDemo.java) |
| `LinkedList` | [LinkedListDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/LinkedListDemo.java) |
| `Vector` + `Stack` (legacy) | [VectorAndStack.java](src/Phase5_CollectionsLambdasStreams/Collections/VectorAndStack.java) |
| `AbstractList` + `AbstractSequentialList` (skeleton classes) | [AbstractListClasses.java](src/Phase5_CollectionsLambdasStreams/Collections/AbstractListClasses.java) |

#### 3. Set Implementations

| Topic | Source |
|---|---|
| `HashSet` | [HashSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/HashSetDemo.java) |
| `LinkedHashSet` | [LinkedHashSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/LinkedHashSetDemo.java) |
| `TreeSet` | [TreeSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/TreeSetDemo.java) |
| `EnumSet` (bit-vector enum keys) | [EnumSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/EnumSetDemo.java) |
| `SortedSet` + `NavigableSet` interfaces | [SortedAndNavigableSet.java](src/Phase5_CollectionsLambdasStreams/Collections/SortedAndNavigableSet.java) |
| `ConcurrentSkipListSet` | [ConcurrentSkipListSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ConcurrentSkipListSetDemo.java) |

#### 4. Queue / Deque Implementations

| Topic | Source |
|---|---|
| `PriorityQueue` (heap, Top-K) | [PriorityQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/PriorityQueueDemo.java) |
| `ArrayDeque` (modern stack + queue) | [ArrayDequeDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ArrayDequeDemo.java) |
| `BlockingQueue` (ArrayBlockingQueue / LinkedBlockingQueue / SynchronousQueue) | [BlockingQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/BlockingQueueDemo.java) |
| `ConcurrentLinkedQueue` (lock-free) | [ConcurrentLinkedQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ConcurrentLinkedQueueDemo.java) |
| `AbstractQueue` (skeleton class + custom BoundedQueue) | [AbstractQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/AbstractQueueDemo.java) |

#### 5. Map Implementations

| Topic | Source |
|---|---|
| `HashMap` | [HashMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/HashMapDemo.java) |
| `LinkedHashMap` (insertion + access order, LRU cache) | [LinkedHashMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/LinkedHashMapDemo.java) |
| `TreeMap` (sorted, NavigableMap) | [TreeMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/TreeMapDemo.java) |
| `WeakHashMap` (GC-eligible keys) | [WeakHashMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/WeakHashMapDemo.java) |
| `IdentityHashMap` (`==` instead of `equals`) | [IdentityHashMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/IdentityHashMapDemo.java) |
| `Hashtable` (legacy) | [HashtableDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/HashtableDemo.java) |

#### 6. Utility & Supporting Classes

| Topic | Source |
|---|---|
| `Collections` utility class | [CollectionsClass.java](src/Phase5_CollectionsLambdasStreams/Collections/CollectionsClass.java) |
| `Iterable` interface (custom for-each types) | [IterableDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/IterableDemo.java) |
| `Iterator` / `ListIterator` / `Spliterator` | [IteratorDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/IteratorDemo.java) |
| `Enumeration` (legacy 1.0 iteration) | [EnumerationDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/EnumerationDemo.java) |
| `Comparator` and `Comparable` | [ComparatorComparable.java](src/Phase5_CollectionsLambdasStreams/Collections/ComparatorComparable.java) |

#### 7. Concurrency Collections

| Topic | Source |
|---|---|
| `ConcurrentHashMap` (lock-striped) | [ConcurrentHashMapDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ConcurrentHashMapDemo.java) |
| `CopyOnWriteArrayList` (snapshot iterator, listener-list pattern) | [CopyOnWriteArrayListDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/CopyOnWriteArrayListDemo.java) |
| `ConcurrentLinkedQueue` (lock-free) | [ConcurrentLinkedQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ConcurrentLinkedQueueDemo.java) |
| `BlockingQueue` family | [BlockingQueueDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/BlockingQueueDemo.java) |
| `ConcurrentSkipListSet` | [ConcurrentSkipListSetDemo.java](src/Phase5_CollectionsLambdasStreams/Collections/ConcurrentSkipListSetDemo.java) |

#### Project

| Topic | Source |
|---|---|
| **Face Detection System** (uses every collection type) | [Face](src/Phase5_CollectionsLambdasStreams/Collections/FaceDetectionApp/Face.java) · [Detector](src/Phase5_CollectionsLambdasStreams/Collections/FaceDetectionApp/Detector.java) · [FaceRepository](src/Phase5_CollectionsLambdasStreams/Collections/FaceDetectionApp/FaceRepository.java) · **[Runner: FaceDetectionApp.java](src/Phase5_CollectionsLambdasStreams/Collections/FaceDetectionApp/FaceDetectionApp.java)** |

### Lambda Expressions and Streams

#### Foundations

| Topic | Source |
|---|---|
| Lambda Expressions (syntax forms, capture, target typing, `this`) | [LambdaExpressions.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/LambdaExpressions.java) |
| Method References (`::`) — four forms in stream context | [MethodReferences.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/MethodReferences.java) |

#### Streams

| Topic | Source |
|---|---|
| Stream Introduction (laziness, one-shot, decl vs imp) | [StreamIntroduction.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/StreamIntroduction.java) |
| Stream Creation (15 ways) | [StreamCreation.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/StreamCreation.java) |
| Stream Pipeline (Source / Intermediate / Terminal architecture) | [StreamPipeline.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/StreamPipeline.java) |
| Intermediate Operations (`filter`/`map`/`flatMap`/`sorted`/`distinct`/`limit`/`skip`/`peek`/`takeWhile`/`dropWhile`/`mapMulti`) | [IntermediateOperations.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/IntermediateOperations.java) |
| Terminal Operations (`forEach`/`collect`/`reduce`/`count`/`match`/`find`/`min`/`max`/`toArray`/`toList`) | [TerminalOperations.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/TerminalOperations.java) |
| Collectors (toList/toMap/groupingBy/partitioningBy/joining/teeing/collectingAndThen) | [CollectorsClass.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/CollectorsClass.java) |

#### Stream Types

| Topic | Source |
|---|---|
| Sequential vs Parallel (when each helps, side-effect trap, splittability) | [SequentialVsParallel.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/SequentialVsParallel.java) |
| Infinite Streams (`iterate`/`generate`/`limit`/`takeWhile`) | [InfiniteStreams.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/InfiniteStreams.java) |
| Primitive Streams (`IntStream`/`LongStream`/`DoubleStream`, boxing perf) | [PrimitiveStreams.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/PrimitiveStreams.java) |
| Stream vs Collection (side-by-side, crossing back and forth) | [StreamVsCollection.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/StreamVsCollection.java) |
| File I/O via streams (`Files.lines`/`list`/`walk`, append, write) | [StreamFileIO.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/StreamFileIO.java) |
| Modern Streams — Java 9 → 21 (`takeWhile`/`dropWhile`, `iterate(3-arg)`, `mapMulti`, `toList`, `teeing`, sequenced collections) | [ModernStreams.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/ModernStreams.java) |

#### Real-World Examples

| Topic | Source |
|---|---|
| Filtering Employees by Salary | [EmployeeSalaryExample.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/Examples/EmployeeSalaryExample.java) |
| Streams in a Grocery Store | [GroceryStoreExample.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/Examples/GroceryStoreExample.java) |
| Grouping Books by Author | [BookGroupingExample.java](src/Phase5_CollectionsLambdasStreams/LambdaAndStreams/Examples/BookGroupingExample.java) |

---

## Phase 6 — Runtime, Memory, Regex, Reflection

> What the JVM actually does with your code, how to manipulate text with
> patterns, and how to inspect and dispatch dynamically.

### Memory Allocation

| # | Topic | Source |
|---|-------|--------|
| 1 | Java Memory Management (overview, heap inspection, default GC) | [MemoryManagementIntro.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/MemoryManagementIntro.java) |
| 2 | How Java Objects Are Stored in Memory (header, fields, padding, references, compressed oops) | [ObjectsInMemory.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/ObjectsInMemory.java) |
| 3 | Types of Memory Areas (Method Area / Heap / Stack / PC / Native, generations) | [JvmMemoryAreas.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/JvmMemoryAreas.java) |
| 4 | Stack vs Heap (side-by-side, pass-by-value, escape analysis) | [StackVsHeap.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/StackVsHeap.java) |
| 5 | Garbage Collection (reachability, mark/sweep, generations, weak/soft refs, Cleaner) | [GarbageCollection.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/GarbageCollection.java) |
| 6 | Types of JVM Garbage Collectors (Serial / Parallel / G1 / ZGC / Shenandoah / Epsilon) | [GarbageCollectors.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/GarbageCollectors.java) |
| 7 | Memory Leaks (5 patterns + fixes) | [MemoryLeaks.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/MemoryLeaks.java) |
| 8 | Modern Memory Features — Java 9 → 21 (Compact Strings, Cleaner, direct buffers, virtual threads, **Generational ZGC**) | [ModernMemoryFeatures.java](src/Phase6_RuntimeMemoryRegexReflection/MemoryAllocation/ModernMemoryFeatures.java) |

### Regex

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (Pattern + Matcher, escapes, `matches` vs `find` vs `lookingAt`) | [RegexIntroduction.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/RegexIntroduction.java) |
| 2 | Matcher Class (every important method, named groups, `replaceAll`, `region`, `results`) | [MatcherClass.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/MatcherClass.java) |
| 3 | Character Class (custom sets, predefined `\d`/`\w`/`\s`, POSIX, Unicode) | [CharacterClass.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/CharacterClass.java) |
| 4 | Quantifiers (`*` `+` `?` `{n,m}`, greedy vs reluctant vs possessive, backtracking) | [Quantifiers.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/Quantifiers.java) |
| 5 | Metacharacters & Anchors (`^` `$` `\b` `\A` `\z` `\G`, `Pattern.quote`) | [MetacharactersAndAnchors.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/MetacharactersAndAnchors.java) |
| 6 | Groups & Backreferences (`(…)` `(?:…)` `(?<name>…)` `\1` `${name}`) | [GroupsAndBackreferences.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/GroupsAndBackreferences.java) |
| 7 | Lookahead & Lookbehind (`(?=…)` `(?!…)` `(?<=…)` `(?<!…)`, password rules) | [LookaroundAssertions.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/LookaroundAssertions.java) |
| 8 | Flags (`CASE_INSENSITIVE`, `MULTILINE`, `DOTALL`, `COMMENTS`, `UNICODE_CHARACTER_CLASS`, scoped `(?i:…)`) | [RegexFlags.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/RegexFlags.java) |
| 9 | Modern Features — Java 8/9/11/21 (`splitAsStream`, `asPredicate`, `Matcher.results`, `replaceAll(Function)`, pattern-matching switch on String) | [ModernRegexFeatures.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/ModernRegexFeatures.java) |
| 10 | Real-World Examples (email, phone, URL, IPv4, password, ISO date, hex color, slug, CSV) | [RegexExamples.java](src/Phase6_RuntimeMemoryRegexReflection/Regex/RegexExamples.java) |

### Reflection API

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (Class, Method, Field, basics) | [ReflectionIntroduction.java](src/Phase6_RuntimeMemoryRegexReflection/Reflection/ReflectionIntroduction.java) |
| 2 | Class / Method / Field in depth (overloads, parameters, generic types) | [ClassAndMethodReflection.java](src/Phase6_RuntimeMemoryRegexReflection/Reflection/ClassAndMethodReflection.java) |
| 3 | Dynamic Invocation (`MethodHandle`, dynamic `Proxy`, mini AOP) | [DynamicInvocation.java](src/Phase6_RuntimeMemoryRegexReflection/Reflection/DynamicInvocation.java) |

---

## Phase 7 — Concurrency

> Multiple threads of execution inside one JVM — concurrent and parallel
> work, the synchronization primitives that keep shared state sane, and the
> Java 21 virtual-thread world.

### 1. Foundations

| # | Topic | Source |
|---|-------|--------|
| 1 | Multithreading Introduction (concurrency vs parallelism, why threads) | [MultithreadingIntroduction.java](src/Phase7_Concurrency/Multithreading/MultithreadingIntroduction.java) |
| 2 | Threads (`java.lang.Thread` API tour) | [Threads.java](src/Phase7_Concurrency/Multithreading/Threads.java) |
| 3 | Thread Lifecycle (`NEW`/`RUNNABLE`/`BLOCKED`/`WAITING`/`TIMED_WAITING`/`TERMINATED`) | [ThreadLifecycle.java](src/Phase7_Concurrency/Multithreading/ThreadLifecycle.java) |
| 4 | The Main Thread (default properties, JVM exit rules) | [MainThread.java](src/Phase7_Concurrency/Multithreading/MainThread.java) |
| 5 | `Thread.start()` vs `Thread.run()` | [StartVsRun.java](src/Phase7_Concurrency/Multithreading/StartVsRun.java) |
| 6 | `Thread.sleep(...)` | [ThreadSleepMethod.java](src/Phase7_Concurrency/Multithreading/ThreadSleepMethod.java) |
| 7 | `Thread.join(...)` | [ThreadJoinMethod.java](src/Phase7_Concurrency/Multithreading/ThreadJoinMethod.java) |
| 8 | `Thread.yield()` and `onSpinWait` | [ThreadYieldMethod.java](src/Phase7_Concurrency/Multithreading/ThreadYieldMethod.java) |
| 9 | Thread Interruption (cooperative cancellation) | [ThreadInterruption.java](src/Phase7_Concurrency/Multithreading/ThreadInterruption.java) |
| 10 | Thread Priority (`MIN`/`NORM`/`MAX`, OS mapping) | [ThreadPriority.java](src/Phase7_Concurrency/Multithreading/ThreadPriority.java) |
| 11 | Daemon Threads (JVM exit semantics) | [DaemonThread.java](src/Phase7_Concurrency/Multithreading/DaemonThread.java) |

### 2. Creating Work

| Topic | Source |
|---|---|
| Runnable Interface (lambdas, composition, decoration) | [RunnableInterface.java](src/Phase7_Concurrency/Multithreading/RunnableInterface.java) |
| Callable & Future (results, exceptions, cancellation, FutureTask) | [CallableAndFuture.java](src/Phase7_Concurrency/Multithreading/CallableAndFuture.java) |

### 3. Correctness (synchronization, JMM, atomics)

| Topic | Source |
|---|---|
| Java Synchronization (`synchronized` blocks/methods, monitor locks) | [JavaSynchronization.java](src/Phase7_Concurrency/Multithreading/JavaSynchronization.java) |
| Thread Safety (strategies, levels, compound-op traps) | [ThreadSafety.java](src/Phase7_Concurrency/Multithreading/ThreadSafety.java) |
| Race Conditions, Livelock, Starvation | [RaceConditionStarvationLivelock.java](src/Phase7_Concurrency/Multithreading/RaceConditionStarvationLivelock.java) |
| Java Memory Model (happens-before, safe publication) | [JavaMemoryModel.java](src/Phase7_Concurrency/Multithreading/JavaMemoryModel.java) |
| `volatile` Keyword (visibility, DCL singleton) | [VolatileKeyword.java](src/Phase7_Concurrency/Multithreading/VolatileKeyword.java) |
| `wait` / `notify` / `notifyAll` | [WaitNotifyNotifyAll.java](src/Phase7_Concurrency/Multithreading/WaitNotifyNotifyAll.java) |
| Producer-Consumer (three implementations) | [ProducerConsumer.java](src/Phase7_Concurrency/Multithreading/ProducerConsumer.java) |
| `ThreadLocal<T>` | [ThreadLocalDemo.java](src/Phase7_Concurrency/Multithreading/ThreadLocalDemo.java) |
| Atomic Variables (`Atomic*`, `LongAdder`, ABA) | [AtomicVariables.java](src/Phase7_Concurrency/Multithreading/AtomicVariables.java) |

### 4. Locks

| Topic | Source |
|---|---|
| Locks in Java (`Lock` interface tour) | [LocksInJava.java](src/Phase7_Concurrency/Multithreading/LocksInJava.java) |
| Lock vs Monitor in Concurrency | [LockVsMonitor.java](src/Phase7_Concurrency/Multithreading/LockVsMonitor.java) |
| Lock Framework vs `synchronized` | [LockFrameworkVsSync.java](src/Phase7_Concurrency/Multithreading/LockFrameworkVsSync.java) |
| `ReentrantLock` (fairness, `tryLock`, conditions) | [ReentrantLockDemo.java](src/Phase7_Concurrency/Multithreading/ReentrantLockDemo.java) |
| `ReadWriteLock` (many readers, one writer) | [ReadWriteLockDemo.java](src/Phase7_Concurrency/Multithreading/ReadWriteLockDemo.java) |
| `StampedLock` (optimistic reads) | [StampedLockDemo.java](src/Phase7_Concurrency/Multithreading/StampedLockDemo.java) |
| Deadlock (Coffman, detection, prevention) | [DeadlockDemo.java](src/Phase7_Concurrency/Multithreading/DeadlockDemo.java) |

### 5. Executors and high-level concurrency

| Topic | Source |
|---|---|
| Thread Pools (`ThreadPoolExecutor`, queues, rejection policies) | [ThreadPools.java](src/Phase7_Concurrency/Multithreading/ThreadPools.java) |
| Executor Framework (`ExecutorService` tour) | [ExecutorFramework.java](src/Phase7_Concurrency/Multithreading/ExecutorFramework.java) |
| `ScheduledExecutorService` (`fixedRate` vs `fixedDelay`) | [ScheduledExecutorDemo.java](src/Phase7_Concurrency/Multithreading/ScheduledExecutorDemo.java) |
| `ForkJoinPool` (divide-and-conquer, work stealing) | [ForkJoinPoolDemo.java](src/Phase7_Concurrency/Multithreading/ForkJoinPoolDemo.java) |
| `CompletableFuture` (composable async) | [CompletableFutureDemo.java](src/Phase7_Concurrency/Multithreading/CompletableFutureDemo.java) |

### 6. Synchronizers

| Topic | Source |
|---|---|
| `CountDownLatch` (one-shot gate) | [CountDownLatchDemo.java](src/Phase7_Concurrency/Multithreading/CountDownLatchDemo.java) |
| `CyclicBarrier` (resettable, barrier action) | [CyclicBarrierDemo.java](src/Phase7_Concurrency/Multithreading/CyclicBarrierDemo.java) |
| `Semaphore` (permits, binary, fair) | [SemaphoreDemo.java](src/Phase7_Concurrency/Multithreading/SemaphoreDemo.java) |
| `Phaser` (variable parties, per-phase actions) | [PhaserDemo.java](src/Phase7_Concurrency/Multithreading/PhaserDemo.java) |

### 7. Java 21 — modern concurrency

| Topic | Source |
|---|---|
| Virtual Threads (JEP 444 finalised) | [VirtualThreads.java](src/Phase7_Concurrency/Multithreading/VirtualThreads.java) |
| Structured Concurrency (JEP 453 preview) | [StructuredConcurrency.java](src/Phase7_Concurrency/Multithreading/StructuredConcurrency.java) |
| Scoped Values (JEP 446 preview) | [ScopedValuesDemo.java](src/Phase7_Concurrency/Multithreading/ScopedValuesDemo.java) |

### 8. End-to-end + project

| Topic | Source |
|---|---|
| Multithreading Complete Tutorial (one-file tour) | [MultithreadingCompleteTutorial.java](src/Phase7_Concurrency/Multithreading/MultithreadingCompleteTutorial.java) |
| **Project: Snake Game** (Swing render thread + game loop thread + locked state) | [Direction](src/Phase7_Concurrency/Multithreading/SnakeGame/Direction.java) · [Cell](src/Phase7_Concurrency/Multithreading/SnakeGame/Cell.java) · [GameState](src/Phase7_Concurrency/Multithreading/SnakeGame/GameState.java) · [GameLoop](src/Phase7_Concurrency/Multithreading/SnakeGame/GameLoop.java) · [SnakeBoard](src/Phase7_Concurrency/Multithreading/SnakeGame/SnakeBoard.java) · **[Runner: SnakeGame.java](src/Phase7_Concurrency/Multithreading/SnakeGame/SnakeGame.java)** |

---

## Phase 8 — Practical APIs

> The APIs you'll actually reach for in business apps: reading and writing
> files, handling dates and times, and talking to HTTP services.

### File I/O

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (java.io vs java.nio.file, which class for what) | [FileIOIntroduction.java](src/Phase8_PracticalAPIs/FileIO/FileIOIntroduction.java) |
| 2 | Byte Streams (`InputStream` / `OutputStream`, `File*` impls) | [ByteStreams.java](src/Phase8_PracticalAPIs/FileIO/ByteStreams.java) |
| 3 | Character Streams (`Reader` / `Writer`, charset traps, bridges) | [CharacterStreams.java](src/Phase8_PracticalAPIs/FileIO/CharacterStreams.java) |
| 4 | Buffered Streams (`Buffered*`, `readLine`, `lines()`) | [BufferedStreams.java](src/Phase8_PracticalAPIs/FileIO/BufferedStreams.java) |
| 5 | Data Streams (`DataInput/OutputStream` for primitives) | [DataStreams.java](src/Phase8_PracticalAPIs/FileIO/DataStreams.java) |
| 6 | Object Streams (Java serialization in/out) | [ObjectStreams.java](src/Phase8_PracticalAPIs/FileIO/ObjectStreams.java) |
| 7 | `Path` and `Files` (the modern API) | [FilesAndPaths.java](src/Phase8_PracticalAPIs/FileIO/FilesAndPaths.java) |
| 8 | Walking the file system (`list`/`walk`/`find`/`lines`) | [FilesWalkAndList.java](src/Phase8_PracticalAPIs/FileIO/FilesWalkAndList.java) |
| 9 | `FileChannel` (random access, memory mapping, locks) | [FileChannelDemo.java](src/Phase8_PracticalAPIs/FileIO/FileChannelDemo.java) |
| 10 | `WatchService` (observe FS changes) | [WatchServiceDemo.java](src/Phase8_PracticalAPIs/FileIO/WatchServiceDemo.java) |
| 11 | Modern File I/O — Java 11+ (`readString`, `writeString`, `mismatch`) | [ModernFileIO.java](src/Phase8_PracticalAPIs/FileIO/ModernFileIO.java) |

### Date and Time API

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (the headline types, why `java.time`) | [DateTimeIntroduction.java](src/Phase8_PracticalAPIs/DateAndTime/DateTimeIntroduction.java) |
| 2 | `LocalDate` / `LocalTime` / `LocalDateTime` (zone-less locals) | [LocalDateLocalTimeLocalDateTime.java](src/Phase8_PracticalAPIs/DateAndTime/LocalDateLocalTimeLocalDateTime.java) |
| 3 | `Instant` (the global timeline) | [InstantDemo.java](src/Phase8_PracticalAPIs/DateAndTime/InstantDemo.java) |
| 4 | `ZonedDateTime` / `OffsetDateTime` (zones, DST, fixed offsets) | [ZonedDateTimeDemo.java](src/Phase8_PracticalAPIs/DateAndTime/ZonedDateTimeDemo.java) |
| 5 | `Duration` (clock-length differences) | [DurationDemo.java](src/Phase8_PracticalAPIs/DateAndTime/DurationDemo.java) |
| 6 | `Period` (calendar-length differences) | [PeriodDemo.java](src/Phase8_PracticalAPIs/DateAndTime/PeriodDemo.java) |
| 7 | `DateTimeFormatter` (parse and print) | [DateTimeFormatterDemo.java](src/Phase8_PracticalAPIs/DateAndTime/DateTimeFormatterDemo.java) |
| 8 | Legacy bridge (`Date` / `Calendar` / `Timestamp` ↔ `java.time`) | [LegacyDateConversions.java](src/Phase8_PracticalAPIs/DateAndTime/LegacyDateConversions.java) |

### HTTP Client (Java 11+)

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (`HttpClient`, `HttpRequest`, body handlers, body publishers) | [HttpClientIntroduction.java](src/Phase8_PracticalAPIs/HttpClient/HttpClientIntroduction.java) |
| 2 | Synchronous and Asynchronous Requests (`send` vs `sendAsync`, fan-out, errors) | [SyncAndAsyncRequests.java](src/Phase8_PracticalAPIs/HttpClient/SyncAndAsyncRequests.java) |
| 3 | WebSocket (full-duplex, listeners, backpressure) | [WebSocketDemo.java](src/Phase8_PracticalAPIs/HttpClient/WebSocketDemo.java) |

---

## Phase 9 — Modern Java & Modules

> Language and library features that landed between Java 8 and Java 21, and
> the module system added in Java 9. By the end of this phase you can read
> any modern Java codebase fluently.

### Modern Java (10 → 21)

| Feature | Since | Source |
|---------|-------|--------|
| `var` — local variable type inference | Java 10 | [VarLocalTypeInference.java](src/Phase9_ModernJavaAndModules/ModernJava/VarLocalTypeInference.java) |
| Switch expressions (`->`, `yield`) | Java 14 | [SwitchExpression.java](src/Phase9_ModernJavaAndModules/ModernJava/SwitchExpression.java) |
| Pattern matching for `switch` (with `when`, `null` cases) | Java 21 | [PatternMatchingSwitch.java](src/Phase9_ModernJavaAndModules/ModernJava/PatternMatchingSwitch.java) |
| Records and **record patterns** (deconstruction) | Java 16 / 21 | [RecordsAndPatterns.java](src/Phase9_ModernJavaAndModules/ModernJava/RecordsAndPatterns.java) |
| Sequenced Collections (`getFirst`, `getLast`, `reversed`) | Java 21 | [SequencedCollections.java](src/Phase9_ModernJavaAndModules/ModernJava/SequencedCollections.java) |

### Java Platform Module System (JPMS)

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (why modules, anatomy of `module-info.java`) | [ModulesIntroduction.java](src/Phase9_ModernJavaAndModules/Modules/ModulesIntroduction.java) |
| 2 | Module Examples (catalogue of `module-info.java` shapes) | [ModuleExamples.java](src/Phase9_ModernJavaAndModules/Modules/ModuleExamples.java) |
| 3 | `ServiceLoader` (the built-in plugin / SPI mechanism) | [ServiceLoaderDemo.java](src/Phase9_ModernJavaAndModules/Modules/ServiceLoaderDemo.java) |

---

## How to Run

This is a plain-Java project — no Maven, no Gradle. From the repo root:

```bash
# Compile one file
javac src/Phase0_SetupAndFirstPrograms/Introduction/Introduction.java

# Run it (use the fully qualified class name including the package)
cd src
java Phase0_SetupAndFirstPrograms.Introduction.Introduction
```

Or from your IDE: right-click any `.java` file containing a `main()` and choose
**Run**.

### Single-file mode (Java 11+)

```bash
java src/Phase0_SetupAndFirstPrograms/Introduction/Introduction.java
```

---

## How Each File Is Structured

Every `.java` file is self-contained:

1. A theory block at the top (Javadoc style) — read this first.
2. A `main()` method with concrete, runnable examples grouped into numbered sections.
3. Expected output captured inline as comments where useful.

Most folders also have one `<Topic>.README.md` per file with the same theory in
markdown form — handy for browsing on GitHub without opening the source.

---

## Pace and Projects

| Phase | Suggested duration | Capstone |
|---|---|---|
| 0–1 | Week 1 | — |
| 2 | Week 2 | — |
| 3 | Week 3 | **Banking App**, **Employee App** |
| 4 | Week 4 | — |
| 5 | Weeks 5–6 | **Face Detection** |
| 6 | Week 7 | — |
| 7 | Weeks 8–9 | **Snake Game** |
| 8 | Week 10 | — |
| 9 | Week 11 | — |

Happy hacking!
