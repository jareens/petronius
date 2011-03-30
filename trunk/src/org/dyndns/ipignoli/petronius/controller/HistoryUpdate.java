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

package org.dyndns.ipignoli.petronius.controller;

import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.app.Activity;


public class HistoryUpdate extends
    MyAsyncTask<GregorianCalendar, Integer, Boolean>{

  private MyHelper dbHelper;

  public HistoryUpdate(Activity activity, EndTaskListener<Boolean> callback){
    super(activity.getResources().getString(R.string.history_update), activity,
        callback);
    this.dbHelper = new MyHelper(activity);
  }

  @Override
  protected Boolean doTheWork(GregorianCalendar... date){
    date[0].set(GregorianCalendar.HOUR, 0);
    date[0].set(GregorianCalendar.MINUTE, 0);
    date[0].set(GregorianCalendar.SECOND, 0);
    date[0].set(GregorianCalendar.MILLISECOND, 0);
    date[0].setTimeInMillis(date[0].getTimeInMillis() - 30l * 86400l * 1000l);
    return dbHelper.updateHistory(date[0].getTimeInMillis());
  }
}
