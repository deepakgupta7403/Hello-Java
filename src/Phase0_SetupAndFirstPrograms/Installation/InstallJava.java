package Phase0_SetupAndFirstPrograms.Installation;

/**
 * Downloading and Installing Java
 * -------------------------------
 * To run any Java program you need the JDK (Java Development Kit) installed on
 * your machine. The JDK contains the compiler (javac), the runtime (java), and
 * various development tools (jar, javadoc, jshell, etc.).
 * <p>
 *
 * Step 1 - Choose a JDK Distribution
 * ----------------------------------
 * Multiple vendors ship the JDK. All of them are based on the same OpenJDK source
 * code, so any of them will work:
 *   - Oracle JDK                 - https://www.oracle.com/java/technologies/downloads/
 *   - Eclipse Temurin (Adoptium) - https://adoptium.net/
 *   - Amazon Corretto            - https://aws.amazon.com/corretto/
 *   - Azul Zulu                  - https://www.azul.com/downloads/
 *   - Microsoft Build of OpenJDK - https://learn.microsoft.com/java/openjdk/
 * <p>
 * For learning, prefer an LTS (Long Term Support) version: Java 17 or Java 21.
 * <p>
 *
 * Step 2 - Install
 * ----------------
 * Windows:
 *   1. Download the .msi or .exe installer for your architecture (x64 / aarch64).
 *   2. Run the installer (defaults are fine).
 *   3. The installer usually adds Java to PATH automatically. If not, add manually:
 *        a. Win + R -> sysdm.cpl -> Advanced -> Environment Variables
 *        b. Create JAVA_HOME pointing to your JDK folder
 *           (e.g. C:\Program Files\Java\jdk-21).
 *        c. Edit PATH and add %JAVA_HOME%\bin
 * <p>
 * macOS:
 *   1. Download the .pkg installer or install via Homebrew:
 *        brew install --cask temurin
 *   2. Verify JAVA_HOME (zsh):
 *        export JAVA_HOME=$(/usr/libexec/java_home -v 21)
 * <p>
 * Linux (Debian/Ubuntu):
 *        sudo apt update
 *        sudo apt install openjdk-21-jdk
 * <p>
 *
 * Step 3 - Verify the Installation
 * --------------------------------
 * Open a fresh terminal/PowerShell and run:
 * <p>
 *        java -version
 *        javac -version
 * <p>
 * Expected output (versions will vary):
 *        openjdk version "21.0.2" 2024-01-16 LTS
 *        OpenJDK Runtime Environment Temurin-21.0.2+13 (build 21.0.2+13-LTS)
 *        OpenJDK 64-Bit Server VM Temurin-21.0.2+13 (build 21.0.2+13-LTS, mixed mode)
 * <p>
 *        javac 21.0.2
 * <p>
 *
 * Step 4 - Pick an IDE (Optional but recommended)
 * -----------------------------------------------
 * - IntelliJ IDEA (Community Edition is free) - https://www.jetbrains.com/idea/
 * - Eclipse                                   - https://www.eclipse.org/downloads/
 * - VS Code + "Extension Pack for Java"       - https://code.visualstudio.com/
 * - Apache NetBeans                           - https://netbeans.apache.org/
 * <p>
 *
 * Step 5 - Compile and Run Your First Program (Manually)
 * ------------------------------------------------------
 * Without an IDE, the workflow is:
 * <p>
 *        javac HelloWorld.java     // produces HelloWorld.class (bytecode)
 *        java HelloWorld           // executes the bytecode in the JVM
 * <p>
 * Since Java 11 you can also run a single-file program directly without compiling:
 * <p>
 *        java HelloWorld.java
 * <p>
 *
 * Troubleshooting
 * ---------------
 * - "java is not recognized" - PATH is not set. Re-do Step 2.
 * - "Unsupported major.minor"  - Code compiled with a newer JDK than your runtime.
 *                                Install a newer JDK or recompile with --release.
 * - Multiple Java versions    - Use JAVA_HOME to point to the one you want.
 * <p>
 *
 * This file is documentation only. There is nothing to execute here.
 */

public class InstallJava {

    public static void main(String[] args) {
        // Quickly print the active JDK location so you can verify your install.
        System.out.println("java.home    = " + System.getProperty("java.home"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("java.vendor  = " + System.getProperty("java.vendor"));
    }
}
