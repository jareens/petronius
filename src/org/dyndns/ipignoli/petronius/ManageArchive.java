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
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import org.dyndns.ipignoli.petronius.controller.ArchiveLoad;
import org.dyndns.ipignoli.petronius.ui.FileListAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class ManageArchive extends ListActivity{

  private static final int ACTIVITY_SAVE = 1;

  private static final int MENU_DELETE   = Menu.FIRST;

  private File             sdCard;
  private FilenameFilter   fileFilter;
  private File[]           files;

  private boolean          ended;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    ended = false;

    setTitle(R.string.archive);

    setContentView(R.layout.archive_list);

    sdCard =
        new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + Petronius.DIR_ARCHIVE);
    fileFilter = new FilenameFilter(){
      @Override
      public boolean accept(File dir, String filename){
        return filename.endsWith(".xml");
      }
    };

    registerForContextMenu(getListView());
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
    MyContext.initialize(this);
    restoreState();
    updateData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.manage_archive_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo){
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_DELETE, 0, R.string.delete_archive);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.save_archive:
        Intent intent = new Intent(this, SaveArchive.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE, ACTIVITY_SAVE);
        startActivityForResult(intent, ACTIVITY_SAVE);
        return true;
 
      case R.id.manage_archive_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.manage_archive_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    loadArchive(files[position]);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item){
    switch(item.getItemId()){
      case MENU_DELETE:
        deleteFile(((AdapterContextMenuInfo)item.getMenuInfo()).position);
        return true;
    }

    return super.onContextItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    super.onActivityResult(requestCode, resultCode, intent);

    if(resultCode == RESULT_OK)
      updateData();
  }

  private void saveState(){
    CommonStore.getInstance().put(CommonStore.MANAGE_ARCHIVE_SD_CARD, sdCard);
    CommonStore.getInstance().put(CommonStore.MANAGE_ARCHIVE_FILE_FILTER,
        fileFilter);
  }

  private void restoreState(){
    if(CommonStore.getInstance()
        .containsKey(CommonStore.MANAGE_ARCHIVE_SD_CARD))
      sdCard =
          (File)CommonStore.getInstance().get(
              CommonStore.MANAGE_ARCHIVE_SD_CARD);

    if(CommonStore.getInstance().containsKey(
        CommonStore.MANAGE_ARCHIVE_FILE_FILTER))
      fileFilter =
          (FilenameFilter)CommonStore.getInstance().get(
              CommonStore.MANAGE_ARCHIVE_FILE_FILTER);
  }

  private void deleteFile(final int index){
    if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
            .getExternalStorageState())){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setMessage(
          R.string.sdcard_not_readable).setCancelable(false).setPositiveButton(
          R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
          }).show();
      return;
    }

    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.delete_archive).setMessage(
            getResources().getString(R.string.really_delete_archive) + " "
                + ((File)getListAdapter().getItem(index)).getName() + "?")
        .setCancelable(false).setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){
                if(((File)getListAdapter().getItem(index)).delete())
                  updateData();
              }
            }).setNegativeButton(getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){}
            }).show();

  }

  private void loadArchive(final File loadFile){
    if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
            .getExternalStorageState())){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setMessage(
          R.string.sdcard_not_readable).setCancelable(false).setPositiveButton(
          R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
          }).show();
      return;
    }

    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.load_archive).setMessage(
            getResources().getString(R.string.archive_load_chosen) + " "
                + loadFile.getName() + ".\n"
                + getResources().getString(R.string.confirm_archive_load))
        .setCancelable(false).setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){
                new ArchiveLoad(ManageArchive.this,
                    new ArchiveLoad.EndTaskListener<Boolean>(){
                      @Override
                      public void notify(Boolean result){
                        if(result)
                          (new AlertDialog.Builder(ManageArchive.this))
                              .setIcon(android.R.drawable.ic_dialog_info)
                              .setMessage(R.string.archive_load_ok)
                              .setCancelable(false).setPositiveButton(
                                  R.string.ok,
                                  new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,
                                        int id){}
                                  }).show();
                      }
                    }).execute(loadFile);
              }
            }).setNegativeButton(getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){}
            }).show();

    return;
  }

  private void updateData(){
    if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
            .getExternalStorageState())){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setMessage(
          R.string.sdcard_not_readable).setCancelable(false).setPositiveButton(
          R.string.ok, null).show();
      return;
    }

    files = sdCard.listFiles(fileFilter);
    
    if(files == null)
      return;
    
    Arrays.sort(files, new Comparator<File>(){
      @Override
      public int compare(File object1, File object2){
        if(object1 == null && object2 == null)
          return 0;
        if(object1 == null)
          return 1;
        if(object2 == null)
          return -1;
        if(object1.lastModified() > object2.lastModified())
          return -1;
        if(object1.lastModified() < object2.lastModified())
          return 1;
        return 0;
      }
    });
    
    setListAdapter(new FileListAdapter(this, files));
  }
}
