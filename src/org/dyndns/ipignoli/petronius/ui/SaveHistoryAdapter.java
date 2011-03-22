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
import org.dyndns.ipignoli.petronius.SaveHistory;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.util.GarmentImage;
import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class SaveHistoryAdapter extends ArrayAdapter<Chooser>{

  private Activity                            activity;
  private Chooser[]                           chooser;

  private SparseBooleanArray                  checked;
  private SaveHistory.SaveHistoryItemListener listItemListener;

  public SaveHistoryAdapter(Activity activity, Chooser[] objects,
      SparseBooleanArray checked,
      SaveHistory.SaveHistoryItemListener listItemListener){
    super(activity, R.layout.row_save_history_record, objects);
    this.activity = activity;
    this.chooser = objects;
    this.checked = checked;
    this.listItemListener = listItemListener;
  }

  public static class ViewHolder{
    public CheckBox  choice;
    public TextView  text;
    public ImageView icon;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent){
    ViewHolder viewHolder;

    if(convertView == null){
      LayoutInflater inflater = activity.getLayoutInflater();
      convertView =
          inflater.inflate(R.layout.row_save_history_record, null, true);
      viewHolder = new ViewHolder();
      viewHolder.choice =
          (CheckBox)convertView.findViewById(R.id.chosen_garment_check);
      viewHolder.text =
          (TextView)convertView.findViewById(R.id.chosen_garment_name);
      viewHolder.icon =
          (ImageView)convertView.findViewById(R.id.chosen_garment_icon);
      convertView.setTag(viewHolder);
    }
    else
      viewHolder = (ViewHolder)convertView.getTag();

    viewHolder.choice.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        checked.put(position, !checked.get(position));
      }
    });
    viewHolder.choice.setChecked(checked.get(position));

    viewHolder.text.setText(chooser[position].getSelected().getGarment()
        .getName());
    viewHolder.text.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(final View v){
        listItemListener.OnClick(v, position);
      }
    });

    viewHolder.icon.setImageBitmap(GarmentImage.getInstance().getGarmentImage(
        chooser[position].getSelected().getGarment()));
    viewHolder.icon.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(final View v){
        listItemListener.OnClick(v, position);
      }
    });

    return convertView;
  }

  public SparseBooleanArray getCheckedItemPositions(){
    return checked;
  }
}
