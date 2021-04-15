package br.unb.cic.oberon.util

import java.nio.file.{Paths, Files}

object Resources {
  def getContent(resource: String): String = {
    val path = Paths.get(getClass.getClassLoader.getResource(resource).toURI)
    assert(path != null)
    val content = String.join("\n", Files.readAllLines(path))
    content
  }
}