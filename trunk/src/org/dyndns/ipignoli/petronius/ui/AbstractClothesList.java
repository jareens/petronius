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
import org.dyndns.ipignoli.petronius.controller.ClothesLoad;
import org.dyndns.ipignoli.petronius.db.ClothesFilter;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;


public abstract class AbstractClothesList extends ListActivity{

  protected Spinner             editFilterType;
  protected EditText            editFilterName;

  protected SimpleCursorAdapter cursorAdapter;

  protected ClothesFilter       filter;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    filter = new ClothesFilter();

    setContentView(R.layout.clothes_list);

    CharSequence[] arrayTypes =
        getResources().getTextArray(R.array.garment_types);
    CharSequence[] arrayFilterType = new CharSequence[arrayTypes.length + 1];
    arrayFilterType[0] = getResources().getText(R.string.all);
    for(int i = 0; i < arrayTypes.length; i++)
      arrayFilterType[i + 1] = arrayTypes[i];
    ArrayAdapter<CharSequence> adapterType =
        new ArrayAdapter<CharSequence>(this,
            android.R.layout.simple_spinner_item, arrayFilterType);
    editFilterType = (Spinner)findViewById(R.id.edit_garment_filter_type);
    editFilterType.setAdapter(adapterType);
    editFilterType.setOnItemSelectedListener(new OnItemSelectedListener(){
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos,
          long id){
        filter.setGarmentTypeFilter(pos > 0 ? "" + (pos - 1) : null);
        updateData();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent){}
    });

    editFilterName = (EditText)findViewById(R.id.edit_garment_filter_name);
    editFilterName.addTextChangedListener(new TextWatcher(){
      @Override
      public void afterTextChanged(Editable s){
        filter.setGarmentNameFilter(s.length() > 0 ? s.toString() : null);
        updateData();
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after){}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count){}
    });

    registerForContextMenu(getListView());
  }

  @Override
  protected void onResume(){
    super.onResume();
    MyContext.initialize(this);
    updateData();
  }

  private SimpleCursorAdapter getCursorAdapter(Cursor cursor){
    if(cursorAdapter == null)
      cursorAdapter =
          new ClothesCursorAdapter(this, R.layout.row_garment, cursor,
              R.id.garment_icon, R.id.garment_name);
    cursorAdapter.changeCursor(cursor);
    return cursorAdapter;
  }

  protected void updateData(){
    (new ClothesLoad(this, new ClothesLoad.EndTaskListener<Cursor>(){
      @Override
      public void notify(final Cursor cursor){
        setListAdapter(getCursorAdapter(cursor));
      }
    })).execute(filter);
  }
}
