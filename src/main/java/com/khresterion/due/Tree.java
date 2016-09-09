/**
 * copyright Khresterion 2016
 */
package com.khresterion.due;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author khresterion
 *
 */
@JsonSerialize
public interface Tree {

   public Section[] getData();
   
   public void addSection(Section section);
   
   public void removeSection(final int index);
}
