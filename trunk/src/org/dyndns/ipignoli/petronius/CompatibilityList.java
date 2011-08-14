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

import org.dyndns.ipignoli.petronius.compatibilities.Compatibility;
import org.dyndns.ipignoli.petronius.controller.CompatibilitiesLoad;
import org.dyndns.ipignoli.petronius.controller.CompatibilityDeletion;
import org.dyndns.ipignoli.petronius.controller.CompatibilityRetrieve;
import org.dyndns.ipignoli.petronius.db.CompatibilitiesFilter;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.CompatibilityCursorAdapter;
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


public class CompatibilityList extends ListActivity{

  public static final int       ACTIVITY_TYPE_SINGLE_GARMENT = 1;

  private static final int      ACTIVITY_CREATE              = 0;
  private static final int      ACTIVITY_EDIT                = 1;

  private static final int      MENU_EDIT                    = Menu.FIRST;
  private static final int      MENU_DELETE                  = Menu.FIRST + 1;

  private long                  garmentId;
  private CompatibilitiesFilter filter;

  private SimpleCursorAdapter   cursorAdapter;

  private boolean               ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    this.setTitle(R.string.compatibility_list);

    filter = new CompatibilitiesFilter();

    setContentView(R.layout.compatibilities_list);

    registerForContextMenu(getListView());
  }

  @Override
  protected void onPause(){
    super.onResume();
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
    inflater.inflate(R.menu.compatibility_list_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo){
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_EDIT, 0, R.string.edit_compatibility);
    menu.add(0, MENU_DELETE, 1, R.string.delete_compatibility);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.new_compatibility:
        Intent intent = new Intent(this, EditCompatibility.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            EditCompatibility.ACTIVITY_TYPE_CREATE);
        intent.putExtra(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1, garmentId);

        CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_EDITABLE,
            true);

        startActivityForResult(intent, ACTIVITY_CREATE);
        return true;

      case R.id.compatibility_list_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE,
            R.raw.compatibility_list_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item){
    switch(item.getItemId()){
      case MENU_EDIT:
        Intent intent = new Intent(this, EditCompatibility.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            EditCompatibility.ACTIVITY_TYPE_EDIT);
        intent.putExtra(MyHelper.F_COMPATIBILITIES_ID,
            ((AdapterContextMenuInfo)item.getMenuInfo()).id);
        intent.putExtra(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1, garmentId);

        CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_EDITABLE,
            true);

        startActivityForResult(intent, ACTIVITY_EDIT);
        return true;

      case MENU_DELETE:
        deleteCompatibility(((AdapterContextMenuInfo)item.getMenuInfo()).id);
        return true;
    }
    return super.onContextItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(this, EditCompatibility.class);
    intent.putExtra(Petronius.ACTIVITY_TYPE,
        EditCompatibility.ACTIVITY_TYPE_EDIT);
    intent.putExtra(MyHelper.F_COMPATIBILITIES_ID, id);
    intent.putExtra(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1, garmentId);

    CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_EDITABLE,
        false);

    startActivityForResult(intent, ACTIVITY_EDIT);
  }

  @Override
  public void finish(){
    ended = true;
    clearState();
    super.finish();
  }

  private void saveState(){
    CommonStore.getInstance().put(CommonStore.COMPATIBILITY_LIST_GARMENT_ID_1,
        garmentId);
  }

  private void restoreState(){
    garmentId =
        (Long)CommonStore.getInstance().get(
            CommonStore.COMPATIBILITY_LIST_GARMENT_ID_1);
    filter.setGarmentId(garmentId);
  }

  private void clearState(){
    CommonStore.getInstance().remove(
        CommonStore.COMPATIBILITY_LIST_GARMENT_ID_1);

  }

  private SimpleCursorAdapter getCursorAdapter(Cursor cursor){
    if(cursorAdapter == null)
      cursorAdapter =
          new CompatibilityCursorAdapter(this, R.layout.row_compatibility,
              cursor, R.id.compatibility_icon,
              R.id.compatibility_garment_name_1,
              R.id.compatibility_garment_name_2);
    cursorAdapter.changeCursor(cursor);
    return cursorAdapter;
  }

  private void deleteCompatibility(long id){
    new CompatibilityRetrieve(this,
        new CompatibilityRetrieve.EndTaskListener<Compatibility>(){
          @Override
          public void notify(final Compatibility compatibility){
            if(compatibility == null)
              return;

            (new AlertDialog.Builder(CompatibilityList.this)).setIcon(
                android.R.drawable.ic_dialog_info).setTitle(
                R.string.delete_compatibility).setMessage(
                R.string.really_delete_selected_compatibility).setCancelable(
                false).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener(){
                  @Override
                  public void onClick(DialogInterface dialog, int which){
                    new CompatibilityDeletion(CompatibilityList.this,
                        new CompatibilityDeletion.EndTaskListener<Boolean>(){
                          @Override
                          public void notify(Boolean result){
                            if(result == null)
                              return;
                            updateData();
                          }
                        }).execute(compatibility);
                  }
                }).setNegativeButton(R.string.cancel, null).show();
          }
        }).execute(id);
  }

  private void updateData(){
    new CompatibilitiesLoad(this,
        new CompatibilitiesLoad.EndTaskListener<Cursor>(){
          @Override
          public void notify(final Cursor cursor){
            setListAdapter(getCursorAdapter(cursor));
          }
        }).execute(filter);
  }
}
