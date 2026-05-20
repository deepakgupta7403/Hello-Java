# Annotations — Introduction

Annotations are **metadata** attached to code. They don't change behaviour
directly — tools and libraries read them and act on them (compiler checks,
frameworks, code generators, IDEs).

## The four families
| | What |
|---|---|
| Built-in markers | `@Override`, `@Deprecated`, `@FunctionalInterface` |
| Compiler hints | `@SuppressWarnings`, `@SafeVarargs` |
| Your custom annotations | Defined with `@interface` |
| Meta-annotations | Annotations *on* annotations: `@Retention`, `@Target`, `@Inherited`, `@Repeatable`, `@Documented` |

## Anatomy
```java
@MyAnnotation(name = "alice", priority = 5)
public class C { ... }
```
- `@` marks it.
- Elements (`name`, `priority`) are compile-time constants.
- A single-element annotation called `value` can drop the name:
  `@Author("alice")`.

## Where they can appear
- Types (class, interface, enum, record).
- Methods, constructors.
- Parameters, return types.
- Fields, local variables.
- Type uses (Java 8+): `List<@NonNull String>`.
- Modules and packages (`module-info.java`, `package-info.java`).

## Retention levels (`@Retention`)
| Level | Lives in | Tools that can see it |
|---|---|---|
| `SOURCE` | Source only | Compiler / lint / annotation processors |
| `CLASS` | `.class` file, not at runtime | Bytecode tools |
| `RUNTIME` | `.class` and visible via reflection | Frameworks (Spring, JUnit, JPA) |

If a framework reads it with reflection, it **must** be `RUNTIME`.

## Real-world examples
- **JUnit**: `@Test`, `@BeforeEach`
- **Spring**: `@Component`, `@Autowired`, `@Transactional`
- **JPA**: `@Entity`, `@Id`, `@Column`
- **Lombok**: `@Data`, `@Builder` (source-level code generation)
- **Jakarta Validation**: `@NotNull`, `@Size`, `@Email`

## Run
```bash
cd src
java Basics.Annotations.AnnotationsIntroduction
```

## See also
- `BuiltInAnnotations.java` — the standard library annotations.
- `CustomAnnotations.java` — write your own.
- `RuntimeAnnotations.java` — read them via reflection.
