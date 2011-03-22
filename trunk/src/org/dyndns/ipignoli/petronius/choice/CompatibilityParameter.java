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

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.compatibilities.Compatibility;
import org.dyndns.ipignoli.petronius.db.CompatibilitiesFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.content.res.Resources;
import android.database.Cursor;


public class CompatibilityParameter extends Parameter{

  private static final int MIN           = 0;
  private static final int MAX           = 3;
  private static final int MEDIUM        = 2;

  private MyHelper         dbHelper;
  private int              compatibility = MEDIUM;

  public CompatibilityParameter(Garment garment, Resources resources,
      MyHelper dbHelper){
    super(resources.getString(R.string.compatibility), garment, MIN, MAX);
    this.dbHelper = dbHelper;
  }

  @Override
  protected int computeValue(){
    return MAX - compatibility;
  }

  @Override
  protected double computeWeight(){
    return -3 * Tables.LOGARITHMIC[value];
  }

  public void update(Garment[] chosen){
    int count = 0;
    compatibility = 0;
    Compatibility record;
    for(int i = 0; i < chosen.length; i++){
      if(chosen[i] == null)
        continue;
      
      CompatibilitiesFilter filter = new CompatibilitiesFilter();
      filter.setGarmentId(garment.getId());
      Cursor cursor = dbHelper.fetchCompatibilityIds(filter);
      for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
        record=dbHelper.fetchCompatibility(cursor.getLong(0));
        if(record.matches(chosen[i].getId())){
          compatibility += record.getLevel();
          count++;
          break;
        }
      }
    }
    
    compatibility = (count == 0 ? MEDIUM : compatibility / count);
    value = (int)(computeValue() * (min + 100d / (max - min)));
    weight = computeWeight();
  }
}
