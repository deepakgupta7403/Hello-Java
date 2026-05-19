# Hello-Java

A hands-on, topic-by-topic walkthrough of the **Java** programming language
(targeting **Java 21 LTS**). Every topic is a runnable `.java` file with theory
in the top Javadoc and worked examples in `main()`. The expected console output
is also included as comments inside each `main()` so you can verify your run.

> **Project SDK: JDK 21** — set in `.idea/misc.xml`. Files under
> [`src/Basics/ModernJava`](src/Basics/ModernJava) use Java 21 features
> (pattern matching for switch, record patterns, sequenced collections, etc.).

> Each section below links straight to the source file(s) in this repository.

---

## Basics

> Core syntax: variables, data types, loops, decision making, and the building
> blocks every Java program is made of.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction to Java | [Introduction.java](src/Basics/Introduction/Introduction.java) |
| 2 | Download and Install Java | [InstallJava.java](src/Basics/Installation/InstallJava.java) |
| 3 | JDK vs JRE vs JVM | [JDKvsJREvsJVM.java](src/Basics/JDKvsJREvsJVM/JDKvsJREvsJVM.java) |
| 4 | Taking Input | [Scanner](src/Basics/Input/ScannerInput.java) · [BufferedReader](src/Basics/Input/BufferedReaderInput.java) · [Console](src/Basics/Input/ConsoleInput.java) |
| 5 | Printing Output | [PrintMethods.java](src/Basics/Output/PrintMethods.java) · [FormattedOutput.java](src/Basics/Output/FormattedOutput.java) |
| 6 | Identifiers | [Identifiers.java](src/Basics/Identifiers/Identifiers.java) |
| 7 | Keywords | [Keywords.java](src/Basics/Keywords/Keywords.java) |
| 8 | Data Types (Intro) | [DataTypesIntro.java](src/Basics/DataTypes/DataTypesIntro.java) |
| 9 | Primitive Data Types | [byte](src/Basics/PrimitiveDataTypes/ByteExample.java) · [short](src/Basics/PrimitiveDataTypes/ShortExample.java) · [int](src/Basics/PrimitiveDataTypes/IntegerExample.java) · [long](src/Basics/PrimitiveDataTypes/LongExample.java) · [float](src/Basics/PrimitiveDataTypes/FloatExample.java) · [double](src/Basics/PrimitiveDataTypes/DoubleExample.java) · [char](src/Basics/PrimitiveDataTypes/CharExample.java) · [boolean](src/Basics/PrimitiveDataTypes/BooleanExample.java) |
| 10 | Wrapper Classes | [WrapperClassesIntro.java](src/Basics/WrapperClasses/WrapperClassesIntro.java) · [AutoboxingUnboxing.java](src/Basics/WrapperClasses/AutoboxingUnboxing.java) |
| 11 | Variables | [Instance](src/Basics/Variables/InstanceVariableExample.java) · [Local](src/Basics/Variables/LocalVariableExample.java) · [Static](src/Basics/Variables/StaticVariableExample.java) · [Scope](src/Basics/Variables/ScopeOfVariable.java) |
| 12 | Operators | [Arithmetic](src/Basics/Operators/ArithmeticOperators.java) · [Relational](src/Basics/Operators/RelationalOperators.java) · [Logical](src/Basics/Operators/LogicalOperators.java) · [Bitwise & Shift](src/Basics/Operators/BitwiseOperators.java) · [Assignment](src/Basics/Operators/AssignmentOperators.java) · [Unary](src/Basics/Operators/UnaryOperators.java) · [Ternary](src/Basics/Operators/TernaryOperator.java) · [instanceof](src/Basics/Operators/InstanceofOperator.java) |
| 13 | Decision Making | [if](src/Basics/DecisionMaking/IfStatement.java) · [if-else](src/Basics/DecisionMaking/IfElseStatement.java) · [if-else-if](src/Basics/DecisionMaking/IfElseIfStatement.java) · [nested-if](src/Basics/DecisionMaking/NestedIfStatement.java) · [switch](src/Basics/DecisionMaking/SwitchCaseStatement.java) |
| 14 | Loops and Jump Statements | [for](src/Basics/Loops/ForLoop.java) · [while](src/Basics/Loops/WhileLoop.java) · [do-while](src/Basics/Loops/DoWhileLoop.java) · [enhanced for](src/Basics/Loops/ForEachOrEnhanceLoop.java) · [infinite loop](src/Basics/Loops/InfiniteLoop.java) · [for loop notes](src/Basics/Loops/ForLoopImportant.java) · [break](src/Basics/DecisionMaking/JumpStatement/BreakStatement.java) · [continue](src/Basics/DecisionMaking/JumpStatement/ContinueStatement.java) · [return](src/Basics/DecisionMaking/JumpStatement/ReturnStatement.java) · [labels](src/Basics/DecisionMaking/JumpStatement/LabelStatement.java) |
| 15 | Type Conversion | [Automatic](src/Basics/Conversion/AutomaticTypeConversion.java) · [Explicit](src/Basics/Conversion/ExplicitTypeConversion.java) · [Widening](src/Basics/Conversion/WideningPrimitive.java) |
| 16 | Comments | [Single-line](src/Basics/Comments/SingleLineComments.java) · [Multi-line](src/Basics/Comments/MultiLineComment.java) · [Documentation (Javadoc)](src/Basics/Comments/DocumentationComments.java) |
| 17 | Enumerations | [Intro](src/Basics/Enumerations/InsideClassEnum.java) · [Outside class](src/Basics/Enumerations/OutsideClassEnum.java) · [Custom values](src/Basics/Enumerations/EnumWithCustomizedVal.java) · [Constructor](src/Basics/Enumerations/EnumConstructor.java) · [Methods](src/Basics/Enumerations/EnumMethods.java) · [In switch](src/Basics/Enumerations/EnumWithSwitchCase.java) · [Main inside enum](src/Basics/Enumerations/MainInsideEnum.java) |
| 18 | Special Keywords | [final](src/Basics/SpecialKeyword/FinalKeywordExample.java) |
| 19 | Extras / Interview Tidbits | [Facts about null](src/Basics/Extra/FactsAboutNull.java) · [Underscore in numbers](src/Basics/Extra/UnderscoreInNumber.java) · [Does Java support goto?](src/Basics/Extra/IsJavaSupportGoto.java) · [Object memory allocation](src/Basics/Extra/ObjectMemoryAllocation.java) · [Function currying](src/Basics/Extra/FunctionCurrying.java) · [Binary search](src/Basics/Extra/BinarySearch.java) · [Sorting](src/Basics/Extra/Sorting.java) |

---

## Strings

> Sequences of characters. `String` is immutable; `StringBuffer` and
> `StringBuilder` are mutable counterparts.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (pool, intern, char[] bridge, compact strings) | [StringIntroduction.java](src/Basics/Strings/StringIntroduction.java) |
| 2 | Why Strings are Immutable (pool, threads, security, hash caching) | [StringImmutability.java](src/Basics/Strings/StringImmutability.java) |
| 3 | String Concatenation (`+`, `concat`, `join`, `StringJoiner`, perf trap) | [StringConcatenation.java](src/Basics/Strings/StringConcatenation.java) |
| 4 | String Methods (every important method by category) | [StringMethods.java](src/Basics/Strings/StringMethods.java) |
| 5 | StringBuffer Class (synchronized, full method reference) | [StringBufferClass.java](src/Basics/Strings/StringBufferClass.java) |
| 6 | StringBuilder Class (non-synchronized, full method reference) | [StringBuilderClass.java](src/Basics/Strings/StringBuilderClass.java) |
| 7 | String vs StringBuffer vs StringBuilder (table + benchmark) | [StringVsBufferVsBuilder.java](src/Basics/Strings/StringVsBufferVsBuilder.java) |
| 8 | Modern Features — Java 11/12/15/21 (`strip`, `repeat`, `lines`, `transform`, text blocks, `case String s when …`) | [ModernStringFeatures.java](src/Basics/Strings/ModernStringFeatures.java) |

---

## Arrays

> Fixed-size, index-based containers for multiple values of the same type.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (declare, init, iterate, defaults, pitfalls) | [ArrayIntroduction.java](src/Basics/Arrays/ArrayIntroduction.java) |
| 2 | Multi-Dimensional Arrays (2D, 3D, `deepToString`) | [MultiDimensionalArrays.java](src/Basics/Arrays/MultiDimensionalArrays.java) |
| 3 | Jagged Arrays (varying row lengths, Pascal's triangle) | [JaggedArrays.java](src/Basics/Arrays/JaggedArrays.java) |
| 4 | `java.util.Arrays` utility class (sort, search, copy, fill, equals, stream) | [ArraysClass.java](src/Basics/Arrays/ArraysClass.java) |
| 5 | Final Arrays (reference vs contents, immutability patterns) | [FinalArrays.java](src/Basics/Arrays/FinalArrays.java) |

---

## Methods

> Reusable blocks of code that perform a task. Methods improve readability,
> remove duplication, and are the foundation of testable code.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (syntax, overloading, pass-by-value, recursion) | [MethodIntroduction.java](src/Basics/Methods/MethodIntroduction.java) |
| 2 | Static Methods vs Instance Methods | [StaticVsInstanceMethods.java](src/Basics/Methods/StaticVsInstanceMethods.java) |
| 3 | Access Modifiers (public / protected / package-private / private) | [AccessModifiers.java](src/Basics/Methods/AccessModifiers.java) |
| 4 | Command Line Arguments (`String[] args`) | [CommandLineArguments.java](src/Basics/Methods/CommandLineArguments.java) |
| 5 | Variable Arguments (Varargs `...`) | [Varargs.java](src/Basics/Methods/Varargs.java) |
| 6 | Method References (`::`) — Java 8+ | [MethodReferences.java](src/Basics/Methods/MethodReferences.java) |
| 7 | Interface Methods — `default` / `static` / `private` — Java 8/9 | [InterfaceMethods.java](src/Basics/Methods/InterfaceMethods.java) |

---

## Modern Java (10 → 21)

> Language and library features that landed between Java 8 and Java 21. The
> repo's project SDK is set to **JDK 21**, so all examples here compile and run
> on a Java 21 install.

| Feature | Since | Source |
|---------|-------|--------|
| `var` — local variable type inference | Java 10 | [VarLocalTypeInference.java](src/Basics/ModernJava/VarLocalTypeInference.java) |
| Switch expressions (`->`, `yield`) | Java 14 | [SwitchExpression.java](src/Basics/ModernJava/SwitchExpression.java) |
| Pattern matching for `switch` (with `when`, `null` cases) | Java 21 | [PatternMatchingSwitch.java](src/Basics/ModernJava/PatternMatchingSwitch.java) |
| Records and **record patterns** (deconstruction) | Java 16 / 21 | [RecordsAndPatterns.java](src/Basics/ModernJava/RecordsAndPatterns.java) |
| Sequenced Collections (`getFirst`, `getLast`, `reversed`) | Java 21 | [SequencedCollections.java](src/Basics/ModernJava/SequencedCollections.java) |

---

## Overview (First Programs)

| Topic | Source |
|-------|--------|
| Hello World | [HelloWorld.java](src/Overview/HelloWorld.java) |
| Is `main` compulsory? | [IsMainCompulsory.java](src/Overview/IsMainCompulsory.java) |
| Class name vs file name | [ClassNameMyth/Hello.java](src/Overview/ClassNameMyth/Hello.java) |

---

## OOP Concepts

| Topic | Source |
|-------|--------|
| Classes & Objects | [Initializing object](src/OOPSConcepts/ClassesAndObject/InitializingObject.java) · [Ways to create an object](src/OOPSConcepts/ClassesAndObject/WaysToCreateObject.java) |
| Encapsulation | [TestEncapsulation.java](src/OOPSConcepts/Encapsulation/TestEncapsulation.java) |
| Inheritance | [Intro](src/OOPSConcepts/Inheritance/InheritanceJava.java) · [Single](src/OOPSConcepts/Inheritance/TypesOfInheritance/SingleInheritance.java) · [Multi-level](src/OOPSConcepts/Inheritance/TypesOfInheritance/MultiLevelInheritance.java) · [Hierarchical](src/OOPSConcepts/Inheritance/TypesOfInheritance/HierarchicalInheritance.java) · [Multiple (interfaces)](src/OOPSConcepts/Inheritance/TypesOfInheritance/MultipleInheritance.java) · [Hybrid](src/OOPSConcepts/Inheritance/TypesOfInheritance/HybridInheritance.java) |
| Serialization | [Demo 1](src/OOPSConcepts/SerializationDeserialization/SerializationDeserializationDemoOne.java) · [Demo 2](src/OOPSConcepts/SerializationDeserialization/SerializationDeserializationDemoTwo.java) |

---

## How to Run

This is a plain-Java project — no Maven, no Gradle. From the repo root:

```bash
# Compile one file
javac src/Basics/Introduction/Introduction.java

# Run it (use the fully qualified class name including the package)
cd src
java Basics.Introduction.Introduction
```

Or from your IDE: right-click any `.java` file containing a `main()` and choose
**Run**.

### Single-file mode (Java 11+)

```bash
java src/Basics/Introduction/Introduction.java
```

---

## Reading Order (Suggested)

If you are new to Java, work through the topics in the order they appear in the
**Basics** table above. Each file is self-contained and includes:

1. A theory block at the top (Javadoc style).
2. A `main()` method with concrete, runnable examples.
3. The expected output captured inline as comments.

Happy hacking!
