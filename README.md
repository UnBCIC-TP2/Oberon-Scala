## Oberon Language

This is an implementation of the Oberon language in Scala.

### Compiling and Testing

   * Compiling
   
```shell
$ sbt compile

[...]

[info] Loading global plugins from /Users/rbonifacio/.sbt/1.0/plugins
[info] Loading settings for project oberon-scala-build from assembly.sbt,sbt-antlr4.sbt,plugins.sbt ...
[info] Loading project definition from /Users/rbonifacio/Documents/workspace-scala/Oberon-Scala/project
[info] Loading settings for project oberon-scala from build.sbt ...
[info] Set current project to oberon-lang (in build file:/Users/rbonifacio/Documents/workspace-scala/Oberon-Scala/)
[info] Executing in batch mode. For better performance use sbt's shell
[success] Total time: 1 s, completed 25/08/2021 14:34:33
```

   * Executing the test cases

```shell
$ sbt test

[...]

[info] Run completed in 2 seconds, 286 milliseconds.
[info] Total number of tests run: 246
[info] Suites: completed 14, aborted 0
[info] Tests: succeeded 246, failed 0, canceled 0, ignored 62, pending 0
[info] All tests passed.
[success] Total time: 4 s, completed 25/08/2021 14:36:21

```

You can also try a combination of the above commands, for instance `sbt compile test`. It is also possible to run `sbt` in a "shell" mode (this might save compilation and building time). To start the sbt shell model, just type `sbt` in a terminal.

```shell
$ sbt
[info] Loading global plugins from /Users/rbonifacio/.sbt/1.0/plugins
[info] Loading settings for project oberon-scala-build from assembly.sbt,sbt-antlr4.sbt,plugins.sbt ...
[info] Loading project definition from /Users/rbonifacio/Documents/workspace-scala/Oberon-Scala/project
[info] Loading settings for project oberon-scala from build.sbt ...
[info] Set current project to oberon-lang (in build file:/Users/rbonifacio/Documents/workspace-scala/Oberon-Scala/)
[info] sbt server started at local:///Users/rbonifacio/.sbt/1.0/server/5b1186f4048efb7df7ec/sock

sbt:oberon-lang> 
```

You can now execute `sbt` commands directly. See some examples bellow. 


```shell
sbt:oberon-lang> compile

[success] Total time: 1 s, completed 25/08/2021 14:44:10

sbt:oberon-lang> test

[...]

[info] Run completed in 1 second, 604 milliseconds.
[info] Total number of tests run: 246
[info] Suites: completed 14, aborted 0
[info] Tests: succeeded 246, failed 0, canceled 0, ignored 62, pending 0
[info] All tests passed.
[success] Total time: 3 s, completed 25/08/2021 14:44:41

sbt:oberon-lang>
```

### Building an executable JAR file

In order to generate an executable JAR file, you can run the `sbt assembly` command.

```shell
$ sbt assembly

[...]
[info] Run completed in 1 second, 781 milliseconds.
[info] Total number of tests run: 246
[info] Suites: completed 14, aborted 0
[info] Tests: succeeded 246, failed 0, canceled 0, ignored 62, pending 0
[info] All tests passed.

[info] Assembly up to date: .../target/scala-2.13/oberon-lang-assembly-0.1.1.jar
[success] Total time: 5 s, completed 25/08/2021 14:45:56

```

This command generates the `oberon-lang-assembly-0.1.1.jar` artifact into the `target/scala-2.13/` folder. You can execute this artifact using the following command, which prints some usage help of our implementation.

```shell
$ cd target/scala-2.13
$ java -jar oberon-lang-assembly-0.1.1.jar --help
```

### Using the Oberon JAR executable

After you have the `oberon-lang-assembly-0.1.1.jar` executable, you should be able to run various commands. Some of the commands are exemplified below:

```
$ java -jar oberon-lang-assembly-0.1.1.jar --help
```
Displays a help message with all possible commands ans options


```
$ java -jar typeChecker -i <filename>.oberon
```
Runs the type checker on an Oberon file.

```
$ java -jar compile -b c -i <filename>.oberon -o <filename>.c
```
Compiles an oberon file into a c file.
