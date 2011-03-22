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

package org.dyndns.ipignoli.petronius.clothes;

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.content.Context;


public class Types{
  private static Types instance;
  private String[]     typeList;

  public static Types getInstance(){
    if(instance == null)
      instance = new Types(MyContext.getInstance().getContext());
    return instance;
  }

  private Types(Context context){
    typeList = context.getResources().getStringArray(R.array.garment_types);
  }

  public int getTotTypes(){
    return typeList.length;
  }
  
  public int getIndex(String type){
    for(int i = 0; i < typeList.length; i++)
      if(typeList[i].equals(type))
        return i;
    return -1;
  }

  public boolean isSet(String garmentType, byte types){
    return (1<<getIndex(garmentType)&types)>0;
  }

  public int getTotSet(byte types){
    int ret=0;
    for(int i=0;i<typeList.length;i++)
      ret+=types>>i&1;
    return ret;
  }

  public byte getTypes(boolean pullJacket, boolean shirt,
      boolean skirtTrousers, boolean coat, boolean shoes){
    byte ret = 0;
    if(pullJacket)
      ret |= 1;
    if(shirt)
      ret |= 2;
    if(skirtTrousers)
      ret |= 4;
    if(coat)
      ret |= 8;
    if(shoes)
      ret |= 16;
    return ret;
  }
}
