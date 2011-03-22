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

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.clothes.Types;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.content.res.Resources;


public class ChosenGarment implements Comparable<ChosenGarment>{

  private Garment                garment;
  private Parameter[]            parameters = new Parameter[Chooser.PARAMETERS];
  private CompatibilityParameter compatibility;

  public ChosenGarment(Garment garment, GregorianCalendar date,
      Resources resources, MyHelper dbHelper){
    this.garment = garment;
    parameters[0] = new Preference(garment, resources);
    parameters[1] = new PurchaseDate(garment, resources);
    parameters[2] = new LastSelection(garment, date, resources, dbHelper);
    parameters[3] = new SelectionCount(garment, date, resources, dbHelper);
    parameters[4] = new RandomParameter(garment, resources);
    compatibility = new CompatibilityParameter(garment, resources, dbHelper);
  }

  public Garment getGarment(){
    return garment;
  }

  public int getTotal(){
    int ret = 0;
    for(int i = 0; i < parameters.length; i++)
      ret += parameters[i].getWeightedValue();
    ret += compatibility.getWeightedValue();
    return ret;
  }

  public Parameter getParameter(int index){
    return parameters[index];
  }

  public Parameter[] getParameters(){
    return parameters;
  }

  public CompatibilityParameter getCompatibility(){
    return compatibility;
  }

  public void updateCompatibility(Chooser[] chosen, List<Score> scores){
    Garment[] clothes = new Garment[Types.getInstance().getTotTypes()];
    for(Iterator<Score> itScores = scores.iterator(); itScores.hasNext();){
      int type = itScores.next().getType();
      if(type == getGarment().getType())
        break;
      clothes[type] = chosen[type].getSelected().getGarment();
    }
    compatibility.update(clothes);
  }

  @Override
  public int compareTo(ChosenGarment another){
    if(another == null)
      return 1;

    if(getTotal() > another.getTotal())
      return -1;

    if(getTotal() < another.getTotal())
      return 1;

    return 0;
  }
}
