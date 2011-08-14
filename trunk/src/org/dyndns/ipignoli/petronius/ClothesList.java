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

import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.controller.GarmentDeletion;
import org.dyndns.ipignoli.petronius.controller.GarmentRetrieve;
import org.dyndns.ipignoli.petronius.controller.GarmentUpdate;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.AbstractClothesList;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.GarmentImage;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;


public class ClothesList extends AbstractClothesList{

  private static final int ACTIVITY_CHOICE     = 1;
  private static final int ACTIVITY_CREATE     = 2;
  private static final int ACTIVITY_EDIT       = 3;
  private static final int ACTIVITY_PICK_IMAGE = 4;

  private static final int MENU_EDIT           = Menu.FIRST;
  private static final int MENU_DELETE         = Menu.FIRST + 1;
  private static final int MENU_AVAILABLE      = Menu.FIRST + 2;
  private static final int MENU_UNAVAILABLE    = Menu.FIRST + 3;
  private static final int MENU_PICK_IMAGE     = Menu.FIRST + 4;
  private static final int MENU_RESET_IMAGE    = Menu.FIRST + 5;

  @Override
  public void onCreate(Bundle bundle){
    super.onCreate(bundle);
    this.setTitle(R.string.clothes_list);
  }

  @Override
  protected void onPause(){
    super.onPause();
    CommonStore.getInstance().put(CommonStore.CLOTHES_LIST_FIRST_DISPLAYED,
        getListView().getFirstVisiblePosition());
    CommonStore.getInstance().put(CommonStore.CLOTHES_LIST_LIST_TOP,
        getListView().getTop());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.clothes_list_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo){
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_EDIT, 0, R.string.edit_garment);
    menu.add(0, MENU_DELETE, 1, R.string.delete_garment);
    menu.add(0, MENU_AVAILABLE, 2, R.string.set_available);
    menu.add(0, MENU_UNAVAILABLE, 3, R.string.set_unavailable);
    menu.add(0, MENU_PICK_IMAGE, 4, R.string.change_image);
    menu.add(0, MENU_RESET_IMAGE, 5, R.string.reset_default_image);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.clothes_choice:
        Intent choiceIntent = new Intent(this, ClothesChooser.class);
        choiceIntent.putExtra(Petronius.ACTIVITY_TYPE, ACTIVITY_CHOICE);
        startActivityForResult(choiceIntent, ACTIVITY_CHOICE);
        return true;

      case R.id.new_garment:
        Intent newIntent = new Intent(this, EditGarment.class);
        newIntent.putExtra(Petronius.ACTIVITY_TYPE,
            EditGarment.ACTIVITY_TYPE_NEW);

        CommonStore.getInstance().put(CommonStore.EDIT_GARMENT_EDITABLE, true);

        startActivityForResult(newIntent, ACTIVITY_CREATE);
        return true;

      case R.id.clothes_list_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.clothes_list_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item){
    switch(item.getItemId()){
      case MENU_EDIT:
        Intent editIntent = new Intent(this, EditGarment.class);
        editIntent.putExtra(Petronius.ACTIVITY_TYPE,
            EditGarment.ACTIVITY_TYPE_EDIT);
        editIntent.putExtra(MyHelper.F_CLOTHES_ID,
            ((AdapterContextMenuInfo)item.getMenuInfo()).id);

        CommonStore.getInstance().put(CommonStore.EDIT_GARMENT_EDITABLE, true);

        startActivityForResult(editIntent, ACTIVITY_EDIT);
        return true;

      case MENU_DELETE:
        deleteGarment(((AdapterContextMenuInfo)item.getMenuInfo()).id);
        return true;

      case MENU_AVAILABLE:
        setGarmentAvailable(((AdapterContextMenuInfo)item.getMenuInfo()).id,
            true);
        return true;

      case MENU_UNAVAILABLE:
        setGarmentAvailable(((AdapterContextMenuInfo)item.getMenuInfo()).id,
            false);
        return true;

      case MENU_PICK_IMAGE:
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        CommonStore.getInstance().put(CommonStore.CLOTHES_LIST_GARMENT_ID,
            ((AdapterContextMenuInfo)item.getMenuInfo()).id);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, ACTIVITY_PICK_IMAGE);
        return true;

      case MENU_RESET_IMAGE:
        (new GarmentRetrieve(this,
            new GarmentRetrieve.EndTaskListener<Garment>(){
              @Override
              public void notify(Garment result){
                if(result == null)
                  return;
                result.setImage(0);
                (new GarmentUpdate(ClothesList.this,
                    new GarmentUpdate.EndTaskListener<Boolean>(){
                      @Override
                      public void notify(Boolean result){
                        updateData();
                      }
                    })).execute(result);
              }
            })).execute(((AdapterContextMenuInfo)item.getMenuInfo()).id);

        GarmentImage.getInstance().resetGarmentImage(
            ((AdapterContextMenuInfo)item.getMenuInfo()).id);

        return true;
    }

    return super.onContextItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(this, EditGarment.class);
    intent.putExtra(Petronius.ACTIVITY_TYPE, EditGarment.ACTIVITY_TYPE_EDIT);
    intent.putExtra(MyHelper.F_CLOTHES_ID, id);

    CommonStore.getInstance().put(CommonStore.EDIT_GARMENT_EDITABLE, false);

    startActivityForResult(intent, ACTIVITY_EDIT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    super.onActivityResult(requestCode, resultCode, intent);

    switch(requestCode){
      case ACTIVITY_PICK_IMAGE:
        try{
          if(resultCode != RESULT_OK)
            return;

          Cursor cursor = managedQuery(intent.getData(), new String[]{
            MediaStore.Images.Media.DATA
          }, null, null, null);
          try{
            cursor.moveToFirst();
            GarmentImage.getInstance().saveGarmentImage(
                cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA)),
                (Long)CommonStore.getInstance().get(
                    CommonStore.CLOTHES_LIST_GARMENT_ID));
          }finally{
            if(cursor != null)
              cursor.close();
          }

          (new GarmentRetrieve(this,
              new GarmentRetrieve.EndTaskListener<Garment>(){
                @Override
                public void notify(Garment result){
                  if(result == null)
                    return;
                  result.setImage(1);
                  (new GarmentUpdate(ClothesList.this,
                      new GarmentUpdate.EndTaskListener<Boolean>(){
                        @Override
                        public void notify(Boolean result){
                          if(result == null)
                            return;
                          updateData();
                        }
                      })).execute(result);
                }
              })).execute((Long)CommonStore.getInstance().get(
              CommonStore.CLOTHES_LIST_GARMENT_ID));

        }catch(Exception e){
          e.printStackTrace();
        }finally{
          CommonStore.getInstance().remove(CommonStore.CLOTHES_LIST_GARMENT_ID);
        }

        return;
    }
  }

  private void deleteGarment(long id){
    new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
      @Override
      public void notify(final Garment garment){
        if(garment == null)
          return;

        (new AlertDialog.Builder(ClothesList.this)).setIcon(
            android.R.drawable.ic_dialog_info)
            .setTitle(R.string.delete_garment).setMessage(
                ClothesList.this.getResources().getString(
                    R.string.really_delete_garment)
                    + " " + garment.getName() + "?").setCancelable(false)
            .setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener(){
                  @Override
                  public void onClick(DialogInterface dialog, int which){
                    new GarmentDeletion(ClothesList.this,
                        new GarmentDeletion.EndTaskListener<Boolean>(){
                          @Override
                          public void notify(Boolean result){
                            if(result == null)
                              return;
                            updateData();
                          }
                        }).execute(garment);
                  }
                }).setNegativeButton(R.string.cancel, null).show();
      }
    }).execute(id);
  }

  private void setGarmentAvailable(long id, final boolean available){
    new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
      @Override
      public void notify(final Garment garment){
        if(garment == null || garment.isAvailable() == available)
          return;

        garment.setAvailable(available);

        new GarmentUpdate(ClothesList.this,
            new GarmentUpdate.EndTaskListener<Boolean>(){
              @Override
              public void notify(Boolean result){
                updateData();
              }
            }).execute(garment);
      }
    }).execute(id);
  }

  @Override
  protected void updateData(){
    super.updateData();

    if(CommonStore.getInstance().containsKey(
        CommonStore.CLOTHES_LIST_FIRST_DISPLAYED)){
      getListView().setSelectionFromTop(
          (Integer)CommonStore.getInstance().get(
              CommonStore.CLOTHES_LIST_FIRST_DISPLAYED),
          (Integer)CommonStore.getInstance().get(
              CommonStore.CLOTHES_LIST_LIST_TOP));
      CommonStore.getInstance()
          .remove(CommonStore.CLOTHES_LIST_FIRST_DISPLAYED);
      CommonStore.getInstance().remove(CommonStore.CLOTHES_LIST_LIST_TOP);
    }
  }
}
