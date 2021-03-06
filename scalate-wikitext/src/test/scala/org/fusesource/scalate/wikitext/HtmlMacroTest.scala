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

package org.fusesource.scalate.wikitext

class HtmlMacroTest extends AbstractConfluenceTest {
  test("html macro test") {
    assertFilter("""{html}<p>hello <b>world!</b></p>{html}""",
      """<p>hello <b>world!</b></p>""")
  }

  test("div macro test") {
    assertFilter("""{div}hello world{div}""",
      """<div><p>hello world</p></div>""")
  }

  test("div with attributes macro test") {
    assertFilter("""{div:style=margin-left:-20px; text-align:center; padding-right:20px;}hello world{div}""",
      """<div style="margin-left:-20px; text-align:center; padding-right:20px;"><p>hello world</p></div>""")
  }

  test("section and column") {
    assertFilter("""{section}{column}foo{column}{column}bar{column}{section}""",
      """<table class="sectionMacro" border="0" cellpadding="5px" cellspacing="0" width="100%"><tr><td class="confluenceTd" valign="top"><p>foo</p></td><td class="confluenceTd" valign="top"><p>bar</p></td></tr></table>""")
  }

  test("section and column with line breaks") {
    assertFilter("""
{section}
{column}
foo{column}
{column}
bar
{column}
{section}
""",
      """<table class="sectionMacro" border="0" cellpadding="5px" cellspacing="0" width="100%"><tr><td class="confluenceTd" valign="top"><p>foo</p></td><td class="confluenceTd" valign="top"><p>bar</p></td></tr></table>""")
  }

  test("link with image and width") {
    assertFilter("""[!karaf-box.png|width=256!|Download]""",
      """<p><a href="Download"><img width="256" border="0" src="karaf-box.png"/></a></p>""")
  }


  test("multiple links") {
    assertFilter("""[Foo|foo]/[Bar|bar]""",
      """<p><a href="foo">Foo</a>/<a href="bar">Bar</a></p>""")
  }


}