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

package org.dyndns.ipignoli.petronius.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.compatibilities.Compatibility;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.history.HistoryRecord;
import org.dyndns.ipignoli.petronius.util.MyDateFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.app.Activity;


public class MyContentHandler extends DefaultHandler{
  private Activity          activity;

  private MyHelper          helper;

  private Object            buffer;
  private List<String>      charList;
  private Map<String, Long> clothesMap;

  private boolean           documentOK;
  private boolean           clothesEnded;

  public MyContentHandler(Activity activity){
    this.activity = activity;
    helper = new MyHelper(activity);
    buffer = null;
    charList = new LinkedList<String>();
    clothesMap = new HashMap<String, Long>();
    documentOK = false;
    clothesEnded = false;
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException{
    super.startElement(uri, localName, qName, attributes);

    if(!documentOK){
      if(!"petronius".equals(localName))
        throw new SAXException(activity.getResources().getString(
            R.string.err_archive_not_valid));

      documentOK = true;
      
      return;
    }

    if("garment".equals(localName)){
      if(clothesEnded)
        throw new SAXException(activity.getResources().getString(
            R.string.err_archive_not_valid));
      buffer = new Garment(-1l, "", 0, 0, 0l, 0, 0, (byte)0, 0, true, 0);
      return;
    }

    if("history_record".equals(localName)){
      buffer = new HistoryRecord(-1l, 0l, -1l);
      clothesEnded = true;
      return;
    }

    if("compatibility".equals(localName)){
      buffer = new Compatibility(-1l, 0l, -1l, 0);
      clothesEnded = true;
      return;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException{
    super.characters(ch, start, length);
    if(length == 0)
      return;
    char[] ch2 = new char[length];
    System.arraycopy(ch, start, ch2, 0, length);
    String s = (new String(ch2)).trim();
    if(s.length() == 0)
      return;
    charList.add(s);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException{
    super.endElement(uri, localName, qName);

    if(buffer instanceof Garment){
      try{
        if("id".equals(localName))
          ((Garment)buffer).setId(Long.parseLong(getChars()));
        else if("name".equals(localName))
          ((Garment)buffer).setName(getChars());
        else if("type".equals(localName))
          ((Garment)buffer).setType(Integer.parseInt(getChars()));
        else if("grade".equals(localName))
          ((Garment)buffer).setGrade(Integer.parseInt(getChars()));
        else if("date".equals(localName))
          try{
            ((Garment)buffer).setDate(MyDateFormat.getInstance().stringToDate(
                getChars()));
          }catch(Exception e){
            ((Garment)buffer).setDate(Long.parseLong(getChars()));
          }
        else if("elegance_min".equals(localName))
          ((Garment)buffer).setElegance(Integer.parseInt(getChars()), -1);
        else if("elegance_max".equals(localName))
          ((Garment)buffer).setElegance(-1, Integer.parseInt(getChars()));
        else if("season".equals(localName))
          ((Garment)buffer).putSeason(Integer.parseInt(getChars()));
        else if("seasons".equals(localName))
          ((Garment)buffer).setSeasons(Byte.parseByte(getChars()));
        else if("weather".equals(localName))
          ((Garment)buffer).setWeather(Integer.parseInt(getChars()));
        else if("available".equals(localName))
          ((Garment)buffer).setAvailable(Integer.parseInt(getChars())==1);
        else if("image".equals(localName))
          ((Garment)buffer).setImage(Integer.parseInt(getChars()));
        else if("garment".equals(localName)){
          long id = helper.insert((Garment)buffer);
          if(id <= 0)
            throw new SAXException(R.string.err_archive_not_valid + "\n"
                + R.string.err_insert_garment + ": "
                + ((Garment)buffer).getName());
          this.clothesMap.put("" + ((Garment)buffer).getId(), id);
          buffer = null;
        }
        else
          throw new SAXException(activity.getResources().getString(
              R.string.err_archive_not_valid)
              + "\n"
              + activity.getResources().getString(R.string.err_unhandled_tag)
              + ": " + localName);
      }catch(Exception e){
        throw new SAXException(activity.getResources().getString(
            R.string.err_archive_not_valid)
            + "\n"
            + activity.getResources().getString(R.string.err_on_garment)
            + " " + ((Garment)buffer).getName() + ": " + e.getMessage());
      }
    }
    else if(buffer instanceof HistoryRecord){
      try{
        if("id".equals(localName))
          ((HistoryRecord)buffer).setId(Long.parseLong(getChars()));
        else if("date".equals(localName))
          try{
            ((HistoryRecord)buffer).setDate(MyDateFormat.getInstance()
                .stringToDate(getChars()));
          }catch(Exception e){
            ((HistoryRecord)buffer).setDate(Long.parseLong(getChars()));
          }
        else if("garment_id".equals(localName))
          ((HistoryRecord)buffer).setGarmentId(clothesMap.get(getChars()));
        else if("history_record".equals(localName)){
          long id = helper.insert((HistoryRecord)buffer);
          if(id <= 0)
            throw new SAXException(activity.getResources().getString(
                R.string.err_archive_not_valid)
                + "\n"
                + activity.getResources().getString(
                    R.string.err_insert_history_record)
                + " #"
                + ((HistoryRecord)buffer).getId());
          buffer = null;
        }
        else
          throw new SAXException(activity.getResources().getString(
              R.string.err_archive_not_valid)
              + "\n"
              + activity.getResources().getString(R.string.err_unhandled_tag)
              + ": " + localName);
      }catch(Exception e){
        e.printStackTrace();
        throw new SAXException(activity.getResources().getString(
            R.string.err_archive_not_valid)
            + "\n"
            + activity.getResources().getString(R.string.err_on_history_record)
            + " #" + ((HistoryRecord)buffer).getId() + ": " + e.getMessage());
      }
    }
    else if(buffer instanceof Compatibility){
      try{
        if("id".equals(localName))
          ((Compatibility)buffer).setId(Long.parseLong(getChars()));
        else if("garment_id_1".equals(localName))
          ((Compatibility)buffer).setGarmentId1(clothesMap.get(getChars()));
        else if("garment_id_2".equals(localName))
          ((Compatibility)buffer).setGarmentId2(clothesMap.get(getChars()));
        else if("value".equals(localName))
          ((Compatibility)buffer).setId(Integer.parseInt(getChars()));
        else if("compatibility".equals(localName)){
          long id = helper.insert((Compatibility)buffer);
          if(id <= 0)
            throw new SAXException(activity.getResources().getString(
                R.string.err_archive_not_valid)
                + "\n"
                + activity.getResources().getString(
                    R.string.err_insert_compatibility)
                + " #"
                + ((Compatibility)buffer).getId());
          buffer = null;
        }
        else
          throw new SAXException(activity.getResources().getString(
              R.string.err_archive_not_valid)
              + "\n"
              + activity.getResources().getString(R.string.err_unhandled_tag)
              + ": " + localName);
      }catch(Exception e){
        e.printStackTrace();
        throw new SAXException(activity.getResources().getString(
            R.string.err_archive_not_valid)
            + "\n"
            + activity.getResources().getString(R.string.err_on_compatibility)
            + " #" + ((Compatibility)buffer).getId() + ": " + e.getMessage());
      }
    }

    charList = new LinkedList<String>();
  }

  private String getChars(){
    StringBuffer sb = new StringBuffer();
    for(Iterator<String> it = charList.listIterator(); it.hasNext(); sb
        .append(it.next())){}
    return sb.toString();
  }
}
