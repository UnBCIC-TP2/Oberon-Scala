scalaVersion := "2.13.1"

name := "oberon-lang"
organization := "br.unb.cic"
version := "0.1.1-SNAPSHOT"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

enablePlugins(Antlr4Plugin)

antlr4PackageName in Antlr4 := Some("br.unb.cic.oberon.parser")

antlr4GenListener in Antlr4 := false // default: true

antlr4GenVisitor in Antlr4 := true // default: false
