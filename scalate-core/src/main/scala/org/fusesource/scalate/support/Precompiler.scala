/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.support

import org.fusesource.scalate.util.FileResourceLoader
import java.util.ArrayList
import java.io.File
import scala.collection.mutable.ListBuffer
import org.fusesource.scalate.{Binding, TemplateEngine}
import org.fusesource.scalate.servlet.ServletRenderContext

/**
 * This class can precompile Scalate templates into JVM
 * classes.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class Precompiler {

  var warSourceDirectory: File = _
  var resourcesSourceDirectory: File = _
  var workingDirectory: File = _
  var classesDirectory:File = _
  var templates:ArrayList[String] = new ArrayList[String]()
  var info:{def apply(v1:String):Unit} = (value:String)=>println(value)
  var contextClass:String = _
  var bootClassName:String = _

  def execute() = {
    if(warSourceDirectory==null) {
      throw new IllegalArgumentException("warSourceDirectory must be set")
    }

    var engine = new TemplateEngine() {
      // lets output generated bytecode to the classes directory.
      override def bytecodeDirectory = {
        if( classesDirectory!=null ) {
          classesDirectory
        } else {
          super.bytecodeDirectory
        }
      }
    }

    engine.classLoader = Thread.currentThread.getContextClassLoader
    engine.resourceLoader = new FileResourceLoader(Some(warSourceDirectory));

    if( contextClass!=null ) {
      engine.bindings = List(Binding("context", contextClass, true, isImplicit = true))
    } else {
      engine.bindings = List(Binding("context", "_root_."+classOf[ServletRenderContext].getName, true, isImplicit = true))
    }

    if( workingDirectory!=null ) {
      engine.workingDirectory = workingDirectory
      workingDirectory.mkdirs();
    }
    if( bootClassName!=null ) {
      engine.bootClassName = bootClassName
    }
    engine.boot

    val sourceDirs = ListBuffer(warSourceDirectory)
    if( resourcesSourceDirectory!=null ) {
      sourceDirs += resourcesSourceDirectory
    }

    var paths = List[String]()
    for (extension <- engine.codeGenerators.keysIterator; sd <- sourceDirs if sd.exists) {
      paths = collectUrisWithExtension(sd, "", "." + extension) ::: paths;
    }

    import collection.JavaConversions._
    templates.foreach { x=>
      paths ::= x
    }

    info("Precompiling Scalate Templates into Scala classes...");
    for (uri <- paths) {

      // TODO it would be easier to just generate Source + URI pairs maybe rather than searching again for the source file???
      val file = sourceDirs.map(new File(_, uri)).find(_.exists).getOrElse(uri)
      info("    processing " + file)
      val template = engine.load(uri)
    }
  }

  protected def collectUrisWithExtension(basedir: File, baseuri: String, extension: String): List[String] = {
    var collected = List[String]()
    if (basedir.isDirectory()) {
      var files = basedir.listFiles();
      if (files != null) {
        for (file <- files) {
          if (file.isDirectory()) {
            collected = collectUrisWithExtension(file, baseuri + "/" + file.getName(), extension) ::: collected;
          } else {
            if (file.getName().endsWith(extension)) {
              collected = baseuri + "/" + file.getName() :: collected;
            } else {
            }

          }
        }
      }
    }
    collected
  }

}