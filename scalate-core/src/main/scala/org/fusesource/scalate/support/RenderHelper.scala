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

/*
  * Copyright (c) 2009 Matthew Hildebrand <matt.hildebrand@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package org.fusesource.scalate
package support

import collection.mutable.LinkedHashMap
import xml.{Node, NodeBuffer, NodeSeq}

object RenderHelper {

  /**
   * Pads the text following newlines with the specified
   * indent amount so that the text is indented.
   */
  def indent( amount:String, text: Any ): String = text.toString.replaceAll("\n(.)", "\n"+amount+"$1" )

  def indentAmount( level:Int, kind:String ): String = {
    val rc = new StringBuilder
    var i=0;
    while(i < level) {
      rc.append(kind)
      i+=1;
    }
    rc.toString
  }

  /**
   * Converts newlines into the XML entity: &#x000A;
   * so that multiple lines encode to a sinle long HTML source line
   * but still render in browser as multiple lines.
   */
  def preserve( text: Any ): String = text.toString.replaceAll("\n", "&#x000A;");

  /**
   *  Escapes any XML special characters.
   */
  def sanitize( text: String ): String =
    text.foldLeft( new StringBuffer )( (acc, ch) => sanitize( ch, acc ) ).toString


  private def sanitize( ch: Char, buffer: StringBuffer ): StringBuffer = {
    buffer.append( ch match {
      case '"' => { "&quot;" }
      case '&' => { "&amp;" }
      case '<' => { "&lt;" }
      case '>' => { "&gt;" }
// Not sure if there are other chars the need sanitization.. but if we do find
// some, then the following might work:
//    case xxx   => { "&#x" + ch.toInt.toHexString + ";" }
      case _ => ch
    })
  }


  def attributes(context:RenderContext, entries: List[(Any,Any)]) {

    val (entries_class, tmp) = entries.partition{x=>{ x._1 match { case "class" => true; case _=> false} } }
    val (entries_id, entries_rest) = tmp.partition{x=>{ x._1 match { case "id" => true; case _=> false} } }
    var map = LinkedHashMap[Any,Any]( )

    def isEnabled(value:Any) = value!=null && (!value.isInstanceOf[Boolean] || value.asInstanceOf[Boolean])

    val flattener = (x:(Any,Any))=>{
      if( isEnabled(x._2) ) {
        List(x._2)
      } else {
        Nil
      }
    }

    val ids = entries_id.flatMap(flattener)
    if( !ids.isEmpty ) {
      map += "id" -> ids.last
    }

    val classes = entries_class.flatMap(flattener)
    if( !classes.isEmpty ) {
      map += "class"->classes.mkString(" ")
    }

    entries_rest.foreach{ me => map += me._1 -> me._2 }

    if( !map.isEmpty ) {
      map.foreach {
        case (name,value) =>
        if( isEnabled(value) ) {
          context << " "
          context << name
          context << "=\""
          if( value.isInstanceOf[Boolean] ) {
            context << name
          } else {
            context.escape(value)
          }
          context << "\""
        }
      }
    }

  }

  def smart_sanitize(context: RenderContext, value: Any): String = {
    context.value(value).toString
/*
    if (value == null) {
      return context.value(value);
    }
    value match {
      case x: Node =>
        context.value(value)

      case x: Traversable[Any] =>
        x.map( smart_sanitize(context, _) ).mkString("")

      case _ =>
        sanitize(context.value(value, false))
    }
*/
  }
}
