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
import org.dyndns.ipignoli.petronius.choice.ChoiceOptions;
import org.dyndns.ipignoli.petronius.choice.ChoiceOptionsException;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.clothes.Types;
import org.dyndns.ipignoli.petronius.controller.ChooseClothes;
import org.dyndns.ipignoli.petronius.controller.UpdateCompatibility;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;


public class ClothesChooser extends Activity{

  public static int     RESULT_CANCEL = 2;

  public static String  CHOICE_DATE   = "ChoiceDate";

  private DatePicker    editDate;
  private Spinner       editEleganceMin;
  private Spinner       editEleganceMax;
  private Spinner       editSeason;
  private Spinner       editWeather;
  private CheckBox      editPullJacket;
  private CheckBox      editShirt;
  private CheckBox      editSkirtTrousers;
  private CheckBox      editCoat;
  private CheckBox      editShoes;
  private Button        buttonOK;
  private Button        buttonCancel;

  private ChoiceOptions choiceOptions;

  private boolean       ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    setTitle(R.string.clothes_chooser);

    setContentView(R.layout.clothes_chooser);

    editDate = (DatePicker)findViewById(R.id.chooser_date);
    editEleganceMin = (Spinner)findViewById(R.id.chooser_elegance_min);
    editEleganceMax = (Spinner)findViewById(R.id.chooser_elegance_max);
    editSeason = (Spinner)findViewById(R.id.chooser_season);
    editWeather = (Spinner)findViewById(R.id.chooser_weather);
    editPullJacket = (CheckBox)findViewById(R.id.chooser_pull_jacket);
    editShirt = (CheckBox)findViewById(R.id.chooser_shirt);
    editSkirtTrousers = (CheckBox)findViewById(R.id.chooser_skirt_trousers);
    editCoat = (CheckBox)findViewById(R.id.chooser_coat);
    editShoes = (CheckBox)findViewById(R.id.chooser_shoes);

    buttonOK = (Button)findViewById(R.id.chooser_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        launchChoice();
      }
    });

    buttonCancel = (Button)findViewById(R.id.chooser_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
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
    MyContext.initialize(this);
    restoreState();
    updateUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.clothes_chooser_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.clothes_chooser_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.clothes_chooser_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  private void saveState(){
    choiceOptions.setDate(new GregorianCalendar(editDate.getYear(), editDate
        .getMonth(), editDate.getDayOfMonth()).getTimeInMillis());
    choiceOptions.setEleganceMin(editEleganceMin.getSelectedItemPosition() + 1);
    choiceOptions.setEleganceMax(editEleganceMax.getSelectedItemPosition() + 1);
    choiceOptions.setSeason(editSeason.getSelectedItemPosition());
    choiceOptions.setWeather(editWeather.getSelectedItemPosition());
    choiceOptions.setGarmentTypes(Types.getInstance().getTypes(
        editPullJacket.isChecked(), editShirt.isChecked(),
        editSkirtTrousers.isChecked(), editCoat.isChecked(),
        editShoes.isChecked()));

    CommonStore.getInstance().put(CommonStore.CLOTHES_CHOOSER_CHOICE_OPTIONS,
        choiceOptions);
  }

  private void restoreState(){
    if(CommonStore.getInstance().containsKey(
        CommonStore.CLOTHES_CHOOSER_CHOICE_OPTIONS)){
      choiceOptions =
          (ChoiceOptions)CommonStore.getInstance().get(
              CommonStore.CLOTHES_CHOOSER_CHOICE_OPTIONS);
      return;
    }

    SharedPreferences pref = getPreferences(MODE_PRIVATE);
    choiceOptions =
        new ChoiceOptions(new GregorianCalendar(), 2, 2, 0, 0, Byte.MAX_VALUE);
    choiceOptions.setEleganceMin(pref.getInt(ChoiceOptions.F_ELEGANCE_MIN,
        choiceOptions.getEleganceMin()));
    choiceOptions.setEleganceMax(pref.getInt(ChoiceOptions.F_ELEGANCE_MAX,
        choiceOptions.getEleganceMax()));
    choiceOptions.setSeason(pref.getInt(ChoiceOptions.F_SEASON, choiceOptions
        .getSeason()));
    choiceOptions.setWeather(pref.getInt(ChoiceOptions.F_WEATHER, choiceOptions
        .getWeather()));
    choiceOptions.setGarmentTypes((byte)pref.getInt(
        ChoiceOptions.F_GARMENT_TYPES, choiceOptions.getGarmentTypes()));
  }

  private void savePreferences(){
    SharedPreferences.Editor pref = getPreferences(MODE_PRIVATE).edit();
    pref.putLong(ChoiceOptions.F_DATE, new GregorianCalendar(
        editDate.getYear(), editDate.getMonth(), editDate.getDayOfMonth())
        .getTimeInMillis());
    pref.putInt(ChoiceOptions.F_ELEGANCE_MIN, editEleganceMin
        .getSelectedItemPosition() + 1);
    pref.putInt(ChoiceOptions.F_ELEGANCE_MAX, editEleganceMax
        .getSelectedItemPosition() + 1);
    pref.putInt(ChoiceOptions.F_SEASON, editSeason.getSelectedItemPosition());
    pref.putInt(ChoiceOptions.F_WEATHER, editWeather.getSelectedItemPosition());
    pref.putInt(ChoiceOptions.F_GARMENT_TYPES, Types.getInstance().getTypes(
        editPullJacket.isChecked(), editShirt.isChecked(),
        editSkirtTrousers.isChecked(), editCoat.isChecked(),
        editShoes.isChecked()));
    pref.commit();
  }

  protected void launchChoice(){
    saveState();

    try{
      choiceOptions.check();
    }catch(ChoiceOptionsException e){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(R.string.err_on_choice)
          .setMessage(e.getMessage()).setCancelable(false).setPositiveButton(
              R.string.ok, null).show();
      return;
    }

    new ChooseClothes(ClothesChooser.this,
        new ChooseClothes.EndTaskListener<Chooser[]>(){
          @Override
          public void notify(Chooser[] result){
            if(result == null)
              return;

            new UpdateCompatibility(ClothesChooser.this,
                new UpdateCompatibility.EndTaskListener<Chooser[]>(){
                  @Override
                  public void notify(Chooser[] result){
                    if(result == null)
                      return;

                    CommonStore.getInstance().put(
                        CommonStore.CLOTHES_CHOOSER_CHOOSER, result);
                    Intent intent =
                        new Intent(ClothesChooser.this, ClothesChoice.class);
                    intent.putExtra(Petronius.ACTIVITY_TYPE,
                        ClothesChoice.ACTIVITY_TYPE_CHOOSE);
                    intent.putExtra(CHOICE_DATE, new GregorianCalendar(editDate
                        .getYear(), editDate.getMonth(), editDate
                        .getDayOfMonth()).getTimeInMillis());
                    startActivity(intent);

                    endMe(RESULT_OK);
                  }
                }).execute(result);
          }
        }).execute(choiceOptions);

  }

  private void endMe(int result){
    if(result == RESULT_OK)
      savePreferences();

    CommonStore.getInstance()
        .remove(CommonStore.CLOTHES_CHOOSER_CHOICE_OPTIONS);
    ended=true;

    setResult(result);
    finish();
  }

  private void updateUI(){
    editDate.updateDate(choiceOptions.getDate().get(Calendar.YEAR),
        choiceOptions.getDate().get(Calendar.MONTH), choiceOptions.getDate()
            .get(Calendar.DAY_OF_MONTH));
    editEleganceMin.setSelection(choiceOptions.getEleganceMin() - 1);
    editEleganceMax.setSelection(choiceOptions.getEleganceMax() - 1);
    editSeason.setSelection(choiceOptions.getSeason());
    editWeather.setSelection(choiceOptions.getWeather());
    editPullJacket.setChecked(Types.getInstance().isSet(
        editPullJacket.getText().toString(), choiceOptions.getGarmentTypes()));
    editShirt.setChecked(Types.getInstance().isSet(
        editShirt.getText().toString(), choiceOptions.getGarmentTypes()));
    editSkirtTrousers.setChecked(Types.getInstance()
        .isSet(editSkirtTrousers.getText().toString(),
            choiceOptions.getGarmentTypes()));
    editCoat.setChecked(Types.getInstance().isSet(
        editCoat.getText().toString(), choiceOptions.getGarmentTypes()));
    editShoes.setChecked(Types.getInstance().isSet(
        editShoes.getText().toString(), choiceOptions.getGarmentTypes()));
  }
}
