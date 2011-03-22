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

package org.dyndns.ipignoli.petronius.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.xml.MyXMLReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.SQLException;


public class ArchiveLoad extends MyAsyncTask<File, Integer, Boolean>{

  private MyHelper dbHelper;

  public ArchiveLoad(Activity activity, EndTaskListener<Boolean> callback){
    super(activity.getResources().getString(R.string.archive_load), activity,
        callback);

    dbHelper = new MyHelper(activity);
  }

  @Override
  protected Boolean doTheWork(File... loadFile) throws Exception{
    boolean saved = false;
    try{
      dbHelper.saveDatabase();
      saved = true;
    }catch(SQLException e){}

    try{
      dbHelper.clearDatabase();
    }catch(SQLException e){
      if(saved)
        try{
          dbHelper.restoreDatabase();
          clearError(e, getActivity().getResources().getString(
              R.string.archive_restored));
          throw e;
        }catch(SQLException e1){
          clearError(e, getActivity().getResources().getString(
              R.string.archive_not_restored));
          throw e1;
        }
      clearError(e, getActivity().getResources().getString(
          R.string.archive_not_restored));
      throw e;
    }

    try{
      MyXMLReader.read(getActivity(), new InputStreamReader(
          new FileInputStream(loadFile[0])));
    }catch(Exception e){
      if(saved)
        try{
          dbHelper.restoreDatabase();
          clearError(e, getActivity().getResources().getString(
              R.string.archive_restored));
          throw e;
        }catch(SQLException e1){
          clearError(e, getActivity().getResources().getString(
              R.string.archive_not_restored));
          throw e1;
        }
      clearError(e, getActivity().getResources().getString(
          R.string.archive_not_restored));
      throw e;
    }

    return true;
  }

  private void clearError(Exception e, String solution){
    (new AlertDialog.Builder(getActivity())).setIcon(
        android.R.drawable.ic_dialog_alert).setTitle(
        getActivity().getResources().getString(R.string.err_on) + " "
            + getName()).setMessage(
        getActivity().getResources().getString(
            R.string.following_error_occurred)
            + ": " + e.getLocalizedMessage() + "\n" + solution).setCancelable(
        false).setPositiveButton(R.string.ok, null).show();
  }
}
