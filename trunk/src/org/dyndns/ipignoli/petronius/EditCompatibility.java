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
import org.dyndns.ipignoli.petronius.compatibilities.Compatibility;
import org.dyndns.ipignoli.petronius.compatibilities.CompatibilityException;
import org.dyndns.ipignoli.petronius.controller.CompatibilityDeletion;
import org.dyndns.ipignoli.petronius.controller.CompatibilityInsertion;
import org.dyndns.ipignoli.petronius.controller.CompatibilityRetrieve;
import org.dyndns.ipignoli.petronius.controller.CompatibilityUpdate;
import org.dyndns.ipignoli.petronius.controller.GarmentRetrieve;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


public class EditCompatibility extends Activity{

  public static final int  ACTIVITY_TYPE_EDIT          = 1;
  public static final int  ACTIVITY_TYPE_CREATE        = 2;
  public static final int  ACTIVITY_TYPE_CREATE_CHOSEN = 3;

  public static final int  RESULT_CANCEL               = 2;
  public static final int  RESULT_DELETE               = 3;
  public static final int  RESULT_INSERT_KO            = 4;

  private static final int ACTIVITY_PICK_1             = 0;
  private static final int ACTIVITY_PICK_2             = 1;

  private Menu             menu;
  private boolean          editable;

  private Button           buttonGarment1, buttonGarment2;
  private Spinner          editLevel;
  private Button           buttonOK, buttonCancel;

  private long             garmentId;
  private Compatibility    compatibility;
  @SuppressWarnings("unchecked")
  private Class            listPickerClass;

  private boolean          ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = true;

    setTitle(R.string.compatibility_edit);

    setContentView(R.layout.compatibility_edit);

    buttonGarment1 = (Button)findViewById(R.id.edit_compatibility_garment1);
    buttonGarment1.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        startGarmentPicker(ACTIVITY_PICK_1, compatibility.getGarmentId2());
      }
    });

    buttonGarment2 = (Button)findViewById(R.id.edit_compatibility_garment2);
    buttonGarment2.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        startGarmentPicker(ACTIVITY_PICK_2, compatibility.getGarmentId1());
      }
    });

    editLevel = (Spinner)findViewById(R.id.edit_compatibility_level);

    buttonOK = (Button)findViewById(R.id.edit_compatibility_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        saveCompatibility();
      }
    });

    buttonCancel = (Button)findViewById(R.id.edit_compatibility_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        endMe(RESULT_CANCEL);
      }
    });

    editable = false;

    Bundle extras = getIntent().getExtras();
    garmentId = extras.getLong(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1);

    switch(extras.getInt(Petronius.ACTIVITY_TYPE)){
      case (ACTIVITY_TYPE_EDIT):
        listPickerClass = ClothesListPicker.class;
        new CompatibilityRetrieve(this,
            new CompatibilityRetrieve.EndTaskListener<Compatibility>(){
              public void notify(final Compatibility compatibility){
                EditCompatibility.this.compatibility = compatibility;
                updateUI();
              }
            }).execute((Long)extras.get(MyHelper.F_COMPATIBILITIES_ID));
        break;

      case (ACTIVITY_TYPE_CREATE):
        listPickerClass = ClothesListPicker.class;
        compatibility = new Compatibility(-1, garmentId, -1, 0);
        editable = true;
        break;

      case (ACTIVITY_TYPE_CREATE_CHOSEN):
        listPickerClass = ChosenClothesListPicker.class;
        compatibility = new Compatibility(-1, garmentId, -1, 0);
        editable = true;
        break;
    }
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
    updateUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    this.menu = menu;
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_compatibility_menu, menu);
    if(editable)
      menu.removeItem(R.id.edit_compatibility);
    if(compatibility.getId() <= 0)
      menu.removeItem(R.id.delete_compatibility);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.edit_compatibility:
        enableSelectors(true);
        menu.removeItem(R.id.edit_compatibility);
        return true;

      case R.id.delete_compatibility:
        deleteCompatibility();
        return true;

      case R.id.edit_compatibility_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.edit_compatibility_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode){
      case ACTIVITY_PICK_1:
        if(resultCode != RESULT_OK)
          return;
        compatibility.setGarmentId1(data.getLongExtra(MyHelper.F_CLOTHES_ID,
            -1l));
        compatibility.setLevel(editLevel.getSelectedItemPosition());
        return;

      case ACTIVITY_PICK_2:
        if(resultCode != RESULT_OK)
          return;
        compatibility.setGarmentId2(data.getLongExtra(MyHelper.F_CLOTHES_ID,
            -1l));
        compatibility.setLevel(editLevel.getSelectedItemPosition());
        return;
    }
  }

  @Override
  public void finish(){
    ended = true;
    super.finish();
  }

  private void saveState(){
    compatibility.setLevel(editLevel.getSelectedItemPosition());

    CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_COMPATIBILITY,
        compatibility);
    CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_EDITABLE,
        editable);
  }

  private void restoreState(){
    if(CommonStore.getInstance().containsKey(
        CommonStore.EDIT_COMPATIBILITY_COMPATIBILITY))
      compatibility =
          (Compatibility)CommonStore.getInstance().get(
              CommonStore.EDIT_COMPATIBILITY_COMPATIBILITY);

    editable =
        (Boolean)CommonStore.getInstance().get(
            CommonStore.EDIT_COMPATIBILITY_EDITABLE);
  }

  private void saveCompatibility(){
    if(!editable){
      endMe(RESULT_OK);
      return;
    }

    saveState();

    try{
      compatibility.check();
    }catch(CompatibilityException e){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(
          R.string.err_on_compatibility).setMessage(e.getMessage())
          .setCancelable(false).setPositiveButton(R.string.ok, null).show();
      return;
    }

    if(compatibility.getId() >= 0){
      new CompatibilityUpdate(this,
          new CompatibilityUpdate.EndTaskListener<Boolean>(){
            @Override
            public void notify(Boolean result){
              if(result)
                endMe(RESULT_OK);
            }
          }).execute(compatibility);

      return;
    }

    new CompatibilityInsertion(this,
        new CompatibilityInsertion.EndTaskListener<Long>(){
          @Override
          public void notify(Long result){
            if(result == null || result <= 0)
              return;

            compatibility.setId(result);
            endMe(RESULT_OK);
          }
        }).execute(compatibility);
  }

  private void endMe(int result){
    CommonStore.getInstance().remove(
        CommonStore.EDIT_COMPATIBILITY_COMPATIBILITY);
    CommonStore.getInstance().remove(CommonStore.EDIT_COMPATIBILITY_EDITABLE);

    setResult(result);
    finish();
  }

  private void deleteCompatibility(){
    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.delete_compatibility).setMessage(
            R.string.really_delete_selected_compatibility).setCancelable(false)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
          @Override
          public void onClick(DialogInterface dialog, int which){
            new CompatibilityDeletion(EditCompatibility.this,
                new CompatibilityDeletion.EndTaskListener<Boolean>(){
                  public void notify(Boolean result){
                    if(result)
                      endMe(RESULT_DELETE);
                  }
                }).execute(compatibility);
          }
        }).setNegativeButton(R.string.cancel, null).show();
  }

  private void startGarmentPicker(final int activityPick, long id){
    if(id <= 0){
      startActivityForResult(
          new Intent(EditCompatibility.this, listPickerClass), activityPick);
      return;
    }

    new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
      @Override
      public void notify(Garment garment){
        if(garment == null)
          return;
        Intent intent = new Intent(EditCompatibility.this, listPickerClass);
        intent.putExtra(MyHelper.F_CLOTHES_TYPE, garment.getType());
        intent.putExtra(MyHelper.F_CLOTHES_ID, garmentId);
        startActivityForResult(intent, activityPick);
      }
    }).execute(id);
  }

  private void updateUI(){
    if(compatibility == null)
      return;

    if(compatibility.getGarmentId1() > 0)
      new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
        @Override
        public void notify(Garment garment){
          if(garment == null)
            return;
          buttonGarment1.setText(garment.getName());
        }
      }).execute(compatibility.getGarmentId1());
    else
      buttonGarment1.setText(getResources().getString(R.string.pick_garment));
    if(compatibility.getGarmentId2() > 0)
      new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
        @Override
        public void notify(Garment garment){
          if(garment == null)
            return;
          buttonGarment2.setText(garment.getName());
        }
      }).execute(compatibility.getGarmentId2());
    else
      buttonGarment2.setText(getResources().getString(R.string.pick_garment));
    editLevel.setSelection(compatibility.getLevel());
    enableSelectors(editable);
  }

  private void enableSelectors(boolean editable){
    this.editable = editable;
    buttonGarment1.setEnabled(garmentId == compatibility.getGarmentId1()
        ? false : editable);
    buttonGarment2.setEnabled(garmentId == compatibility.getGarmentId2()
        ? false : editable);
    editLevel.setEnabled(editable);
    buttonCancel.setEnabled(editable);
  }
}
