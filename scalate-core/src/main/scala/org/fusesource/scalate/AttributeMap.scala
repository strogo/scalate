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

package org.fusesource.scalate

import scala.collection.Set

/**
 * Represents a small map like thing which is easy to implement on top of any attribute storage mechanism without
 * having to implement a full Map interface. Note that these methods should use the same arguments and return types
 * as their corresponding methods in the mutable Map
 *
 * @version $Revision : 1.1 $
 */

trait AttributeMap[A, B] {
  
  /**
   * Retries an optional entry for the given attribute
   */
  def get(key: A): Option[B]

  /**
   * Retrieves the value of the given attribute.
   * 
   * @return the attribute or <code>null</code> in the case where there
   *          is no attribute set using the specified key. 
   */
  def apply(key: A): B

  /**
   * Updates the value of the given attribute
   */
  def update(key: A, value: B): Unit

  /**
   * Removes an attribute
   */
  def remove(key: A): Option[B]

  /**
   * Collects all the available keys
   */
  def keySet: Set[A]
}
