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

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.controller.ArchiveSave;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SaveArchive extends Activity{

  public static final int     RESULT_CANCEL  = 1;
  public static final int     RESULT_SAVE_KO = 2;

  private static final String FILE_EXTENSION = "xml";

  private EditText            editFileName;
  private Button              buttonOK, buttonCancel;

  private String              sdCardPath;
  private File                saveFile;

  private boolean             ended;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    ended = false;

    setTitle(R.string.save_archive);

    setContentView(R.layout.save_archive);

    editFileName = (EditText)findViewById(R.id.edit_save_file_name);

    GregorianCalendar d = new GregorianCalendar();
    String fName =
        getResources().getString(R.string.archive_prefix) + "_"
            + d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-"
            + d.get(Calendar.DAY_OF_MONTH);
    sdCardPath =
        Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + Petronius.DIR_ARCHIVE;

    saveFile = new File(sdCardPath, fName + "." + FILE_EXTENSION);
    int prog = 1;
    while(saveFile.exists())
      saveFile =
          new File(sdCardPath, fName + "_" + (prog++) + "." + FILE_EXTENSION);

    editFileName.setText(saveFile.getName().substring(0,
        saveFile.getName().length() - 4));

    buttonOK = (Button)findViewById(R.id.save_archive_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        saveArchive();
      }
    });

    buttonCancel = (Button)findViewById(R.id.save_archive_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        endMe(RESULT_CANCEL);
      }
    });
  }

  @Override
  protected void onPause(){
    super.onPause();
    if(!ended)
      saveState();
  }

  @Override
  protected void onResume(){
    super.onResume();
    restoreState();
    MyContext.initialize(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.save_archive_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.save_archive_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE,
            R.raw.save_archive_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public void finish(){
    ended = true;
    clearState();
    super.finish();
  }

  private void saveState(){
    CommonStore.getInstance().put(CommonStore.SAVE_ARCHIVE_SAVE_FILE,
        editFileName.getText().toString());
  }

  private void restoreState(){
    if(CommonStore.getInstance()
        .containsKey(CommonStore.SAVE_ARCHIVE_SAVE_FILE))
      saveFile =
          new File(sdCardPath, (String)CommonStore.getInstance().get(
              CommonStore.SAVE_ARCHIVE_SAVE_FILE)
              + "." + FILE_EXTENSION);

    editFileName.setText(saveFile.getName().substring(0,
        saveFile.getName().length() - 4));
  }

  private void clearState(){
    CommonStore.getInstance().remove(CommonStore.SAVE_ARCHIVE_SAVE_FILE);
  }

  private void saveArchive(){
    if(editFileName.getText().toString().contains(File.separator)){
      (new AlertDialog.Builder(SaveArchive.this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(R.string.save_archive)
          .setMessage(R.string.invalid_filename).setCancelable(false)
          .setPositiveButton(R.string.ok, null).show();
      return;
    }

    saveFile =
        new File(saveFile.getParent(), editFileName.getText() + "."
            + FILE_EXTENSION);

    if(saveFile.exists()){
      (new AlertDialog.Builder(SaveArchive.this)).setIcon(
          android.R.drawable.ic_dialog_info).setTitle(R.string.save_archive)
          .setMessage(R.string.file_already_exists).setCancelable(false)
          .setPositiveButton(R.string.ok, new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
              reallySaveArchive();
            }
          }).setNegativeButton(R.string.cancel, null).show();
      return;
    }

    reallySaveArchive();
  }

  void reallySaveArchive(){
    new ArchiveSave(this, new ArchiveSave.EndTaskListener<Boolean>(){
      @Override
      public void notify(Boolean result){
        if(result == null || !result)
          endMe(RESULT_SAVE_KO);

        (new AlertDialog.Builder(SaveArchive.this)).setIcon(
            android.R.drawable.ic_dialog_info).setTitle(R.string.save_archive)
            .setMessage(R.string.archive_save_ok).setCancelable(false)
            .setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener(){
                  @Override
                  public void onClick(DialogInterface dialog, int id){
                    endMe(RESULT_OK);
                  }
                }).show();
      }
    }).execute(saveFile);

    return;
  }

  protected void endMe(int result){
    setResult(result);
    finish();
  }
}
