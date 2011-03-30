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

package org.dyndns.ipignoli.petronius;

import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.controller.HistoryUpdate;
import org.dyndns.ipignoli.petronius.controller.MyAsyncTask.EndTaskListener;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;


public class Petronius extends TabActivity{

  public static final String ACTIVITY_TYPE = "ActivityType";

  public static final String DIR_ARCHIVE   = "Petronius";
  public static final int    ICON_WIDTH    = 48;
  public static final int    ICON_HEIGHT   = 48;
  public static final String DIR_ICON      = "icons";
  
  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.main);

    Intent intent = new Intent().setClass(this, ClothesList.class);
    getTabHost().addTab(
        getTabHost().newTabSpec("ClothesList").setIndicator(
            getResources().getString(R.string.clothes_list),
            getResources().getDrawable(R.drawable.ic_tab_clothes_desc))
            .setContent(intent));

    intent = new Intent().setClass(this, HistoryList.class);
    getTabHost().addTab(
        getTabHost().newTabSpec("HistoryList").setIndicator(
            getResources().getString(R.string.history),
            getResources().getDrawable(R.drawable.ic_tab_history_desc))
            .setContent(intent));

    intent = new Intent().setClass(this, ManageArchive.class);
    getTabHost().addTab(
        getTabHost().newTabSpec("ManageArchive").setIndicator(
            getResources().getString(R.string.archive),
            getResources().getDrawable(R.drawable.ic_tab_archive_desc))
            .setContent(intent));

    getTabHost().setCurrentTab(0);
    
    new HistoryUpdate(this,new EndTaskListener<Boolean>(){
      @Override
      public void notify(Boolean result){}
      }).execute(new GregorianCalendar());
  }

  @Override
  protected void onPause(){
    super.onPause();

    CommonStore.getInstance().put(CommonStore.PETRONIUS_TAB_SELECTED,
        getTabHost().getCurrentTab());
  }

  @Override
  protected void onResume(){
    super.onResume();

    MyContext.initialize(this);

    if(CommonStore.getInstance()
        .containsKey(CommonStore.PETRONIUS_TAB_SELECTED))
      getTabHost().setCurrentTab(
          (Integer)CommonStore.getInstance().get(
              CommonStore.PETRONIUS_TAB_SELECTED));
  }
}
