/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.model;

import com.khresterion.due.Section;

/**
 * @author khresterion
 *
 */
public class DefaultSection implements Section {

  private String id;

  private String name;

  private String link;

  /**
   * default constructor
   */
  public DefaultSection() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#getId()
   */
  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return this.id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#setId(java.lang.String)
   */
  @Override
  public void setId(String id) {
    this.id = id;

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#getLink()
   */
  @Override
  public String getLink() {
    return this.link;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#setLink(java.lang.String)
   */
  @Override
  public void setLink(String link) {
    this.link = link;

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#getName()
   */
  @Override
  public String getName() {

    return this.name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.due.Section#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int compareTo(Section another) {
    return getName().compareTo(another.getName());
  }

}
