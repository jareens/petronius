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

package org.dyndns.ipignoli.petronius.ui;

import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.util.GarmentImage;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class HistoryCursorAdapter extends SimpleCursorAdapter{

  private MyHelper helper;

  public HistoryCursorAdapter(final Activity context, int layout, Cursor c,
      int viewIcon, int viewDate, int viewGarment){
    super(context, layout, c, new String[]{
        MyHelper.F_HISTORY_GARMENT_ID, MyHelper.F_HISTORY_DATE, MyHelper.F_HISTORY_GARMENT_ID
    }, new int[]{
        viewIcon, viewDate, viewGarment
    });

    helper = new MyHelper(context);

    setViewBinder(new ViewBinder(){
      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex){
        if(columnIndex == cursor.getColumnIndex(MyHelper.F_HISTORY_DATE)){
          ((TextView)view)
              .setText(MyDateFormat.getInstance().format(
                  MyDateFormat.getInstance().createGC(
                      cursor.getLong(columnIndex))));
          return true;
        }

        if(columnIndex == cursor.getColumnIndex(MyHelper.F_HISTORY_GARMENT_ID)){
          Garment garment = helper.fetchGarment(cursor.getLong(columnIndex));

          if(view instanceof ImageView)
            ((ImageView)view).setImageBitmap(GarmentImage.getInstance().getGarmentImage(
              garment));
          else
            ((TextView)view).setText(garment.getName());

          return true;
        }

        return false;
      }
    });
  }
}
