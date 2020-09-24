scalaVersion := "2.13.1"

name := "oberon-lang"
organization := "br.unb.cic"
version := "0.1.1-SNAPSHOT"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

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

libraryDependencies += "org.scala-graph" %% "graph-core" % "1.13.2"

Compile / unmanagedResourceDirectories += baseDirectory.value / "target/scala-2.13/src_managed/main/antlr4/"

