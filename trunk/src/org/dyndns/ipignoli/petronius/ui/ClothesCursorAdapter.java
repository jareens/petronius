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

import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.util.GarmentImage;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class ClothesCursorAdapter extends SimpleCursorAdapter{

  public ClothesCursorAdapter(Context context, int layout, Cursor c,
      int viewIcon, int viewName){
    super(context, layout, c, new String[]{
        MyHelper.F_CLOTHES_IMAGE, MyHelper.F_CLOTHES_NAME
    }, new int[]{
        viewIcon, viewName
    });

    setViewBinder(new ViewBinder(){
      public boolean setViewValue(View view, Cursor cursor, int columnIndex){
        if(columnIndex == cursor.getColumnIndex(MyHelper.F_CLOTHES_IMAGE)){
          ((ImageView)view)
              .setImageBitmap(GarmentImage.getInstance()
                  .getGarmentImage(
                      cursor.getInt(columnIndex),
                      cursor.getLong(cursor
                          .getColumnIndex(MyHelper.F_CLOTHES_ID)),
                      cursor.getInt(cursor
                          .getColumnIndex(MyHelper.F_CLOTHES_TYPE))));

          return true;
        }

        if(columnIndex == cursor.getColumnIndex(MyHelper.F_CLOTHES_NAME)){
          ((TextView)view).setText(cursor.getString(columnIndex));

          if(cursor.getInt(cursor.getColumnIndex(MyHelper.F_CLOTHES_AVAILABLE)) == 0)
            ((TextView)view).setEnabled(false);
          else
            ((TextView)view).setEnabled(true);

          return true;
        }

        return false;
      }
    });
  }
}
