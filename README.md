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

## Lambda Expressions and Streams

> Functional-style data processing. Lambdas give concise syntax for
> anonymous functions; the Stream API turns "loops + temporary lists"
> into composable, parallelisable pipelines.

### Foundations

| Topic | Source |
|---|---|
| Lambda Expressions (syntax forms, capture, target typing, `this`) | [LambdaExpressions.java](src/Basics/LambdaAndStreams/LambdaExpressions.java) |
| Method References (`::`) — four forms in stream context | [MethodReferences.java](src/Basics/LambdaAndStreams/MethodReferences.java) |

### Streams

| Topic | Source |
|---|---|
| Stream Introduction (laziness, one-shot, decl vs imp) | [StreamIntroduction.java](src/Basics/LambdaAndStreams/StreamIntroduction.java) |
| Stream Creation (15 ways) | [StreamCreation.java](src/Basics/LambdaAndStreams/StreamCreation.java) |
| Stream Pipeline (Source / Intermediate / Terminal architecture) | [StreamPipeline.java](src/Basics/LambdaAndStreams/StreamPipeline.java) |
| Intermediate Operations (`filter`/`map`/`flatMap`/`sorted`/`distinct`/`limit`/`skip`/`peek`/`takeWhile`/`dropWhile`/`mapMulti`) | [IntermediateOperations.java](src/Basics/LambdaAndStreams/IntermediateOperations.java) |
| Terminal Operations (`forEach`/`collect`/`reduce`/`count`/`match`/`find`/`min`/`max`/`toArray`/`toList`) | [TerminalOperations.java](src/Basics/LambdaAndStreams/TerminalOperations.java) |
| Collectors (toList/toMap/groupingBy/partitioningBy/joining/teeing/collectingAndThen) | [CollectorsClass.java](src/Basics/LambdaAndStreams/CollectorsClass.java) |

### Stream Types

| Topic | Source |
|---|---|
| Sequential vs Parallel (when each helps, side-effect trap, splittability) | [SequentialVsParallel.java](src/Basics/LambdaAndStreams/SequentialVsParallel.java) |
| Infinite Streams (`iterate`/`generate`/`limit`/`takeWhile`) | [InfiniteStreams.java](src/Basics/LambdaAndStreams/InfiniteStreams.java) |
| Primitive Streams (`IntStream`/`LongStream`/`DoubleStream`, boxing perf) | [PrimitiveStreams.java](src/Basics/LambdaAndStreams/PrimitiveStreams.java) |
| Stream vs Collection (side-by-side, crossing back and forth) | [StreamVsCollection.java](src/Basics/LambdaAndStreams/StreamVsCollection.java) |
| File I/O via streams (`Files.lines`/`list`/`walk`, append, write) | [StreamFileIO.java](src/Basics/LambdaAndStreams/StreamFileIO.java) |
| Modern Streams — Java 9 → 21 (`takeWhile`/`dropWhile`, `iterate(3-arg)`, `mapMulti`, `toList`, `teeing`, sequenced collections) | [ModernStreams.java](src/Basics/LambdaAndStreams/ModernStreams.java) |

### Real-World Examples

| Topic | Source |
|---|---|
| Filtering Employees by Salary | [EmployeeSalaryExample.java](src/Basics/LambdaAndStreams/Examples/EmployeeSalaryExample.java) |
| Streams in a Grocery Store | [GroceryStoreExample.java](src/Basics/LambdaAndStreams/Examples/GroceryStoreExample.java) |
| Grouping Books by Author | [BookGroupingExample.java](src/Basics/LambdaAndStreams/Examples/BookGroupingExample.java) |

---

## Generic Programming

> Type-safe reuse via parameterised classes, methods, and interfaces.
> Generics are checked by the compiler and erased at runtime — a quirky
> but powerful combination.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (before/after, type parameter conventions) | [GenericsIntroduction.java](src/Basics/Generics/GenericsIntroduction.java) |
| 2 | Generic Classes (single + multi-param, inheritance, diamond) | [GenericClasses.java](src/Basics/Generics/GenericClasses.java) |
| 3 | Generic Methods (type inference, method-level type params) | [GenericMethods.java](src/Basics/Generics/GenericMethods.java) |
| 4 | Generic Interfaces (Comparable, Function, custom contracts) | [GenericInterfaces.java](src/Basics/Generics/GenericInterfaces.java) |
| 5 | Bounded Type Parameters (`<T extends X>`, multi-bound `&`) | [BoundedTypeParameters.java](src/Basics/Generics/BoundedTypeParameters.java) |
| 6 | Wildcards (`?`, `? extends`, `? super`) | [Wildcards.java](src/Basics/Generics/Wildcards.java) |
| 7 | PECS Principle (Producer Extends, Consumer Super) | [PecsPrinciple.java](src/Basics/Generics/PecsPrinciple.java) |
| 8 | Type Erasure (runtime behaviour, bridge methods, reflection) | [TypeErasure.java](src/Basics/Generics/TypeErasure.java) |
| 9 | Generic Restrictions (no primitives / no `new T()` / no generic arrays / no parameterised `instanceof`) | [GenericRestrictions.java](src/Basics/Generics/GenericRestrictions.java) |
| 10 | Recursive Type Bounds (`<T extends Comparable<T>>`, self-typed builders, `Enum<E extends Enum<E>>`) | [RecursiveTypeBounds.java](src/Basics/Generics/RecursiveTypeBounds.java) |
| 11 | Heap Pollution + `@SafeVarargs` | [HeapPollutionAndSafeVarargs.java](src/Basics/Generics/HeapPollutionAndSafeVarargs.java) |
| 12 | Modern Generics — Java 7 → 21 (diamond, `var`, generic records, sealed generic interfaces, **generic record patterns in switch**) | [ModernGenerics.java](src/Basics/Generics/ModernGenerics.java) |

---

## Collections

> Framework for storing and manipulating groups of objects — `List`, `Set`,
> `Queue`, `Deque`, `Map`, plus utilities and concurrency variants. Each
> file is a complete method reference with **why this type exists** and
> **when to use it**.

### 0. Framework Overview

| Topic | Source |
|---|---|
| Framework Introduction (hierarchy diagram, big-O cheatsheet) | [CollectionsIntroduction.java](src/Basics/Collections/CollectionsIntroduction.java) |
| Modern Features — Java 8 → 21 (factories, Collectors, `Stream.toList`, **Sequenced Collections**) | [ModernCollections.java](src/Basics/Collections/ModernCollections.java) |

### 1. Core Interfaces

| Topic | Source |
|---|---|
| `Collection` Interface | [CollectionInterface.java](src/Basics/Collections/CollectionInterface.java) |
| `List` Interface | [ListInterface.java](src/Basics/Collections/ListInterface.java) |
| `Set` Interface | [SetInterface.java](src/Basics/Collections/SetInterface.java) |
| `Queue` Interface | [QueueInterface.java](src/Basics/Collections/QueueInterface.java) |
| `Deque` Interface | [DequeInterface.java](src/Basics/Collections/DequeInterface.java) |
| `Map` Interface | [MapInterface.java](src/Basics/Collections/MapInterface.java) |

### 2. List Implementations

| Topic | Source |
|---|---|
| `ArrayList` | [ArrayListDemo.java](src/Basics/Collections/ArrayListDemo.java) |
| `LinkedList` | [LinkedListDemo.java](src/Basics/Collections/LinkedListDemo.java) |
| `Vector` + `Stack` (legacy) | [VectorAndStack.java](src/Basics/Collections/VectorAndStack.java) |
| `AbstractList` + `AbstractSequentialList` (skeleton classes) | [AbstractListClasses.java](src/Basics/Collections/AbstractListClasses.java) |

### 3. Set Implementations

| Topic | Source |
|---|---|
| `HashSet` | [HashSetDemo.java](src/Basics/Collections/HashSetDemo.java) |
| `LinkedHashSet` | [LinkedHashSetDemo.java](src/Basics/Collections/LinkedHashSetDemo.java) |
| `TreeSet` | [TreeSetDemo.java](src/Basics/Collections/TreeSetDemo.java) |
| `EnumSet` (bit-vector enum keys) | [EnumSetDemo.java](src/Basics/Collections/EnumSetDemo.java) |
| `SortedSet` + `NavigableSet` interfaces | [SortedAndNavigableSet.java](src/Basics/Collections/SortedAndNavigableSet.java) |
| `ConcurrentSkipListSet` | [ConcurrentSkipListSetDemo.java](src/Basics/Collections/ConcurrentSkipListSetDemo.java) |

### 4. Queue / Deque Implementations

| Topic | Source |
|---|---|
| `PriorityQueue` (heap, Top-K) | [PriorityQueueDemo.java](src/Basics/Collections/PriorityQueueDemo.java) |
| `ArrayDeque` (modern stack + queue) | [ArrayDequeDemo.java](src/Basics/Collections/ArrayDequeDemo.java) |
| `BlockingQueue` (ArrayBlockingQueue / LinkedBlockingQueue / SynchronousQueue) | [BlockingQueueDemo.java](src/Basics/Collections/BlockingQueueDemo.java) |
| `ConcurrentLinkedQueue` (lock-free) | [ConcurrentLinkedQueueDemo.java](src/Basics/Collections/ConcurrentLinkedQueueDemo.java) |
| `AbstractQueue` (skeleton class + custom BoundedQueue) | [AbstractQueueDemo.java](src/Basics/Collections/AbstractQueueDemo.java) |

### 5. Map Implementations

| Topic | Source |
|---|---|
| `HashMap` | [HashMapDemo.java](src/Basics/Collections/HashMapDemo.java) |
| `LinkedHashMap` (insertion + access order, LRU cache) | [LinkedHashMapDemo.java](src/Basics/Collections/LinkedHashMapDemo.java) |
| `TreeMap` (sorted, NavigableMap) | [TreeMapDemo.java](src/Basics/Collections/TreeMapDemo.java) |
| `WeakHashMap` (GC-eligible keys) | [WeakHashMapDemo.java](src/Basics/Collections/WeakHashMapDemo.java) |
| `IdentityHashMap` (`==` instead of `equals`) | [IdentityHashMapDemo.java](src/Basics/Collections/IdentityHashMapDemo.java) |
| `Hashtable` (legacy) | [HashtableDemo.java](src/Basics/Collections/HashtableDemo.java) |

### 6. Utility & Supporting Classes

| Topic | Source |
|---|---|
| `Collections` utility class | [CollectionsClass.java](src/Basics/Collections/CollectionsClass.java) |
| `Iterable` interface (custom for-each types) | [IterableDemo.java](src/Basics/Collections/IterableDemo.java) |
| `Iterator` / `ListIterator` / `Spliterator` | [IteratorDemo.java](src/Basics/Collections/IteratorDemo.java) |
| `Enumeration` (legacy 1.0 iteration) | [EnumerationDemo.java](src/Basics/Collections/EnumerationDemo.java) |
| `Comparator` and `Comparable` | [ComparatorComparable.java](src/Basics/Collections/ComparatorComparable.java) |

### 7. Concurrency Collections

| Topic | Source |
|---|---|
| `ConcurrentHashMap` (lock-striped) | [ConcurrentHashMapDemo.java](src/Basics/Collections/ConcurrentHashMapDemo.java) |
| `CopyOnWriteArrayList` (snapshot iterator, listener-list pattern) | [CopyOnWriteArrayListDemo.java](src/Basics/Collections/CopyOnWriteArrayListDemo.java) |
| `ConcurrentLinkedQueue` (lock-free) | [ConcurrentLinkedQueueDemo.java](src/Basics/Collections/ConcurrentLinkedQueueDemo.java) |
| `BlockingQueue` family | [BlockingQueueDemo.java](src/Basics/Collections/BlockingQueueDemo.java) |
| `ConcurrentSkipListSet` | [ConcurrentSkipListSetDemo.java](src/Basics/Collections/ConcurrentSkipListSetDemo.java) |

### Project

| Topic | Source |
|---|---|
| Face Detection System (uses every collection type) | [Face](src/Basics/Collections/FaceDetectionApp/Face.java) · [Detector](src/Basics/Collections/FaceDetectionApp/Detector.java) · [FaceRepository](src/Basics/Collections/FaceDetectionApp/FaceRepository.java) · **[Runner: FaceDetectionApp.java](src/Basics/Collections/FaceDetectionApp/FaceDetectionApp.java)** |

---

## Memory Allocation

> How the JVM assigns memory to variables, objects, and classes — heap,
> stack, garbage collection, and how to avoid leaks.

| # | Topic | Source |
|---|-------|--------|
| 1 | Java Memory Management (overview, heap inspection, default GC) | [MemoryManagementIntro.java](src/Basics/MemoryAllocation/MemoryManagementIntro.java) |
| 2 | How Java Objects Are Stored in Memory (header, fields, padding, references, compressed oops) | [ObjectsInMemory.java](src/Basics/MemoryAllocation/ObjectsInMemory.java) |
| 3 | Types of Memory Areas (Method Area / Heap / Stack / PC / Native, generations) | [JvmMemoryAreas.java](src/Basics/MemoryAllocation/JvmMemoryAreas.java) |
| 4 | Stack vs Heap (side-by-side, pass-by-value, escape analysis) | [StackVsHeap.java](src/Basics/MemoryAllocation/StackVsHeap.java) |
| 5 | Garbage Collection (reachability, mark/sweep, generations, weak/soft refs, Cleaner) | [GarbageCollection.java](src/Basics/MemoryAllocation/GarbageCollection.java) |
| 6 | Types of JVM Garbage Collectors (Serial / Parallel / G1 / ZGC / Shenandoah / Epsilon) | [GarbageCollectors.java](src/Basics/MemoryAllocation/GarbageCollectors.java) |
| 7 | Memory Leaks (5 patterns + fixes) | [MemoryLeaks.java](src/Basics/MemoryAllocation/MemoryLeaks.java) |
| 8 | Modern Memory Features — Java 9 → 21 (Compact Strings, Cleaner, direct buffers, virtual threads, **Generational ZGC**) | [ModernMemoryFeatures.java](src/Basics/MemoryAllocation/ModernMemoryFeatures.java) |

---

## Regex

> Pattern matching and text manipulation via `java.util.regex` —
> `Pattern` compiles a regex, `Matcher` runs it against an input.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (Pattern + Matcher, escapes, `matches` vs `find` vs `lookingAt`) | [RegexIntroduction.java](src/Basics/Regex/RegexIntroduction.java) |
| 2 | Matcher Class (every important method, named groups, `replaceAll`, `region`, `results`) | [MatcherClass.java](src/Basics/Regex/MatcherClass.java) |
| 3 | Character Class (custom sets, predefined `\d`/`\w`/`\s`, POSIX, Unicode) | [CharacterClass.java](src/Basics/Regex/CharacterClass.java) |
| 4 | Quantifiers (`*` `+` `?` `{n,m}`, greedy vs reluctant vs possessive, backtracking) | [Quantifiers.java](src/Basics/Regex/Quantifiers.java) |
| 5 | Metacharacters & Anchors (`^` `$` `\b` `\A` `\z` `\G`, `Pattern.quote`) | [MetacharactersAndAnchors.java](src/Basics/Regex/MetacharactersAndAnchors.java) |
| 6 | Groups & Backreferences (`(…)` `(?:…)` `(?<name>…)` `\1` `${name}`) | [GroupsAndBackreferences.java](src/Basics/Regex/GroupsAndBackreferences.java) |
| 7 | Lookahead & Lookbehind (`(?=…)` `(?!…)` `(?<=…)` `(?<!…)`, password rules) | [LookaroundAssertions.java](src/Basics/Regex/LookaroundAssertions.java) |
| 8 | Flags (`CASE_INSENSITIVE`, `MULTILINE`, `DOTALL`, `COMMENTS`, `UNICODE_CHARACTER_CLASS`, scoped `(?i:…)`) | [RegexFlags.java](src/Basics/Regex/RegexFlags.java) |
| 9 | Modern Features — Java 8/9/11/21 (`splitAsStream`, `asPredicate`, `Matcher.results`, `replaceAll(Function)`, pattern-matching switch on String) | [ModernRegexFeatures.java](src/Basics/Regex/ModernRegexFeatures.java) |
| 10 | Real-World Examples (email, phone, URL, IPv4, password, ISO date, hex color, slug, CSV) | [RegexExamples.java](src/Basics/Regex/RegexExamples.java) |

---

## Exception Handling

> Mechanism for handling runtime errors so a program can recover or fail
> gracefully. Uses `try`, `catch`, `finally`, `throw`, `throws`, and the
> Throwable type hierarchy.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (hierarchy, checked vs unchecked, stack trace) | [ExceptionIntroduction.java](src/Basics/ExceptionHandling/ExceptionIntroduction.java) |
| 2 | Try-Catch Block (single / multiple / multi-catch / nested / `finally`) | [TryCatchBlock.java](src/Basics/ExceptionHandling/TryCatchBlock.java) |
| 3 | `final`, `finally`, `finalize` — three confusable keywords | [FinalFinallyFinalize.java](src/Basics/ExceptionHandling/FinalFinallyFinalize.java) |
| 4 | `throw` and `throws` — raise vs declare, propagation, re-throw | [ThrowAndThrows.java](src/Basics/ExceptionHandling/ThrowAndThrows.java) |
| 5 | Custom Exceptions (checked + unchecked, carrying extra data) | [CustomException.java](src/Basics/ExceptionHandling/CustomException.java) |
| 6 | Chained Exceptions (`getCause`, `initCause`, suppressed vs cause) | [ChainedException.java](src/Basics/ExceptionHandling/ChainedException.java) |
| 7 | Null Pointer Exceptions (six causes, helpful NPE Java 14+, `Optional`) | [NullPointerExceptions.java](src/Basics/ExceptionHandling/NullPointerExceptions.java) |
| 8 | Exception Handling with Method Overriding (the `throws` rule) | [ExceptionInOverriding.java](src/Basics/ExceptionHandling/ExceptionInOverriding.java) |
| 9 | Try-with-resources — Java 7+ / 9+ (`AutoCloseable`, suppressed exceptions) | [TryWithResources.java](src/Basics/ExceptionHandling/TryWithResources.java) |
| 10 | Best Practices (top-10 dos & don'ts with code) | [ExceptionBestPractices.java](src/Basics/ExceptionHandling/ExceptionBestPractices.java) |

---

## Multithreading and Synchronization

> Multiple threads of execution inside one JVM — concurrent and parallel
> work, the synchronization primitives that keep shared state sane, and
> the Java 21 virtual-thread world. Every topic has a `.java` runner and
> a matching `<Topic>.README.md` next to it.

### 1. Foundations

| # | Topic | Source |
|---|-------|--------|
| 1 | Multithreading Introduction (concurrency vs parallelism, why threads) | [MultithreadingIntroduction.java](src/Basics/Multithreading/MultithreadingIntroduction.java) |
| 2 | Threads (`java.lang.Thread` API tour) | [Threads.java](src/Basics/Multithreading/Threads.java) |
| 3 | Thread Lifecycle (`NEW`/`RUNNABLE`/`BLOCKED`/`WAITING`/`TIMED_WAITING`/`TERMINATED`) | [ThreadLifecycle.java](src/Basics/Multithreading/ThreadLifecycle.java) |
| 4 | The Main Thread (default properties, JVM exit rules) | [MainThread.java](src/Basics/Multithreading/MainThread.java) |
| 5 | `Thread.start()` vs `Thread.run()` | [StartVsRun.java](src/Basics/Multithreading/StartVsRun.java) |
| 6 | `Thread.sleep(...)` | [ThreadSleepMethod.java](src/Basics/Multithreading/ThreadSleepMethod.java) |
| 7 | `Thread.join(...)` | [ThreadJoinMethod.java](src/Basics/Multithreading/ThreadJoinMethod.java) |
| 8 | `Thread.yield()` and `onSpinWait` | [ThreadYieldMethod.java](src/Basics/Multithreading/ThreadYieldMethod.java) |
| 9 | Thread Interruption (cooperative cancellation) | [ThreadInterruption.java](src/Basics/Multithreading/ThreadInterruption.java) |
| 10 | Thread Priority (`MIN`/`NORM`/`MAX`, OS mapping) | [ThreadPriority.java](src/Basics/Multithreading/ThreadPriority.java) |
| 11 | Daemon Threads (JVM exit semantics) | [DaemonThread.java](src/Basics/Multithreading/DaemonThread.java) |

### 2. Creating Work

| Topic | Source |
|---|---|
| Runnable Interface (lambdas, composition, decoration) | [RunnableInterface.java](src/Basics/Multithreading/RunnableInterface.java) |
| Callable & Future (results, exceptions, cancellation, FutureTask) | [CallableAndFuture.java](src/Basics/Multithreading/CallableAndFuture.java) |

### 3. Correctness (synchronization, JMM, atomics)

| Topic | Source |
|---|---|
| Java Synchronization (`synchronized` blocks/methods, monitor locks) | [JavaSynchronization.java](src/Basics/Multithreading/JavaSynchronization.java) |
| Thread Safety (strategies, levels, compound-op traps) | [ThreadSafety.java](src/Basics/Multithreading/ThreadSafety.java) |
| Race Conditions, Livelock, Starvation | [RaceConditionStarvationLivelock.java](src/Basics/Multithreading/RaceConditionStarvationLivelock.java) |
| Java Memory Model (happens-before, safe publication) | [JavaMemoryModel.java](src/Basics/Multithreading/JavaMemoryModel.java) |
| `volatile` Keyword (visibility, DCL singleton) | [VolatileKeyword.java](src/Basics/Multithreading/VolatileKeyword.java) |
| `wait` / `notify` / `notifyAll` | [WaitNotifyNotifyAll.java](src/Basics/Multithreading/WaitNotifyNotifyAll.java) |
| Producer-Consumer (three implementations) | [ProducerConsumer.java](src/Basics/Multithreading/ProducerConsumer.java) |
| `ThreadLocal<T>` | [ThreadLocalDemo.java](src/Basics/Multithreading/ThreadLocalDemo.java) |
| Atomic Variables (`Atomic*`, `LongAdder`, ABA) | [AtomicVariables.java](src/Basics/Multithreading/AtomicVariables.java) |

### 4. Locks

| Topic | Source |
|---|---|
| Locks in Java (`Lock` interface tour) | [LocksInJava.java](src/Basics/Multithreading/LocksInJava.java) |
| Lock vs Monitor in Concurrency | [LockVsMonitor.java](src/Basics/Multithreading/LockVsMonitor.java) |
| Lock Framework vs `synchronized` | [LockFrameworkVsSync.java](src/Basics/Multithreading/LockFrameworkVsSync.java) |
| `ReentrantLock` (fairness, `tryLock`, conditions) | [ReentrantLockDemo.java](src/Basics/Multithreading/ReentrantLockDemo.java) |
| `ReadWriteLock` (many readers, one writer) | [ReadWriteLockDemo.java](src/Basics/Multithreading/ReadWriteLockDemo.java) |
| `StampedLock` (optimistic reads) | [StampedLockDemo.java](src/Basics/Multithreading/StampedLockDemo.java) |
| Deadlock (Coffman, detection, prevention) | [DeadlockDemo.java](src/Basics/Multithreading/DeadlockDemo.java) |

### 5. Executors and high-level concurrency

| Topic | Source |
|---|---|
| Thread Pools (`ThreadPoolExecutor`, queues, rejection policies) | [ThreadPools.java](src/Basics/Multithreading/ThreadPools.java) |
| Executor Framework (`ExecutorService` tour) | [ExecutorFramework.java](src/Basics/Multithreading/ExecutorFramework.java) |
| `ScheduledExecutorService` (`fixedRate` vs `fixedDelay`) | [ScheduledExecutorDemo.java](src/Basics/Multithreading/ScheduledExecutorDemo.java) |
| `ForkJoinPool` (divide-and-conquer, work stealing) | [ForkJoinPoolDemo.java](src/Basics/Multithreading/ForkJoinPoolDemo.java) |
| `CompletableFuture` (composable async) | [CompletableFutureDemo.java](src/Basics/Multithreading/CompletableFutureDemo.java) |

### 6. Synchronizers

| Topic | Source |
|---|---|
| `CountDownLatch` (one-shot gate) | [CountDownLatchDemo.java](src/Basics/Multithreading/CountDownLatchDemo.java) |
| `CyclicBarrier` (resettable, barrier action) | [CyclicBarrierDemo.java](src/Basics/Multithreading/CyclicBarrierDemo.java) |
| `Semaphore` (permits, binary, fair) | [SemaphoreDemo.java](src/Basics/Multithreading/SemaphoreDemo.java) |
| `Phaser` (variable parties, per-phase actions) | [PhaserDemo.java](src/Basics/Multithreading/PhaserDemo.java) |

### 7. Java 21 — modern concurrency

| Topic | Source |
|---|---|
| Virtual Threads (JEP 444 finalised) | [VirtualThreads.java](src/Basics/Multithreading/VirtualThreads.java) |
| Structured Concurrency (JEP 453 preview) | [StructuredConcurrency.java](src/Basics/Multithreading/StructuredConcurrency.java) |
| Scoped Values (JEP 446 preview) | [ScopedValuesDemo.java](src/Basics/Multithreading/ScopedValuesDemo.java) |

### 8. End-to-end

| Topic | Source |
|---|---|
| Multithreading Complete Tutorial (one-file tour) | [MultithreadingCompleteTutorial.java](src/Basics/Multithreading/MultithreadingCompleteTutorial.java) |

### Project

| Topic | Source |
|---|---|
| Snake Game (Swing render thread + game loop thread + locked state) | [Direction](src/Basics/Multithreading/SnakeGame/Direction.java) · [Cell](src/Basics/Multithreading/SnakeGame/Cell.java) · [GameState](src/Basics/Multithreading/SnakeGame/GameState.java) · [GameLoop](src/Basics/Multithreading/SnakeGame/GameLoop.java) · [SnakeBoard](src/Basics/Multithreading/SnakeGame/SnakeBoard.java) · **[Runner: SnakeGame.java](src/Basics/Multithreading/SnakeGame/SnakeGame.java)** |

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

> Java's organising paradigm — classes, objects, and the four pillars
> (abstraction, encapsulation, inheritance, polymorphism). The section ends
> with a small **Simple Banking Application** that uses all of them together.

| # | Topic | Source |
|---|-------|--------|
| 1 | Introduction (the four pillars in one mini-demo) | [OopIntroduction.java](src/OOPSConcepts/Introduction/OopIntroduction.java) |
| 2 | Classes & Objects | [Initializing object](src/OOPSConcepts/ClassesAndObject/InitializingObject.java) · [Ways to create an object](src/OOPSConcepts/ClassesAndObject/WaysToCreateObject.java) |
| 3 | Constructors (default / parameterized / overloading / chaining / copy / private) | [Constructors.java](src/OOPSConcepts/Constructors/Constructors.java) |
| 4 | Object Class (equals / hashCode / toString / clone / getClass / records) | [ObjectClassMethods.java](src/OOPSConcepts/ObjectClass/ObjectClassMethods.java) |
| 5 | Abstraction | [Abstract class](src/OOPSConcepts/Abstraction/AbstractClassExample.java) · [Interfaces](src/OOPSConcepts/Abstraction/InterfaceExample.java) · [Abstract vs Interface](src/OOPSConcepts/Abstraction/AbstractClassVsInterface.java) |
| 6 | Encapsulation | [TestEncapsulation.java](src/OOPSConcepts/Encapsulation/TestEncapsulation.java) |
| 7 | Inheritance | [Intro](src/OOPSConcepts/Inheritance/InheritanceJava.java) · [Single](src/OOPSConcepts/Inheritance/TypesOfInheritance/SingleInheritance.java) · [Multi-level](src/OOPSConcepts/Inheritance/TypesOfInheritance/MultiLevelInheritance.java) · [Hierarchical](src/OOPSConcepts/Inheritance/TypesOfInheritance/HierarchicalInheritance.java) · [Multiple (interfaces)](src/OOPSConcepts/Inheritance/TypesOfInheritance/MultipleInheritance.java) · [Hybrid](src/OOPSConcepts/Inheritance/TypesOfInheritance/HybridInheritance.java) |
| 8 | Polymorphism (overloading + overriding + dynamic dispatch + covariant returns) | [Polymorphism.java](src/OOPSConcepts/Polymorphism/Polymorphism.java) |
| 9 | Packages and Imports (single / wildcard / static, package-private access) | [PackagesAndImports.java](src/OOPSConcepts/Packages/PackagesAndImports.java) |
| 10 | Sealed Classes — Java 17+ (`sealed` / `non-sealed` / `permits`) | [SealedClassesDemo.java](src/OOPSConcepts/SealedClasses/SealedClassesDemo.java) |
| 11 | Project: Simple Banking Application | [Account](src/OOPSConcepts/BankingApp/Account.java) · [SavingsAccount](src/OOPSConcepts/BankingApp/SavingsAccount.java) · [CheckingAccount](src/OOPSConcepts/BankingApp/CheckingAccount.java) · [Transaction (record)](src/OOPSConcepts/BankingApp/Transaction.java) · [Bank](src/OOPSConcepts/BankingApp/Bank.java) · **[Runner: BankingApp.java](src/OOPSConcepts/BankingApp/BankingApp.java)** |
| 12 | Serialization (extra) | [Demo 1](src/OOPSConcepts/SerializationDeserialization/SerializationDeserializationDemoOne.java) · [Demo 2](src/OOPSConcepts/SerializationDeserialization/SerializationDeserializationDemoTwo.java) |

---

## Interfaces

> Contracts a class agrees to satisfy. Interfaces give Java its main form
> of abstraction and let one class fulfil multiple types.

| # | Topic | Source |
|---|-------|--------|
| 1 | Interfaces — full tour (abstract / default / static / private members, multiple impl, interface inheritance) | [InterfaceIntro.java](src/OOPSConcepts/Interfaces/InterfaceIntro.java) |
| 2 | Class vs Interface (side-by-side, real-world JDK pattern) | [ClassVsInterface.java](src/OOPSConcepts/Interfaces/ClassVsInterface.java) · [longer version](src/OOPSConcepts/Abstraction/AbstractClassVsInterface.java) |
| 3 | Functional Interface (SAM, `@FunctionalInterface`, `java.util.function`, composition, lambdas, comparators) | [FunctionalInterfaceDemo.java](src/OOPSConcepts/Interfaces/FunctionalInterfaceDemo.java) |
| 4 | Nested Interface (inside class, inside interface, `private` nested) | [NestedInterface.java](src/OOPSConcepts/Interfaces/NestedInterface.java) |
| 5 | Marker Interface (`Serializable`, custom marker, annotation alternative, generic upper bound) | [MarkerInterface.java](src/OOPSConcepts/Interfaces/MarkerInterface.java) |
| 6 | Sealed Interfaces — Java 17+ (`permits`, `non-sealed`, exhaustive switch) | [SealedInterfaceDemo.java](src/OOPSConcepts/Interfaces/SealedInterfaceDemo.java) |
| 7 | Project: Employee Management System | [Employee (sealed)](src/OOPSConcepts/EmployeeApp/Employee.java) · [FullTimeEmployee](src/OOPSConcepts/EmployeeApp/FullTimeEmployee.java) · [PartTimeEmployee](src/OOPSConcepts/EmployeeApp/PartTimeEmployee.java) · [Contractor](src/OOPSConcepts/EmployeeApp/Contractor.java) · [Intern](src/OOPSConcepts/EmployeeApp/Intern.java) · [Promotable](src/OOPSConcepts/EmployeeApp/Promotable.java) · [Auditable (marker)](src/OOPSConcepts/EmployeeApp/Auditable.java) · [EmployeeFilter (functional)](src/OOPSConcepts/EmployeeApp/EmployeeFilter.java) · [EmployeeRepository](src/OOPSConcepts/EmployeeApp/EmployeeRepository.java) · **[Runner: EmployeeApp.java](src/OOPSConcepts/EmployeeApp/EmployeeApp.java)** |

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
