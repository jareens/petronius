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
import org.dyndns.ipignoli.petronius.db.DataFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.app.Activity;
import android.database.Cursor;

public class CompatibilitiesLoad extends MyAsyncTask<DataFilter, Integer, Cursor>{

  private MyHelper dbHelper;

  public CompatibilitiesLoad(Activity activity,EndTaskListener<Cursor> callback){
    super(activity.getResources().getString(R.string.compatibilities_loading),activity,callback);
    this.dbHelper = new MyHelper(activity);
  }

  @Override
  protected Cursor doTheWork(DataFilter... filter){
    return dbHelper.fetchCompatibilities(filter[0]);
  }
}
