package br.unb.cic.oberon.util

import scala.io.Source

object Resources {
  def getContent(resource: String): String = {
    val i = Source.fromFile(resource)
    val content = i.getLines().toString().replace("\n\r", "\n")

    i.close()

    content
  }
}