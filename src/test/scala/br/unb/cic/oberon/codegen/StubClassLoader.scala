package br.unb.cic.oberon.codegen

class StubClassLoader extends ClassLoader {
  def getClass(name: String, b: Array[Byte]): Class[_] = {
    return defineClass(name, b, 0, b.length);
  }
}


