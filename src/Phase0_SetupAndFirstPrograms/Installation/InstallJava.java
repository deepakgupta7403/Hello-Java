package Phase0_SetupAndFirstPrograms.Installation;

/**
 * <h1>Downloading and Installing Java</h1>
 *
 * <p>To run any Java program you need the JDK (Java Development Kit) installed on
 * your machine. The JDK contains the compiler (<code>javac</code>), the runtime
 * (<code>java</code>), and various development tools (<code>jar</code>,
 * <code>javadoc</code>, <code>jshell</code>, etc.).</p>
 *
 * <h2>Step 1 &mdash; Choose a JDK Distribution</h2>
 * <p>Multiple vendors ship the JDK. All of them are based on the same OpenJDK source
 * code, so any of them will work:</p>
 * <ul>
 *   <li><b>Oracle JDK</b> &mdash; https://www.oracle.com/java/technologies/downloads/</li>
 *   <li><b>Eclipse Temurin (Adoptium)</b> &mdash; https://adoptium.net/</li>
 *   <li><b>Amazon Corretto</b> &mdash; https://aws.amazon.com/corretto/</li>
 *   <li><b>Azul Zulu</b> &mdash; https://www.azul.com/downloads/</li>
 *   <li><b>Microsoft Build of OpenJDK</b> &mdash; https://learn.microsoft.com/java/openjdk/</li>
 * </ul>
 *
 * <p>For learning, prefer an LTS (Long Term Support) version: Java 17 or Java 21.</p>
 *
 * <h2>Step 2 &mdash; Install</h2>
 * <p>Windows:</p>
 * <pre>
 * 1. Download the .msi or .exe installer for your architecture (x64 / aarch64).
 * 2. Run the installer (defaults are fine).
 * 3. The installer usually adds Java to PATH automatically. If not, add manually:
 *      a. Win + R -&gt; sysdm.cpl -&gt; Advanced -&gt; Environment Variables
 *      b. Create JAVA_HOME pointing to your JDK folder
 *         (e.g. C:\Program Files\Java\jdk-21).
 *      c. Edit PATH and add %JAVA_HOME%\bin
 * </pre>
 *
 * <p>macOS:</p>
 * <pre>
 * 1. Download the .pkg installer or install via Homebrew:
 *      brew install --cask temurin
 * 2. Verify JAVA_HOME (zsh):
 *      export JAVA_HOME=$(/usr/libexec/java_home -v 21)
 * </pre>
 *
 * <p>Linux (Debian/Ubuntu):</p>
 * <pre>
 * sudo apt update
 * sudo apt install openjdk-21-jdk
 * </pre>
 *
 * <h2>Step 3 &mdash; Verify the Installation</h2>
 * <p>Open a fresh terminal/PowerShell and run:</p>
 * <pre>
 * java -version
 * javac -version
 * </pre>
 *
 * <p>Expected output (versions will vary):</p>
 * <pre>
 * openjdk version "21.0.2" 2024-01-16 LTS
 * OpenJDK Runtime Environment Temurin-21.0.2+13 (build 21.0.2+13-LTS)
 * OpenJDK 64-Bit Server VM Temurin-21.0.2+13 (build 21.0.2+13-LTS, mixed mode)
 *
 * javac 21.0.2
 * </pre>
 *
 * <h2>Step 4 &mdash; Pick an IDE (Optional but recommended)</h2>
 * <ul>
 *   <li><b>IntelliJ IDEA (Community Edition is free)</b> &mdash; https://www.jetbrains.com/idea/</li>
 *   <li><b>Eclipse</b> &mdash; https://www.eclipse.org/downloads/</li>
 *   <li><b>VS Code + "Extension Pack for Java"</b> &mdash; https://code.visualstudio.com/</li>
 *   <li><b>Apache NetBeans</b> &mdash; https://netbeans.apache.org/</li>
 * </ul>
 *
 * <h2>Step 5 &mdash; Compile and Run Your First Program (Manually)</h2>
 * <p>Without an IDE, the workflow is:</p>
 * <pre>
 * javac HelloWorld.java     // produces HelloWorld.class (bytecode)
 * java HelloWorld           // executes the bytecode in the JVM
 * </pre>
 *
 * <p>Since Java 11 you can also run a single-file program directly without compiling:</p>
 * <pre>
 * java HelloWorld.java
 * </pre>
 *
 * <h2>Troubleshooting</h2>
 * <ul>
 *   <li><b>"java is not recognized"</b> &mdash; PATH is not set. Re-do Step 2.</li>
 *   <li><b>"Unsupported major.minor"</b> &mdash; Code compiled with a newer JDK than
 *       your runtime. Install a newer JDK or recompile with <code>--release</code>.</li>
 *   <li><b>Multiple Java versions</b> &mdash; Use <code>JAVA_HOME</code> to point to the
 *       one you want.</li>
 * </ul>
 *
 * <p>This file is documentation only. There is nothing to execute here.</p>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */

public class InstallJava {

    public static void main(String[] args) {
        // Quickly print the active JDK location so you can verify your install.
        System.out.println("java.home    = " + System.getProperty("java.home"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("java.vendor  = " + System.getProperty("java.vendor"));
    }
}
