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

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class CompatibilityCursorAdapter extends SimpleCursorAdapter{
  private MyHelper helper;

  public CompatibilityCursorAdapter(final Context context, int layout,
      Cursor c, int viewLevel, int viewGarment1, int viewGarment2){
    super(context, layout, c, new String[]{
        MyHelper.F_COMPATIBILITIES_LEVEL,
        MyHelper.F_COMPATIBILITIES_GARMENT_ID_1,
        MyHelper.F_COMPATIBILITIES_GARMENT_ID_2
    }, new int[]{
        viewLevel, viewGarment1, viewGarment2
    });

    helper = new MyHelper(context);

    setViewBinder(new ViewBinder(){
      public boolean setViewValue(View view, Cursor cursor, int columnIndex){
        if(columnIndex == cursor
            .getColumnIndex(MyHelper.F_COMPATIBILITIES_LEVEL)){
          switch(cursor.getInt(columnIndex)){
            case 0:
              ((ImageView)view)
                  .setImageResource(R.drawable.ic_list_compatibility_dont_fit);
              return true;
            case 1:
              ((ImageView)view)
                  .setImageResource(R.drawable.ic_list_compatibility_better_not);
              return true;
            case 2:
              ((ImageView)view)
                  .setImageResource(R.drawable.ic_list_compatibility_ok);
              return true;
            case 3:
              ((ImageView)view)
                  .setImageResource(R.drawable.ic_list_compatibility_very_good);
              return true;
          }
          return true;
        }

        if(columnIndex == cursor
            .getColumnIndex(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1)
            || columnIndex == cursor
                .getColumnIndex(MyHelper.F_COMPATIBILITIES_GARMENT_ID_2)){
          ((TextView)view).setText(helper.fetchGarment(
              cursor.getLong(columnIndex)).getName());
          return true;
        }

        return false;
      }
    });
  }
}
