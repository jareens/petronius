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

package org.dyndns.ipignoli.petronius.history;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.util.MyContext;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;
import org.dyndns.ipignoli.petronius.xml.MyXMLWriter;
import org.dyndns.ipignoli.petronius.xml.XMLElement;


public class HistoryRecord implements XMLElement{
  private long _id;
  private GregorianCalendar date;
  private long  garmentId;

  public HistoryRecord(long _id, long date, long garmentId){
    this(_id, MyDateFormat.getInstance().createGC(date), garmentId);
  }

  public HistoryRecord(long _id, GregorianCalendar date, long garmentId){
    setId(_id);
    setDate(date);
    setGarmentId(garmentId);
  }

  public void check() throws HistoryRecordException{
    if(getGarmentId()<=0)
      throw new HistoryRecordException(MyContext.getInstance().getContext()
          .getResources().getString(R.string.err_garment_not_picked));
  }

  public GregorianCalendar getDate(){
    return date;
  }

  public void setDate(long date){
    setDate(MyDateFormat.getInstance().createGC(date));
  }

  public void setDate(GregorianCalendar date){
    date.set(Calendar.HOUR,0);
    date.set(Calendar.MINUTE,0);
    date.set(Calendar.SECOND,0);
    date.set(Calendar.MILLISECOND,0);
    this.date = date;
  }

  public long getGarmentId(){
    return garmentId;
  }

  public void setGarmentId(long garmentId){
    this.garmentId = garmentId;
  }

  public long getId(){
    return _id;
  }

  public void setId(long _id){
    this._id = _id;
  }

  @Override
  public String[] toXML(int indent){
    String[] ret = new String[5];

    int index = 0;
    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("<history_record>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent+2).append("<id>").append(getId()).append(
            "</id>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent+2).append("<date>").append(
            getDate().getTimeInMillis()).append("</date>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent+2).append("<garment_id>").append(
            getGarmentId()).append("</garment_id>").toString();
    ret[index++] =
        MyXMLWriter.getIndentor(indent).append("</history_record>").toString();

    return ret;
  }
}
