# `Class` / `Method` / `Field` in depth

## Finding methods
| Method | Scope |
|---|---|
| `getMethod("n", paramTypes...)` | **Public**, includes inherited |
| `getDeclaredMethod("n", paramTypes...)` | Any access, **this class only** |
| `getMethods()` | All public (incl. inherited) |
| `getDeclaredMethods()` | All declared here |

For overloaded methods, match parameter types **exactly** (`int.class` vs
`Integer.class` are different methods).

## Invocation
```java
Object result = method.invoke(instance, arg1, arg2);
```
- Static method? Pass `null` for `instance`.
- Returns `Object` (boxed for primitives).
- Exceptions thrown by the target are wrapped in `InvocationTargetException`; unwrap with `.getCause()`.

## Field access
| Method | Effect |
|---|---|
| `field.get(instance)` | Read (boxed) |
| `field.set(instance, v)` | Write |
| `field.getInt(instance)` / `getLong` / `getDouble` / … | Primitive read (no boxing) |
| `field.setInt(instance, v)` / … | Primitive write |

**Final fields:** writable via `setAccessible(true)` only on **instance** finals,
not `static final` constants (the JIT may have inlined them).

## Modifiers
```java
int m = method.getModifiers();
Modifier.isStatic(m);   Modifier.isFinal(m);
Modifier.isPublic(m);   Modifier.isPrivate(m);
String s = Modifier.toString(m);
```

## Parameters
| Bit | What |
|---|---|
| `method.getParameters()` | `Parameter[]` |
| `param.getName()` | The compile-time name — requires `javac -parameters` |
| `param.getType()` | Declared type |
| `param.isVarArgs()` | Last varargs parameter |

Without `-parameters`, names appear as `arg0`, `arg1`, etc.

## Generic type info
```java
field.getGenericType();             // includes <T>
method.getGenericReturnType();
method.getGenericParameterTypes();

ParameterizedType pt = (ParameterizedType) field.getGenericType();
Type[] typeArgs = pt.getActualTypeArguments();
```

## Performance tips
- Cache `Method` / `Field` / `Constructor` lookups; don't re-find every call.
- For hot paths, switch to `MethodHandles.Lookup` (see `DynamicInvocation.java`) — closer to direct-call speed.
- Make accessor objects accessible **once**, not per call.

## Module access (Java 9+)
`setAccessible(true)` on a non-public member of another module requires the
target package to be `opens`-exported to your module, or the JVM started with
`--add-opens module/pkg=ALL-UNNAMED`.

## Run
```bash
cd src
java Basics.Reflection.ClassAndMethodReflection
```

## See also
- `ReflectionIntroduction.java`.
- `DynamicInvocation.java` — `MethodHandles`, dynamic proxies.
- `Basics/Annotations/RuntimeAnnotations.java` — annotation reading.
