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

import java.io.File;
import android.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class FileListAdapter extends ArrayAdapter<File>{
  private final Activity context;
  private final File[]   files;

  public FileListAdapter(Activity context, File[] files){
    super(context, R.layout.simple_list_item_1, files);
    this.context = context;
    this.files = files;
  }

  public static class ViewHolder{
    public TextView text;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    ViewHolder viewHolder;

    if(convertView == null){
      LayoutInflater inflater = context.getLayoutInflater();
      convertView = inflater.inflate(R.layout.simple_list_item_1, null, true);
      viewHolder = new ViewHolder();
      viewHolder.text = (TextView)convertView.findViewById(R.id.text1);
      convertView.setTag(viewHolder);
    }
    else
      viewHolder = (ViewHolder)convertView.getTag();

    viewHolder.text.setText(files[position].getName().contains(".")
        ? files[position].getName().substring(0,
            files[position].getName().lastIndexOf(".")) : files[position]
            .getName());

    return convertView;
  }

}
