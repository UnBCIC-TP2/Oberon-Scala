package br.unb.cic.oberon.util

object Resources {
  def getContent(resource: String): String = {
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    val content = new String(stream.readAllBytes, "utf-8").replaceAll("\r\n", "\n")
    content
  }
}