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

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.util.MyContext;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;


public class ChoiceOptions{

  public static String      F_DATE          = "date";
  public static String      F_ELEGANCE_MIN  = "elegance_min";
  public static String      F_ELEGANCE_MAX  = "elegance_max";
  public static String      F_SEASON        = "season";
  public static String      F_WEATHER       = "weather";
  public static String      F_GARMENT_TYPES = "garment_types";

  private GregorianCalendar date;
  private int               eleganceMin;
  private int               eleganceMax;
  private int               season;
  private int               weather;
  private byte              garmentTypes;

  public ChoiceOptions(GregorianCalendar date, int eleganceMin,
      int eleganceMax, int season, int weather, byte garmentTypes){
    setDate(date);
    setEleganceMin(eleganceMin);
    setEleganceMax(eleganceMax);
    setSeason(season);
    setWeather(weather);
    setGarmentTypes(garmentTypes);
  }

  public void check() throws ChoiceOptionsException{
    if(getEleganceMin() > getEleganceMax())
      throw new ChoiceOptionsException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_elegance));
    if(getGarmentTypes() == 0)
      throw new ChoiceOptionsException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_types));
  }

  public GregorianCalendar getDate(){
    return date;
  }

  public void setDate(Long date){
    setDate(MyDateFormat.getInstance().createGC(date));
  }

  public void setDate(GregorianCalendar date){
    date.set(Calendar.HOUR, 0);
    date.set(Calendar.MINUTE, 0);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    this.date = date;
  }

  public int getEleganceMin(){
    return eleganceMin;
  }

  public void setEleganceMin(int eleganceMin){
    this.eleganceMin = eleganceMin;
  }

  public int getEleganceMax(){
    return eleganceMax;
  }

  public void setEleganceMax(int eleganceMax){
    this.eleganceMax = eleganceMax;
  }

  public int getSeason(){
    return season;
  }

  public void setSeason(int season){
    this.season = season;
  }

  public int getWeather(){
    return weather;
  }

  public void setWeather(int weather){
    this.weather = weather;
  }

  public byte getGarmentTypes(){
    return garmentTypes;
  }

  public void setGarmentTypes(byte garmentTypes){
    this.garmentTypes = garmentTypes;
  }
}
