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


public class Seasons{
  private static Seasons instance;
  private String[]       seasonList;

  public static Seasons getInstance(){
    if(instance==null) instance =
        new Seasons(MyContext.getInstance().getContext());
    return instance;
  }

  private Seasons(Context context){
    seasonList = context.getResources().getStringArray(R.array.season_list);
  }

  public int getIndex(String season){
    for(int i = 0; i<seasonList.length; i++)
      if(seasonList[i].equals(season)) return i;
    return -1;
  }

  public boolean isSet(String season, byte seasons){
    return (1<<getIndex(season)&seasons)>0;
  }

  public byte getSeasons(boolean autumn, boolean winter, boolean spring,
      boolean summer){
    byte ret = 0;
    if(autumn) ret |= 1;
    if(winter) ret |= 2;
    if(spring) ret |= 4;
    if(summer) ret |= 8;
    return ret;
  }
}
