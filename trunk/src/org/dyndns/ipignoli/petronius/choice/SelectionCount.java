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

package org.dyndns.ipignoli.petronius.choice;

import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.history.HistoryRecordFilter;
import android.content.res.Resources;
import android.database.Cursor;


public class SelectionCount extends HistoryParameter{

  private static final int MIN = 0, MAX = 30;

  public SelectionCount(Garment garment, GregorianCalendar date,
      Resources resources, MyHelper dbHelper){
    super(resources.getString(R.string.selection_count), garment, MIN, MAX,
        date, dbHelper);
  }

  @Override
  protected int computeValue(){
    HistoryRecordFilter filter = new HistoryRecordFilter();
    filter.setGarmentId(garment.getId());

    Cursor cursor = null;
    try{
      cursor = dbHelper.fetchHistoryRecordIds(filter);
      return Math.min(cursor.getCount(), MAX);
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  @Override
  protected double computeWeight(){
    return -2;
  }
}
