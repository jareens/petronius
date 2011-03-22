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

package org.dyndns.ipignoli.petronius.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.content.Context;


public class MyDateFormat{
  private static MyDateFormat  instance;

  private java.text.DateFormat dateFormat;

  public static MyDateFormat getInstance(){
    if(instance == null)
      instance = new MyDateFormat(MyContext.getInstance().getContext());
    return instance;
  }

  private MyDateFormat(Context context){
    dateFormat = android.text.format.DateFormat.getDateFormat(context);
  }

  public GregorianCalendar createGC(Long date){
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTimeInMillis(date);
    return gc;
  }

  public String format(GregorianCalendar date){
    return dateFormat.format(date.getTime());
  }

  public GregorianCalendar stringToDate(String date){
    GregorianCalendar calendar = new GregorianCalendar();
    int firstSlash = date.indexOf('/'), lastSlash = date.lastIndexOf('/');
    calendar
        .set(Calendar.YEAR, Integer.parseInt(date.substring(0, firstSlash)));
    calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(
        firstSlash + 1, lastSlash)) - 1);
    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date
        .substring(lastSlash + 1)));
    return calendar;
  }

}
