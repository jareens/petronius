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

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.choice.ChosenGarment;
import org.dyndns.ipignoli.petronius.db.HistoryFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.history.HistoryRecord;
import org.dyndns.ipignoli.petronius.history.HistoryRecordException;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;


public class HistorySave extends MyAsyncTask<ChosenGarment, Integer, Boolean>{

  private long     historyDate;

  private MyHelper dbHelper;

  public HistorySave(long historyDate, Activity activity,
      EndTaskListener<Boolean> callback){
    super(activity.getResources().getString(R.string.history_save), activity,
        callback);
    this.historyDate = historyDate;
    this.dbHelper = new MyHelper(activity);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
  }

  @Override
  protected Boolean doTheWork(ChosenGarment... chosen) throws Exception{
    progressDialog.setMax(chosen.length);

    Boolean ret = true;

    for(int i = 0; i < chosen.length; i++){
      HistoryFilter filter = new HistoryFilter();
      filter.setDate(MyDateFormat.getInstance().createGC(historyDate));
      filter.setGarmentId(chosen[i].getGarment().getId());
      Cursor cursor = dbHelper.fetchHistoryRecordIds(filter);
      try{
      if(cursor.getCount() == 0)
        try{
          dbHelper.insert(new HistoryRecord(-1, historyDate, chosen[i]
              .getGarment().getId()));
        }catch(HistoryRecordException e){
          ret = false;
          throw e;
        }
      }finally{
        cursor.close();
      }
      updateProgress(i + 1);
    }

    return ret;
  }
}
