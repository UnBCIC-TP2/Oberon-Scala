package br.unb.cic.oberon.parser

import org.antlr.v4.runtime._
import br.unb.cic.oberon.util.Resources
import br.unb.cic.oberon.ast.OberonModule

/** Loads/Parses a module along with the whole tree of imported modules */
class ModuleLoader {

    var main: Option[String] = None
    var modules = Map.empty[String, OberonModule]
    var readFiles = Set.empty[String]

    // TODO: handle case where `file` does not exist or we can't get the content
    def load(file: String): Boolean = {
        if (readFiles.contains(file)) {
            return false
        }

        // Read and parse `file`
        readFiles += file
        val input = getContent(file)
        val module = ScalaParser.parse(input)

        add(module)

        // Set the main module if needed
        if (main.isEmpty)
            main = Some(module.name)

        // Load submodules
        module.submodules.map(submodule => {
            val path = findModulePath(file, submodule)
            load(path)
        })
        true
    }

    def add(module: OberonModule) = {
        modules += module.name -> module
    }

    protected def getContent(file: String): String = {
        // read file
        ???
    }

    private def findModulePath(basePath: String, moduleId: String) = {
        // "pop" basePath and insert "moduleId.oberon"
        ???
    }
}

/** Same thing as ModuleLoader, but loading resource paths instead of file paths */
class ResourceModuleLoader extends ModuleLoader {
    protected override def getContent(resource: String) =
        Resources.getContent(resource)
}

/** Static methods for ModuleLoader */
object ModuleLoader {
    def load(file: String) = {
        val loader = new ModuleLoader
        loader.load(file)
        loader
    }
}

/** Static methods for ResourceModuleLoader */
object ResourceModuleLoader {
    def load(file: String) = {
        val loader = new ResourceModuleLoader
        loader.load(file)
        loader
    }
}