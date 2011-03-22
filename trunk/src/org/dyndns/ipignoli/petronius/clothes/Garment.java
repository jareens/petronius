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

package org.dyndns.ipignoli.petronius.clothes;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.util.MyContext;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;
import org.dyndns.ipignoli.petronius.xml.MyXMLWriter;
import org.dyndns.ipignoli.petronius.xml.XMLElement;


public class Garment implements XMLElement{
  private long              _id;
  private String            name;
  private int               type;
  private int               grade;
  private GregorianCalendar date;
  private int               eleganceMin;
  private int               eleganceMax;
  private byte              seasons;
  private int               weather;
  private boolean           available;
  private long              image;

  public Garment(long _id, String name, int type, int grade, long date,
      int eleganceMin, int eleganceMax, byte seasons, int weather,
      boolean available, long image){
    this(_id, name, type, grade, MyDateFormat.getInstance().createGC(date),
        eleganceMin, eleganceMax, seasons, weather, available, image);
  }

  public Garment(long _id, String name, int type, int grade,
      GregorianCalendar date, int eleganceMin, int eleganceMax, byte seasons,
      int weather, boolean available, long image){
    setId(_id);
    setName(name);
    setType(type);
    setGrade(grade);
    setDate(date);
    setElegance(eleganceMin, eleganceMax);
    setSeasons(seasons);
    setWeather(weather);
    setAvailable(available);
    setImage(image);
  }

  public void check() throws GarmentException{
    if(getEleganceMin() > getEleganceMax())
      throw new GarmentException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_elegance));
    if(getSeasons() == 0)
      throw new GarmentException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_seasons));
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public int getType(){
    return type;
  }

  public void setType(int type){
    this.type = type;
  }

  public int getGrade(){
    return grade;
  }

  public void setGrade(int grade){
    this.grade = grade;
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

  public void setElegance(int eleganceMin, int eleganceMax){
    if(eleganceMin <= 0)
      eleganceMin = this.eleganceMin;
    if(eleganceMax <= 0)
      eleganceMax = this.eleganceMax;
    this.eleganceMin = eleganceMin;
    this.eleganceMax = eleganceMax;
  }

  public int getEleganceMin(){
    return eleganceMin;
  }

  public int getEleganceMax(){
    return eleganceMax;
  }

  public byte getSeasons(){
    return seasons;
  }

  public void putSeason(int season){
    this.seasons |= 1 << season;
  }

  public void setSeasons(byte seasons){
    this.seasons = seasons;
  }

  public int getWeather(){
    return weather;
  }

  public void setWeather(int weather){
    this.weather = weather;
  }

  public boolean isAvailable(){
    return available;
  }

  public void setAvailable(boolean available){
    this.available = available;
  }

  public long getImage(){
    return image;
  }

  public void setImage(long image){
    this.image = image;
  }

  public long getId(){
    return _id;
  }

  public void setId(long _id){
    this._id = _id;
  }

  @Override
  public String[] toXML(int indent){
    String[] ret = new String[13];

    int index = 0;
    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("<garment>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<id>").append(getId())
            .append("</id>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<name>").append(
            MyXMLWriter.protectChars(getName())).append("</name>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<type>").append(getType())
            .append("</type>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<grade>")
            .append(getGrade()).append("</grade>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<date>").append(
            getDate().getTimeInMillis()).append("</date>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<elegance_min>").append(
            getEleganceMin()).append("</elegance_min>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<elegance_max>").append(
            getEleganceMax()).append("</elegance_max>").toString();

    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<seasons>").append(
            getSeasons()).append("</seasons>").toString();

    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<weather>").append(
            getWeather()).append("</weather>").toString();

    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<available>").append(
            isAvailable() ? 1 : 0).append("</available>").toString();

    ret[index++] =
        MyXMLWriter.getIndentor(indent + 2).append("<image>")
            .append(getImage()).append("</image>").toString();

    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("</garment>").toString();

    return ret;
  }
}
