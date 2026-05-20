# Dynamic Invocation

Two faster / safer alternatives to `Method.invoke`:

| | `MethodHandle` | Dynamic `Proxy` |
|---|---|---|
| What it does | Strongly-typed handle to a method | Class created at runtime implementing N interfaces |
| Use for | Hot reflective calls | AOP, mocking, instrumenting interfaces |
| Speed | Approaches direct call | Reflection overhead per dispatch |

## `MethodHandle`
```java
MethodHandles.Lookup lookup = MethodHandles.lookup();
MethodHandle h = lookup.findVirtual(
        String.class, "toUpperCase", MethodType.methodType(String.class));
Object out = h.invoke("hello");          // returns "HELLO"
```

| Find | Method |
|---|---|
| Virtual instance method | `findVirtual(Class, name, MethodType)` |
| Static method | `findStatic(Class, name, MethodType)` |
| Constructor | `findConstructor(Class, MethodType)` |
| Getter / setter | `findGetter` / `findSetter` |

`MethodType.methodType(returnType, paramTypes...)` describes the signature.
`invokeExact` requires the call-site types to match exactly; `invoke` does
automatic conversions.

## Why `MethodHandle` over `Method.invoke`
- Much faster (the JVM treats handles like `invokedynamic`).
- Doesn't wrap user exceptions.
- Strongly typed — fewer `Object` casts.

## Dynamic Proxy
```java
Greeting proxy = (Greeting) Proxy.newProxyInstance(
        Greeting.class.getClassLoader(),
        new Class<?>[]{Greeting.class},
        (p, method, args) -> {
            System.out.println("called " + method.getName());
            return method.invoke(real, args);
        });
```

Common uses:
- **Logging / timing** around every call.
- **Mocks** for tests (record + return canned values).
- **Transactional / retrying / cached** wrappers — Spring AOP for JDK-proxy beans does this.

## Limitations
- JDK `Proxy` works on **interfaces only**. For class proxies use CGLIB, ByteBuddy, or ASM.
- Sealed interfaces require all permitted subclasses to be proxied — usually impossible.

## Sketch: AOP-style timing wrapper
```java
class TimingHandler implements InvocationHandler {
    private final Object target;
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        long t0 = System.nanoTime();
        try { return m.invoke(target, args); }
        finally { log(m.getName(), System.nanoTime() - t0); }
    }
}
```

## Module-system note
`MethodHandles.lookup()` returns a `Lookup` with the privileges of the caller.
`privateLookupIn(targetClass, callerLookup)` gives access to a class's private
members, subject to module `opens` rules.

## Run
```bash
cd src
java Basics.Reflection.DynamicInvocation
```

## See also
- `ClassAndMethodReflection.java` — the slower but more familiar `Method.invoke`.
- `ReflectionIntroduction.java` — the basics.
- `Basics/Annotations/RuntimeAnnotations.java` — proxy + annotations = AOP.
