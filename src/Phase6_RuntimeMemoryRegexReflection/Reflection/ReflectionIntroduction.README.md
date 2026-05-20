# Reflection API — Introduction

Reflection lets your code **inspect and manipulate** other code at runtime:
classes, methods, fields, constructors, annotations, generic info.

## Why use it
- **Frameworks** — Spring autowiring, Hibernate, Jackson, JUnit discovery.
- **Plugins / service loaders.**
- **Tooling** — debuggers, profilers, IDE features.
- **Generic test helpers.**

## Why NOT to use it in business code
- Slower than direct calls (the JIT helps a lot, but not free).
- Bypasses compile-time type checks.
- May break under the module system without `opens` directives.
- Stack traces wrap user exceptions in `InvocationTargetException`.
- Hard to refactor — renaming a field becomes a runtime bomb.

## Starting point: `Class<?>`
Three ways to obtain a `Class`:
```java
Class<?> c = String.class;
Class<?> c = "hello".getClass();
Class<?> c = Class.forName("java.lang.String");
```

## Headline types in `java.lang.reflect`
| Type | Describes |
|---|---|
| `Class<?>` | The class itself |
| `Method` | A method |
| `Field` | A field |
| `Constructor<T>` | A constructor |
| `Parameter` | A parameter of a method / constructor |
| `Modifier` | Helpers for `int` modifier bits |
| `Array` | Reflective array creation / access |

## Key Class methods
| Method | Returns |
|---|---|
| `getName()` / `getSimpleName()` / `getPackageName()` | Names |
| `getModifiers()` (decode with `Modifier.toString`) | Modifier bits |
| `getSuperclass()` / `getInterfaces()` | Hierarchy |
| `getDeclaredMethods()` / `getMethods()` | Methods (declared / inherited public) |
| `getDeclaredFields()` / `getFields()` | Fields |
| `getDeclaredConstructors()` / `getConstructors()` | Constructors |
| `getAnnotations()` / `getDeclaredAnnotations()` | Annotations |

## Instantiate, invoke, access
```java
Constructor<Greeter> ctor = Greeter.class.getDeclaredConstructor(String.class);
Greeter g = ctor.newInstance("alice");

Method m = Greeter.class.getMethod("greet", String.class);
Object out = m.invoke(g, "hello");

Field f = Greeter.class.getDeclaredField("name");
f.setAccessible(true);
Object v = f.get(g);
```

## `setAccessible(true)`
Bypasses access checks (private / package). Under the module system, the
target package must be `opens`-exported to your module, **or** the JVM
flagged `--add-opens` at startup. See the Modules section.

## Run
```bash
cd src
java Basics.Reflection.ReflectionIntroduction
```

## See also
- `ClassAndMethodReflection.java` — deep dive.
- `DynamicInvocation.java` — dynamic proxies, `MethodHandles`.
- `Basics/Annotations/RuntimeAnnotations.java` — reading annotations via reflection.
