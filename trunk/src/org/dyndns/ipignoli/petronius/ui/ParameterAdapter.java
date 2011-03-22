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
import org.dyndns.ipignoli.petronius.choice.Parameter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ParameterAdapter extends ArrayAdapter<Parameter>{

  private Activity    activity;
  private Parameter[] parameters;

  public ParameterAdapter(Activity activity, Parameter[] objects){
    super(activity, R.layout.row_parameter, objects);
    this.activity = activity;
    this.parameters = objects;
  }

  public static class ViewHolder{
    public TextView  name;
    public TextView value;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent){
    ViewHolder viewHolder;

    if(convertView == null){
      LayoutInflater inflater = activity.getLayoutInflater();
      convertView = inflater.inflate(R.layout.row_parameter, null, true);
      viewHolder = new ViewHolder();
      viewHolder.name =
          (TextView)convertView.findViewById(R.id.parameter_name);
      viewHolder.value =
          (TextView)convertView.findViewById(R.id.parameter_value);
      convertView.setTag(viewHolder);
    }
    else
      viewHolder = (ViewHolder)convertView.getTag();

    viewHolder.name.setText(parameters[position].getName());
    viewHolder.value.setText(""+parameters[position].getWeightedValue());

    return convertView;
  }
}
