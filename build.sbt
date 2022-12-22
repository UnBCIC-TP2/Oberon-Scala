scalaVersion := "2.13.1"

name := "oberon-lang"
organization := "br.unb.cic"
version := "0.1.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

// githubOwner := "UnBCIC-TP2"
// githubRepository := "Oberon-Scala"
//  githubTokenSource := TokenSource.GitConfig("github.token")

enablePlugins(Antlr4Plugin)

antlr4PackageName in Antlr4 := Some("br.unb.cic.oberon.parser")

antlr4GenListener in Antlr4 := false // default: true

antlr4GenVisitor in Antlr4 := true // default: false

libraryDependencies ++= Seq(
  "org.antlr" % "antlr4-runtime" % "4.6",
  "org.antlr" % "stringtemplate" % "3.2"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "org.scalatest" %% "scalatest-featurespec" % "3.2.0" % "test"
libraryDependencies += "org.typelevel" %% "paiges-core" % "0.3.0"


libraryDependencies += "org.scala-graph" %% "graph-core" % "1.13.2"
libraryDependencies += "org.scala-graph" %% "graph-dot" % "1.13.0"

libraryDependencies += "org.scala-sbt.jline3" % "jline-terminal" % "3.16.0-sbt-211a082ed6326908dc84ca017ce4430728f18a8a"

libraryDependencies += "org.rogach" %% "scallop" % "4.0.2"
libraryDependencies += "org.ow2.asm" % "asm" % "9.1"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"

libraryDependencies += "org.jline" % "jline" % "3.21.0"

Compile / unmanagedResourceDirectories += baseDirectory.value / "target/scala-2.13/src_managed/main/antlr4/"
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}