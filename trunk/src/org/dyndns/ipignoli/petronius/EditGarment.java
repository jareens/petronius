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

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.clothes.GarmentException;
import org.dyndns.ipignoli.petronius.clothes.Seasons;
import org.dyndns.ipignoli.petronius.controller.GarmentDeletion;
import org.dyndns.ipignoli.petronius.controller.GarmentInsertion;
import org.dyndns.ipignoli.petronius.controller.GarmentRetrieve;
import org.dyndns.ipignoli.petronius.controller.GarmentUpdate;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;


public class EditGarment extends Activity{
  public static final int ACTIVITY_TYPE_EDIT = 1;
  public static final int ACTIVITY_TYPE_NEW  = 2;

  public static final int RESULT_CANCEL      = 1;
  public static final int RESULT_DELETE      = 2;

  private Menu            menu;
  private boolean         editable;

  private EditText        editName;
  private Spinner         editType;
  private Spinner         editGrade;
  private DatePicker      editDateOfPurchase;
  private Spinner         editEleganceMin, editEleganceMax;
  private CheckBox        editAutumn, editWinter, editSpring, editSummer;
  private Spinner         editWeather;
  private CheckBox        editAvailable;
  private Button          buttonOK, buttonCancel;

  private Garment         garment;

  private boolean         ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    setTitle(R.string.garment_edit);

    setContentView(R.layout.garment_edit);

    editName = (EditText)findViewById(R.id.edit_garment_name);
    editType = (Spinner)findViewById(R.id.edit_garment_type);
    editGrade = (Spinner)findViewById(R.id.edit_garment_grade);
    editDateOfPurchase =
        (DatePicker)findViewById(R.id.edit_garment_date_of_purchase);
    editEleganceMin = (Spinner)findViewById(R.id.edit_garment_elegance_min);
    editEleganceMax = (Spinner)findViewById(R.id.edit_garment_elegance_max);
    editAutumn = (CheckBox)findViewById(R.id.edit_garment_autumn);
    editWinter = (CheckBox)findViewById(R.id.edit_garment_winter);
    editSpring = (CheckBox)findViewById(R.id.edit_garment_spring);
    editSummer = (CheckBox)findViewById(R.id.edit_garment_summer);
    editWeather = (Spinner)findViewById(R.id.edit_garment_weather);
    editAvailable = (CheckBox)findViewById(R.id.edit_garment_available);

    buttonOK = (Button)findViewById(R.id.edit_garment_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        saveGarment();
      }
    });

    buttonCancel = (Button)findViewById(R.id.edit_garment_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        endMe(RESULT_CANCEL);
      }
    });

    editable = false;

    Bundle extras = getIntent().getExtras();
    switch(extras.getInt(Petronius.ACTIVITY_TYPE)){
      case (ACTIVITY_TYPE_EDIT):
        new GarmentRetrieve(this,
            new GarmentRetrieve.EndTaskListener<Garment>(){
              public void notify(final Garment garment){
                EditGarment.this.garment = garment;
                updateUI();
              }
            }).execute((Long)extras.get(MyHelper.F_CLOTHES_ID));
        break;

      case (ACTIVITY_TYPE_NEW):
        garment =
            new Garment(-1, editName.getText().toString(), 0, 6,
                new GregorianCalendar(), 2, 2, Seasons.getInstance()
                    .getSeasons(true, true, true, true), 0, true, 0);
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
    inflater.inflate(R.menu.edit_garment_menu, menu);
    if(editable)
      menu.removeItem(R.id.edit_garment);
    if(garment.getId() <= 0){
      menu.removeItem(R.id.delete_garment);
      menu.removeItem(R.id.open_history_list);
      menu.removeItem(R.id.open_compatibility_list);
    }
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    Intent intent = null;

    switch(item.getItemId()){
      case R.id.edit_garment:
        enableSelectors(true);
        menu.removeItem(R.id.edit_garment);
        return true;

      case R.id.delete_garment:
        deleteGarment();
        return true;

      case R.id.open_history_list:
        intent = new Intent(this, HistoryList.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            HistoryList.ACTIVITY_TYPE_SINGLE_GARMENT);

        CommonStore.getInstance().put(CommonStore.HISTORY_LIST_GARMENT_ID,
            garment.getId());

        startActivity(intent);
        return true;

      case R.id.open_compatibility_list:
        intent = new Intent(this, CompatibilityList.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            CompatibilityList.ACTIVITY_TYPE_SINGLE_GARMENT);

        CommonStore.getInstance().put(
            CommonStore.COMPATIBILITY_LIST_GARMENT_ID_1, garment.getId());

        startActivity(intent);
        return true;

      case R.id.edit_garment_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE,
            R.raw.edit_garment_help);

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
    garment.setName(editName.getText().toString());
    garment.setType(editType.getSelectedItemPosition());
    garment.setGrade(editGrade.getSelectedItemPosition() + 1);
    garment.setDate(new GregorianCalendar(editDateOfPurchase.getYear(),
        editDateOfPurchase.getMonth(), editDateOfPurchase.getDayOfMonth()));
    garment.setElegance(editEleganceMin.getSelectedItemPosition() + 1,
        editEleganceMax.getSelectedItemPosition() + 1);
    garment
        .setSeasons(Seasons.getInstance().getSeasons(editAutumn.isChecked(),
            editWinter.isChecked(), editSpring.isChecked(),
            editSummer.isChecked()));
    garment.setWeather(editWeather.getSelectedItemPosition());
    garment.setAvailable(editAvailable.isChecked());

    CommonStore.getInstance().put(CommonStore.EDIT_GARMENT_GARMENT, garment);
    CommonStore.getInstance().put(CommonStore.EDIT_GARMENT_EDITABLE, editable);
  }

  private void restoreState(){
    if(CommonStore.getInstance().containsKey(CommonStore.EDIT_GARMENT_GARMENT))
      garment =
          (Garment)CommonStore.getInstance().get(
              CommonStore.EDIT_GARMENT_GARMENT);

    editable =
        (Boolean)CommonStore.getInstance().get(
            CommonStore.EDIT_GARMENT_EDITABLE);
  }

  private void clearState(){
    CommonStore.getInstance().remove(CommonStore.EDIT_GARMENT_GARMENT);
    CommonStore.getInstance().remove(CommonStore.EDIT_GARMENT_EDITABLE);
  }

  private void saveGarment(){
    if(!editable){
      endMe(RESULT_OK);
      return;
    }

    saveState();

    try{
      garment.check();
    }catch(GarmentException e){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(R.string.err_on_garment)
          .setMessage(e.getMessage()).setCancelable(false).setPositiveButton(
              R.string.ok, null).show();
      return;
    }

    if(garment.getId() >= 0){
      new GarmentUpdate(this, new GarmentUpdate.EndTaskListener<Boolean>(){
        @Override
        public void notify(Boolean result){
          if(result)
            endMe(RESULT_OK);
        }
      }).execute(garment);

      return;
    }

    new GarmentInsertion(this, new GarmentInsertion.EndTaskListener<Long>(){
      @Override
      public void notify(Long result){
        if(result == null || result <= 0)
          return;

        garment.setId(result);
        endMe(RESULT_OK);
      }
    }).execute(garment);
  }

  private void endMe(int result){
    setResult(result);
    finish();
  }

  private void deleteGarment(){
    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(getResources().getString(R.string.delete_garment))
        .setMessage(
            getResources().getString(R.string.really_delete_garment) + " "
                + garment.getName() + "?").setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener(){
              @Override
              public void onClick(DialogInterface dialog, int which){
                new GarmentDeletion(EditGarment.this,
                    new GarmentDeletion.EndTaskListener<Boolean>(){
                      public void notify(Boolean result){
                        if(result)
                          endMe(RESULT_DELETE);
                      }
                    }).execute(garment);
              }
            }).setNegativeButton(R.string.cancel, null).show();
  }

  private void updateUI(){
    if(garment == null)
      return;

    editName.setText(garment.getName());
    editType.setSelection(garment.getType());
    editGrade.setSelection(garment.getGrade() - 1);
    editDateOfPurchase.updateDate(garment.getDate().get(Calendar.YEAR), garment
        .getDate().get(Calendar.MONTH), garment.getDate().get(
        Calendar.DAY_OF_MONTH));
    editEleganceMin.setSelection(garment.getEleganceMin() - 1);
    editEleganceMax.setSelection(garment.getEleganceMax() - 1);
    editAutumn.setChecked(Seasons.getInstance().isSet(
        editAutumn.getText().toString(), garment.getSeasons()));
    editWinter.setChecked(Seasons.getInstance().isSet(
        editWinter.getText().toString(), garment.getSeasons()));
    editSpring.setChecked(Seasons.getInstance().isSet(
        editSpring.getText().toString(), garment.getSeasons()));
    editSummer.setChecked(Seasons.getInstance().isSet(
        editSummer.getText().toString(), garment.getSeasons()));
    editWeather.setSelection(garment.getWeather());
    editAvailable.setChecked(garment.isAvailable());

    enableSelectors(editable);
  }

  private void enableSelectors(boolean editable){
    this.editable = editable;
    editName.setEnabled(editable);
    editType.setEnabled(editable);
    editGrade.setEnabled(editable);
    editDateOfPurchase.setEnabled(editable);
    editEleganceMin.setEnabled(editable);
    editEleganceMax.setEnabled(editable);
    editAutumn.setEnabled(editable);
    editWinter.setEnabled(editable);
    editSpring.setEnabled(editable);
    editSummer.setEnabled(editable);
    editWeather.setEnabled(editable);
    editAvailable.setEnabled(editable);
    buttonCancel.setEnabled(editable);
  }
}
