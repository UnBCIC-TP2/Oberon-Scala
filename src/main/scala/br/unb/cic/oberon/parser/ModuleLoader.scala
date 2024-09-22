package br.unb.cic.oberon.parser

import br.unb.cic.oberon.ir.ast.{OberonModule, SequenceStmt}
import br.unb.cic.oberon.ir.common.{Statement}
import br.unb.cic.oberon.util.Resources

import scala.io.Source
import scala.reflect.io.Path

/** Loads/Parses a module along with the whole tree of imported modules. By
  * default, modules are loaded from file paths, so if you want to load them
  * from resources, you can mix ContentFromResource like this: `val loader = new
  * ModuleLoader with ContentFromResource`.
  *
  * The most common usage of this class is already implemented by
  * `ModuleLoader.loadAndMerge`.
  */
class ModuleLoader {

  var main: Option[String] = None
  var modules = Map.empty[String, OberonModule]
  var readFiles =
    Set.empty[String] // TODO: normalize files before putting in the set

  // TODO: handle case where `file` does not exist or we can't get the content
  def load(file: Path): Boolean = {
    if (readFiles.contains(file.path)) {
      return false
    }

    // Read and parse `file`
    readFiles += file.path
    val input = getContent(file)
    val module = ScalaParser.parse(input)

    add(module)

    // Set the main module if needed
    if (main.isEmpty)
      main = Some(module.name)

    // Load submodules
    module.submodules.map(submodule => {
      val path = file.parent / Path(s"$submodule.oberon")
      load(path)
    })
    true
  }

  def add(module: OberonModule): Unit = {
    modules += module.name -> module
  }

  protected def getContent(file: Path): String = {
    val iterator = Source.fromFile(file.toURI)
    val content = iterator.getLines().mkString("\n")

    iterator.close()

    content
  }

}

sealed trait ContentFromResource extends ModuleLoader {
  protected override def getContent(resource: Path): String =
    Resources.getContent(resource.path)
}

/** Merges the modules loaded by a ModuleLoader into one big module */
sealed class ModuleMerger(val loader: ModuleLoader) {
  var merged = Set.empty[String]

  // Merge the subtree of `modname`
  def merge(modname: String): OberonModule = {
    merged += modname

    val module = loader.modules(modname)
    val submodules =
      module.submodules.iterator // iterate lazily so `filter` works correctly
        .filterNot(merged contains _) // ignore "already merged" modules
        .map(merge)
        .toList

    val subtree =
      submodules ++ List(module) // The order is important for `stmt`

    val userTypes = subtree.flatMap(_.userTypes)
    val constants = subtree.flatMap(_.constants)
    val variables = subtree.flatMap(_.variables)
    val procedures = subtree.flatMap(_.procedures)
    val tests = subtree.flatMap(_.tests)
    val stmt = concatStmts(subtree.flatMap(_.stmt))

    OberonModule(
      modname,
      Set.empty,
      userTypes,
      constants,
      variables,
      procedures,
      tests,
      Some(stmt)
    )
  }

  def merge(): OberonModule = merge(loader.main.get)

  private def concatStmts(stmts: List[Statement]) =
    SequenceStmt(
      stmts.flatMap(stmt =>
        stmt match {
          case SequenceStmt(lst) => lst
          case other             => List(other)
        }
      )
    )
}

/** Static methods for ModuleLoader */
object ModuleLoader {
  def loadAndMerge(file: String): OberonModule = {
    val loader = new ModuleLoader
    loader.load(file)
    new ModuleMerger(loader).merge()
  }
}

object ResourceModuleLoader {
  def loadAndMerge(file: String): OberonModule = {
    val loader = new ModuleLoader with ContentFromResource
    loader.load(file)
    new ModuleMerger(loader).merge()
  }
}
