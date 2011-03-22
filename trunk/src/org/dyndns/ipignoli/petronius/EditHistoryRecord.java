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
import org.dyndns.ipignoli.petronius.controller.GarmentRetrieve;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordDeletion;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordInsertion;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordRetrieve;
import org.dyndns.ipignoli.petronius.controller.HistoryRecordUpdate;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.history.HistoryRecord;
import org.dyndns.ipignoli.petronius.history.HistoryRecordException;
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
import android.widget.DatePicker;


public class EditHistoryRecord extends Activity{

  public static final int  ACTIVITY_TYPE_EDIT           = 1;
  public static final int  ACTIVITY_TYPE_CREATE         = 2;
  public static final int  ACTIVITY_TYPE_CREATE_GARMENT = 3;

  public static final int  RESULT_CANCEL                = 1;
  public static final int  RESULT_DELETE                = 2;

  private static final int ACTIVITY_PICK                = 0;

  private Menu             menu;
  private boolean          editable;

  private DatePicker       editDate;
  private Button           buttonGarment;
  private Button           buttonOK, buttonCancel;

  private HistoryRecord    historyRecord;

  private boolean          ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    setTitle(R.string.history_record_edit);

    setContentView(R.layout.history_edit);

    editDate = (DatePicker)findViewById(R.id.edit_history_record_date);
    buttonGarment = (Button)findViewById(R.id.edit_history_record_garment);
    buttonGarment.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        Intent intent =
            new Intent(EditHistoryRecord.this, ClothesListPicker.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            ClothesListPicker.ACTIVITY_TYPE_PICK);
        startActivityForResult(intent, ACTIVITY_PICK);
      }
    });

    buttonOK = (Button)findViewById(R.id.edit_history_record_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        saveHistoryRecord();
      }
    });

    buttonCancel = (Button)findViewById(R.id.edit_history_record_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        endMe(RESULT_CANCEL);
      }
    });

    editable = false;

    Bundle extras = getIntent().getExtras();
    switch(extras.getInt(Petronius.ACTIVITY_TYPE)){
      case (ACTIVITY_TYPE_EDIT):
        new HistoryRecordRetrieve(this,
            new HistoryRecordRetrieve.EndTaskListener<HistoryRecord>(){
              public void notify(final HistoryRecord historyRecord){
                EditHistoryRecord.this.historyRecord = historyRecord;
                updateUI();
              }
            }).execute(extras.getLong(MyHelper.F_HISTORY_ID));
        break;

      case (ACTIVITY_TYPE_CREATE_GARMENT):
        historyRecord =
            new HistoryRecord(-1, new GregorianCalendar(), extras
                .getLong(MyHelper.F_HISTORY_GARMENT_ID));
        updateUI();
        break;

      case (ACTIVITY_TYPE_CREATE):
        historyRecord = new HistoryRecord(-1, new GregorianCalendar(), -1);
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
    inflater.inflate(R.menu.edit_history_record_menu, menu);
    if(editable)
      menu.removeItem(R.id.edit_history_record);
    if(historyRecord.getId() <= 0)
      menu.removeItem(R.id.delete_history_record);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.edit_history_record:
        enableSelectors(true);
        menu.removeItem(R.id.edit_history_record);
        return true;

      case R.id.delete_history_record:
        deleteHistoryRecord();
        return true;

      case R.id.edit_history_record_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.edit_history_record_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode){
      case ACTIVITY_PICK:
        if(resultCode != RESULT_OK)
          return;

        historyRecord.setGarmentId(data
            .getLongExtra(MyHelper.F_CLOTHES_ID, -1l));
        historyRecord.setDate(new GregorianCalendar(editDate.getYear(),
            editDate.getMonth(), editDate.getDayOfMonth()));

        return;
    }
  }

  @Override
  public void finish(){
    ended = true;
    super.finish();
  }

  private void saveState(){
    historyRecord.setDate(new GregorianCalendar(editDate.getYear(), editDate
        .getMonth(), editDate.getDayOfMonth()));

    CommonStore.getInstance().put(
        CommonStore.EDIT_HISTORY_RECORD_HISTORY_RECORD, historyRecord);
    CommonStore.getInstance().put(CommonStore.EDIT_HISTORY_RECORD_EDITABLE,
        editable);
  }

  private void restoreState(){
    if(CommonStore.getInstance().containsKey(
        CommonStore.EDIT_HISTORY_RECORD_HISTORY_RECORD))
      historyRecord =
          (HistoryRecord)CommonStore.getInstance().get(
              CommonStore.EDIT_HISTORY_RECORD_HISTORY_RECORD);

    editable =
        (Boolean)CommonStore.getInstance().get(
            CommonStore.EDIT_HISTORY_RECORD_EDITABLE);
  }

  private void saveHistoryRecord(){
    if(!editable){
      endMe(RESULT_OK);
      return;
    }

    saveState();

    try{
      historyRecord.check();
    }catch(HistoryRecordException e){
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(
          R.string.err_on_history_record).setMessage(e.getMessage())
          .setCancelable(false).setPositiveButton(R.string.ok, null).show();
      return;
    }

    if(historyRecord.getId() >= 0){
      new HistoryRecordUpdate(this,
          new HistoryRecordUpdate.EndTaskListener<Boolean>(){
            @Override
            public void notify(Boolean result){
              if(result)
                endMe(RESULT_OK);
            }
          }).execute(historyRecord);

      return;
    }

    new HistoryRecordInsertion(this,
        new HistoryRecordInsertion.EndTaskListener<Long>(){
          @Override
          public void notify(Long result){
            if(result == null || result <= 0)
              return;

            historyRecord.setId(result);
            endMe(RESULT_OK);
          }
        }).execute(historyRecord);
  }

  private void endMe(int result){
    CommonStore.getInstance().remove(
        CommonStore.EDIT_HISTORY_RECORD_HISTORY_RECORD);
    CommonStore.getInstance().remove(CommonStore.EDIT_HISTORY_RECORD_EDITABLE);

    setResult(result);
    finish();
  }

  private void deleteHistoryRecord(){
    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.delete_history_record).setMessage(
            R.string.really_delete_selected_history_record)
        .setCancelable(false).setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener(){
              @Override
              public void onClick(DialogInterface dialog, int which){
                new HistoryRecordDeletion(EditHistoryRecord.this,
                    new HistoryRecordDeletion.EndTaskListener<Boolean>(){
                      public void notify(Boolean result){
                        if(result)
                          endMe(RESULT_DELETE);
                      }
                    }).execute(historyRecord);
              }
            }).setNegativeButton(R.string.cancel, null).show();
  }

  private void updateUI(){
    if(historyRecord == null)
      return;

    editDate.updateDate(historyRecord.getDate().get(Calendar.YEAR),
        historyRecord.getDate().get(Calendar.MONTH), historyRecord.getDate()
            .get(Calendar.DAY_OF_MONTH));
    if(historyRecord.getGarmentId() > 0)
      new GarmentRetrieve(this, new GarmentRetrieve.EndTaskListener<Garment>(){
        @Override
        public void notify(Garment garment){
          if(garment == null)
            return;
          buttonGarment.setText(garment.getName());
        }
      }).execute(historyRecord.getGarmentId());
    else
      buttonGarment.setText(getResources().getString(
          R.string.pick_garment));
    enableSelectors(editable);
  }

  private void enableSelectors(boolean editable){
    this.editable = editable;
    editDate.setEnabled(editable);
    buttonGarment.setEnabled(editable);
    buttonCancel.setEnabled(editable);
  }
}
