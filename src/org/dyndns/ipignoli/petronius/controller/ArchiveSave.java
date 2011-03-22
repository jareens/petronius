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
import java.io.PrintStream;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.xml.MyXMLWriter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;


public class ArchiveSave extends MyAsyncTask<File, Integer, Boolean>{
  private MyXMLWriter writer;

  public ArchiveSave(Activity activity, EndTaskListener<Boolean> callback){
    super(activity.getResources().getString(R.string.archive_save), activity,
        callback);

    writer = new MyXMLWriter(activity);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setMax(writer.getTotObjects());
  }

  @Override
  protected Boolean doTheWork(File... saveFile) throws Exception{
    if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
      throw new SaveArchiveException(getActivity().getResources().getString(
          R.string.sdcard_not_writable));

    if(!saveFile[0].getParentFile().exists()
        && !saveFile[0].getParentFile().mkdirs())
      throw new SaveArchiveException(getActivity().getResources().getString(
          R.string.err_create_dir_archive));

    writer.write(new PrintStream(saveFile[0]), this);

    return true;
  }
}
