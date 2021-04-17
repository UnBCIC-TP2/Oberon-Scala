package br.unb.cic.oberon.util

import scala.io.Source
import java.nio.file.Paths

object Resources {
  def getContent(resource: String): String = {
    val path = Paths.get(getClass.getClassLoader.getResource(resource).toURI)
    assert(path != null)

    val i = Source.fromFile(path.toString)
    val content = i.getLines().toString().replace("\n\r", "\n")

    i.close()

    content
  }
}