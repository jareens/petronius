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

import java.util.ArrayList;
import java.util.List;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.choice.ChosenGarment;
import org.dyndns.ipignoli.petronius.controller.HistorySave;
import org.dyndns.ipignoli.petronius.ui.SaveHistoryAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class SaveHistory extends ListActivity{

  public static final int  ACTIVITY_TYPE_SAVE = 1;

  private static final int RESULT_CANCEL      = 1;
  private static final int RESULT_SAVE_KO     = 2;

  public static interface SaveHistoryItemListener{
    public void OnClick(View v, int position);
  }

  private List<Chooser>           chooser;
  private long                    choiceDate;

  private Button                  buttonOK;
  private Button                  buttonCancel;

  private SaveHistoryItemListener listItemListener;

  private SparseBooleanArray      checked;

  private boolean                 ended;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    ended = false;

    setTitle(R.string.save_history_records);

    listItemListener = new SaveHistoryItemListener(){
      public void OnClick(View v, int position){
        SaveHistory.this.onListItemClick(SaveHistory.this.getListView(), v,
            position, position);
      }
    };

    setContentView(R.layout.choice_list);
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    Bundle extras = getIntent().getExtras();
    choiceDate = extras.getLong(ClothesChooser.CHOICE_DATE);

    registerForContextMenu(getListView());

    buttonOK = (Button)findViewById(R.id.chosen_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        saveHistory();
      }
    });

    buttonCancel = (Button)findViewById(R.id.chosen_cancel);
    buttonCancel.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        endMe(RESULT_CANCEL);
      }
    });
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.save_history_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.save_history_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE,
            R.raw.save_history_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  private void saveState(){
    if(getListAdapter() != null)
      checked =
          ((SaveHistoryAdapter)getListAdapter()).getCheckedItemPositions();

    CommonStore.getInstance().put(CommonStore.SAVE_HISTORY_CHOICE_DATE,
        choiceDate);
    CommonStore.getInstance().put(CommonStore.SAVE_HISTORY_CHECKED, checked);
  }

  @Override
  public void finish(){
    ended = true;
    clearState();
    super.finish();
  }

  @SuppressWarnings("unchecked")
  private void restoreState(){
    chooser =
        (List<Chooser>)CommonStore.getInstance().get(
            CommonStore.CLOTHES_CHOOSER_CHOOSER);

    if(CommonStore.getInstance().containsKey(
        CommonStore.SAVE_HISTORY_CHOICE_DATE))
      choiceDate =
          (Long)CommonStore.getInstance().get(
              CommonStore.SAVE_HISTORY_CHOICE_DATE);

    if(CommonStore.getInstance().containsKey(CommonStore.SAVE_HISTORY_CHECKED))
      checked =
          (SparseBooleanArray)CommonStore.getInstance().get(
              CommonStore.SAVE_HISTORY_CHECKED);
    else{
      checked = new SparseBooleanArray();
      for(int i = 0; i < chooser.size(); i++)
        checked.put(i, true);
    }
  }

  private void clearState(){
    CommonStore.getInstance().remove(CommonStore.SAVE_HISTORY_CHOICE_DATE);
    CommonStore.getInstance().remove(CommonStore.SAVE_HISTORY_CHECKED);
  }

  private void endMe(int result){
    setResult(result);
    finish();
  }

  private void saveHistory(){
    List<ChosenGarment> checked = new ArrayList<ChosenGarment>(chooser.size());
    for(int i = 0; i < chooser.size(); i++)
      if(((SaveHistoryAdapter)getListAdapter()).getCheckedItemPositions()
          .get(i))
        checked.add(chooser.get(i).getSelected());

    if(checked.size() == 0)
      (new AlertDialog.Builder(this)).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(
          R.string.err_on_history_save).setMessage(
          R.string.err_no_garment_selected).setCancelable(false)
          .setPositiveButton(R.string.ok, null).show();

    new HistorySave(choiceDate, this,
        new HistorySave.EndTaskListener<Boolean>(){
          @Override
          public void notify(Boolean result){
            endMe(result ? RESULT_OK : RESULT_SAVE_KO);
          }
        }).execute(checked.toArray(new ChosenGarment[]{}));
  }

  private void updateData(){
    setListAdapter(new SaveHistoryAdapter(this, chooser.toArray(new Chooser[]{}), checked,
        listItemListener));
  }
}
