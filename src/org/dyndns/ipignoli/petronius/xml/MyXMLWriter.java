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

import java.io.PrintStream;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.ProgressUpdater;
import android.app.Activity;
import android.database.Cursor;


public class MyXMLWriter{
  public static StringBuffer getIndentor(int indent){
    StringBuffer ret = new StringBuffer();
    for(int i = 0; i < indent; i++)
      ret.append(" ");
    return ret;
  }

  public static String protectChars(String s){
    return new StringBuffer("<![CDATA[").append(s).append("]]>").toString();
//    return s.replace("&", "&amp;").replace(">", "&gt;")
//        .replace("<", "&lt;").replace("\"", "&quot;").replace("'", "&apos;");
  }

  private static final String HEADER       =
                                               "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>";
  private static final String ARCHIVE_NAME = "petronius";

  private Activity            activity;
  private MyHelper            helper;

  private int                 totObjects;
  private int                 objectsSaved;

  public MyXMLWriter(Activity activity){
    this.activity = activity;
    helper = new MyHelper(activity);
    totObjects = -1;
    objectsSaved = 0;
  }

  public void write(PrintStream os, ProgressUpdater<Integer> progressUpdater){
    os.println(HEADER);
    os.print("<");
    os.print(ARCHIVE_NAME);
    os.println(">");

    int indent = 4;

    try{
      Cursor cursor = helper.fetchGarmentIds();
      try{
        os.println("  <clothes>");
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
          for(String l: helper.fetchGarment(cursor.getLong(0)).toXML(indent))
            os.println(l);
          if(progressUpdater != null)
            progressUpdater.updateProgress(incrementTotObjectsSaved());
        }
        os.println("  </clothes>");

        cursor = helper.fetchHistoryRecordIds();
        activity.startManagingCursor(cursor);
        os.println("  <history>");
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
          for(String l: helper.fetchHistoryRecord(cursor.getLong(0)).toXML(
              indent))
            os.println(l);
          if(progressUpdater != null)
            progressUpdater.updateProgress(incrementTotObjectsSaved());
        }
        os.println("  </history>");

        cursor = helper.fetchCompatibilityIds();
        activity.startManagingCursor(cursor);
        os.println("  <compatibilities>");
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
          for(String l: helper.fetchCompatibility(cursor.getLong(0)).toXML(
              indent))
            os.println(l);
          if(progressUpdater != null)
            progressUpdater.updateProgress(incrementTotObjectsSaved());
        }
        os.println("  </compatibilities>");
      }finally{
        cursor.close();
      }
    }finally{
      if(helper != null)
        helper.close();
    }

    os.print("</");
    os.print(ARCHIVE_NAME);
    os.println(">");
  }

  public int getTotObjects(){
    if(totObjects >= 0)
      return totObjects;

    int totObjects = helper.getTotGarments();
    totObjects += helper.getTotHistoryRecords();
    totObjects += helper.getTotCompatibilities();

    return totObjects;
  }

  public synchronized int incrementTotObjectsSaved(){
    return ++objectsSaved;
  }

  public synchronized int getTotObjectsSaved(){
    return objectsSaved;
  }
}
