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

import org.dyndns.ipignoli.petronius.controller.HistoryLoad;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordDeletion;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordRetrieve;
import org.dyndns.ipignoli.petronius.db.HistoryFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.history.HistoryRecord;
import org.dyndns.ipignoli.petronius.ui.HistoryCursorAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class HistoryList extends ListActivity{

  public static final int     ACTIVITY_TYPE_SINGLE_GARMENT = 1;

  private static final int    ACTIVITY_CREATE              = 0;
  private static final int    ACTIVITY_EDIT                = 1;

  private static final int    MENU_EDIT                    = Menu.FIRST;
  private static final int    MENU_DELETE                  = Menu.FIRST + 1;

  private long                garmentId;
  private HistoryFilter       filter;

  private SimpleCursorAdapter cursorAdapter;

  private boolean             ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    setTitle(R.string.history_record_list);

    filter = new HistoryFilter();

    setContentView(R.layout.history_list);

    garmentId = -1;
    filter.setGarmentId(-1);

    registerForContextMenu(getListView());
  }

  @Override
  protected void onResume(){
    super.onResume();
    MyContext.initialize(this);
    restoreState();
    updateData();
  }

  @Override
  protected void onPause(){
    super.onPause();
    if(!ended)
      saveState();
  }

  private void saveState(){
    CommonStore.getInstance().put(CommonStore.HISTORY_LIST_GARMENT_ID,
        garmentId);
  }

  private void restoreState(){
    if(CommonStore.getInstance().containsKey(
        CommonStore.HISTORY_LIST_GARMENT_ID)){
      garmentId =
          (Long)CommonStore.getInstance().get(
              CommonStore.HISTORY_LIST_GARMENT_ID);
      filter.setGarmentId(garmentId);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.history_list_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo){
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_EDIT, 0, R.string.edit_history_record);
    menu.add(0, MENU_DELETE, 1, R.string.delete_history_record);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.new_history_record:
        Intent intent = new Intent(this, EditHistoryRecord.class);

        if(garmentId >= 0){
          intent.putExtra(Petronius.ACTIVITY_TYPE,
              EditHistoryRecord.ACTIVITY_TYPE_CREATE_GARMENT);
          intent.putExtra(MyHelper.F_HISTORY_GARMENT_ID, garmentId);
        }
        else
          intent.putExtra(Petronius.ACTIVITY_TYPE,
              EditHistoryRecord.ACTIVITY_TYPE_CREATE);

        CommonStore.getInstance().put(CommonStore.EDIT_HISTORY_RECORD_EDITABLE,
            true);

        startActivityForResult(intent, ACTIVITY_CREATE);
        return true;

      case R.id.history_list_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.history_list_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item){
    switch(item.getItemId()){
      case MENU_EDIT:
        Intent intent = new Intent(this, EditHistoryRecord.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            EditHistoryRecord.ACTIVITY_TYPE_EDIT);
        intent.putExtra(MyHelper.F_HISTORY_ID, ((AdapterContextMenuInfo)item
            .getMenuInfo()).id);

        CommonStore.getInstance().put(CommonStore.EDIT_HISTORY_RECORD_EDITABLE,
            true);

        startActivityForResult(intent, ACTIVITY_EDIT);
        return true;

      case MENU_DELETE:
        deleteHistoryRecord(((AdapterContextMenuInfo)item.getMenuInfo()).id);
        return true;
    }

    return super.onContextItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(this, EditHistoryRecord.class);
    intent.putExtra(Petronius.ACTIVITY_TYPE,
        EditHistoryRecord.ACTIVITY_TYPE_EDIT);
    intent.putExtra(MyHelper.F_HISTORY_ID, id);

    CommonStore.getInstance().put(CommonStore.EDIT_HISTORY_RECORD_EDITABLE,
        false);

    startActivityForResult(intent, ACTIVITY_EDIT);
  }

  @Override
  public void finish(){
    CommonStore.getInstance().remove(CommonStore.HISTORY_LIST_GARMENT_ID);
    ended=true;
    super.finish();
  }

  private SimpleCursorAdapter getCursorAdapter(Cursor cursor){
    if(cursorAdapter == null)
      cursorAdapter =
          new HistoryCursorAdapter(this, R.layout.row_history_record, cursor,
              R.id.history_record_icon, R.id.history_record_date,
              R.id.history_record_garment_name);

    cursorAdapter.changeCursor(cursor);

    return cursorAdapter;
  }

  private void deleteHistoryRecord(long id){
    new HistoryRecordRetrieve(this,
        new HistoryRecordRetrieve.EndTaskListener<HistoryRecord>(){
          @Override
          public void notify(final HistoryRecord historyRecord){
            if(historyRecord == null)
              return;

            (new AlertDialog.Builder(HistoryList.this)).setIcon(
                android.R.drawable.ic_dialog_alert).setTitle(
                R.string.delete_history_record).setCancelable(false)
                .setMessage(R.string.really_delete_selected_history_record)
                .setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener(){
                      @Override
                      public void onClick(DialogInterface dialog, int which){
                        new HistoryRecordDeletion(
                            HistoryList.this,
                            new HistoryRecordDeletion.EndTaskListener<Boolean>(){
                              @Override
                              public void notify(Boolean result){
                                if(result == null)
                                  return;
                                updateData();
                              }
                            }).execute(historyRecord);
                      }
                    }).setNegativeButton(R.string.cancel, null).show();
          }
        }).execute(id);
  }

  private void updateData(){
    new HistoryLoad(this, new HistoryLoad.EndTaskListener<Cursor>(){
      @Override
      public void notify(final Cursor cursor){
        setListAdapter(getCursorAdapter(cursor));
      }
    }).execute(filter);
  }
}
