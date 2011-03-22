/*
 * Copyright (c) 2011 Andrea De Rinaldis <ozioso at ipignoli.dyndns.org>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dyndns.ipignoli.petronius.compatibilities;

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.util.MyContext;
import org.dyndns.ipignoli.petronius.xml.MyXMLWriter;
import org.dyndns.ipignoli.petronius.xml.XMLElement;


public class Compatibility implements XMLElement{
  private long _id;
  private long garmentId1, garmentId2;
  private int  level;

  public Compatibility(long _id, long garmentId1, long garmentId2, int level){
    setId(_id);
    setGarmentId1(garmentId1);
    setGarmentId2(garmentId2);
    setLevel(level);
  }

  public void check() throws CompatibilityException{
    if(getGarmentId1() <= 0 || getGarmentId2() <= 0)
      throw new CompatibilityException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_two_garments_not_picked));
    if(getGarmentId1() == getGarmentId2())
      throw new CompatibilityException(MyContext.getInstance().getContext()
          .getResources()
          .getString(R.string.err_must_choose_different_garments));
  }

  public long getGarmentId1(){
    return garmentId1;
  }

  public void setGarmentId1(long garmentId1){
    this.garmentId1 = garmentId1;
  }

  public long getGarmentId2(){
    return garmentId2;
  }

  public void setGarmentId2(long garmentId2){
    this.garmentId2 = garmentId2;
  }

  public int getLevel(){
    return level;
  }

  public void setLevel(int level){
    this.level = level;
  }

  public long getId(){
    return _id;
  }

  public void setId(long _id){
    this._id = _id;
  }

  public boolean matches(long garmentId){
    return getGarmentId1() == garmentId || getGarmentId2() == garmentId;
  }

  @Override
  public String[] toXML(int indent){
    String[] ret = new String[6];

    int index = 0;
    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("<compatibility>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<id>").append(getId())
            .append("</id>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<garment_id_1>").append(
            getGarmentId1()).append("</garment_id_1>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<garment_id_2>").append(
            getGarmentId2()).append("</garment_id_2>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<value>")
            .append(getLevel()).append("</value>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("</compatibility>").toString();

    return ret;
  }
}
