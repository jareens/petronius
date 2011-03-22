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

package org.dyndns.ipignoli.petronius.choice;

import java.util.LinkedList;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.db.ClothesFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.content.res.Resources;
import android.database.Cursor;


public class Chooser extends LinkedList<ChosenGarment>{

  private static final long serialVersionUID = 1L;

  public static final int   PARAMETERS       = 5;

  private ChosenGarment     selected;
  private ChoiceOptions     options;
  private Resources         resources;
  private MyHelper          dbHelper;

  public Chooser(int type, ChoiceOptions options, MyHelper dbHelper,
      Resources resources){
    super();

    this.options = options;
    this.resources = resources;
    this.dbHelper = dbHelper;

    ClothesFilter filter = new ClothesFilter();
    filter.setGarmentTypeFilter("" + type);
    filter.setGarmentEleganceMinFilter("" + options.getEleganceMin());
    filter.setGarmentEleganceMaxFilter("" + options.getEleganceMax());
    filter.setGarmentSeasonFilter("" + options.getSeason());
    filter.setGarmentWeatherFilter("" + options.getWeather());
    filter.setGarmentAvailableFilter(true);

    Cursor cursor = dbHelper.fetchGarmentIds(filter);
    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
      add(dbHelper.fetchGarment(cursor.getLong(0)));
  }

  public void add(Garment garment){
    super
        .add(new ChosenGarment(garment, options.getDate(), resources, dbHelper));
  }

  public ChosenGarment getSelected(){
    if(size() == 0)
      return null;
    return selected == null ? (ChosenGarment)get(0) : selected;
  }

  public boolean isSelected(){
    return selected != null;
  }

  public boolean selectPrevious(){
    int index = indexOf(getSelected());
    if(index == 0)
      return false;
    setSelected(get(index - 1));
    return true;
  }

  public boolean hasNext(){
    int index = indexOf(getSelected());
    return index + 1 < size();
  }

  public boolean selectNext(){
    int index = indexOf(getSelected());
    if(!hasNext())
      return false;
    setSelected(get(index + 1));
    return true;
  }

  public void discardSelected(){
    remove(getSelected());
    selected = null;
  }

  public void setSelected(ChosenGarment selected){
    this.selected = selected;
  }
}
