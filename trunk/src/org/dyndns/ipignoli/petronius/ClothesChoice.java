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

import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.controller.UpdateCompatibility;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.ChosenClothesAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class ClothesChoice extends ListActivity{

  public static final int  ACTIVITY_TYPE_CHOOSE   = 1;

  private static final int RESULT_CANCEL          = 1;

  private static final int ACTIVITY_LIST          = 1;
  private static final int ACTIVITY_COMPATIBILITY = 2;
  private static final int ACTIVITY_VIEW          = 3;
  private static final int ACTIVITY_SAVE          = 4;

  private static final int MENU_PREVIOUS          = Menu.FIRST;
  private static final int MENU_NEXT              = Menu.FIRST + 1;
  private static final int MENU_DISCARD           = Menu.FIRST + 2;
  private static final int MENU_COMPATIBILITY     = Menu.FIRST + 3;
  private static final int MENU_VIEW              = Menu.FIRST + 4;

  private Chooser[]        chooser;
  private long             choiceDate;

  private Button           buttonOK;
  private Button           buttonCancel;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);
    setTitle(R.string.clothes_choice);

    setContentView(R.layout.choice_list);

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
        manageCancel();
      }
    });
  }

  @Override
  protected void onResume(){
    super.onResume();
    MyContext.initialize(this);

    chooser =
        (Chooser[])CommonStore.getInstance().get(
            CommonStore.CLOTHES_CHOOSER_CHOOSER);

    if(chooser.length == 0)
      buttonOK.setEnabled(false);

    updateData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.clothes_choice_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo){
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_PREVIOUS, 0, R.string.previous_choice);
    menu.add(0, MENU_NEXT, 1, R.string.next_choice);
    menu.add(0, MENU_DISCARD, 2, R.string.discard_choice);
    menu.add(0, MENU_COMPATIBILITY, 3, R.string.update_compatibilities);
    menu.add(0, MENU_VIEW, 4, R.string.view_parameters);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.clothes_choice_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE,
            R.raw.clothes_choice_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item){
    switch(item.getItemId()){
      case MENU_PREVIOUS:
        if(!chooser[((AdapterContextMenuInfo)item.getMenuInfo()).position]
            .selectPrevious())
          Toast.makeText(this,
              getResources().getString(R.string.err_already_at_first_garment),
              Toast.LENGTH_SHORT).show();
        else
          updateCompatibilities();
        return true;

      case MENU_NEXT:
        if(!chooser[((AdapterContextMenuInfo)item.getMenuInfo()).position]
            .selectNext())
          Toast.makeText(this,
              getResources().getString(R.string.err_already_at_last_garment),
              Toast.LENGTH_SHORT).show();
        else
          updateCompatibilities();
        return true;

      case MENU_DISCARD:
        if(chooser[((AdapterContextMenuInfo)item.getMenuInfo()).position]
            .size() == 1)
          Toast.makeText(this,
              getResources().getString(R.string.err_cant_discard_garment),
              Toast.LENGTH_SHORT).show();
        else{
          chooser[((AdapterContextMenuInfo)item.getMenuInfo()).position]
              .discardSelected();
          updateCompatibilities();
        }
        return true;

      case MENU_COMPATIBILITY:
        Intent compatibilityIntent = new Intent(this, EditCompatibility.class);
        compatibilityIntent.putExtra(Petronius.ACTIVITY_TYPE,
            EditCompatibility.ACTIVITY_TYPE_CREATE_CHOSEN);
        compatibilityIntent.putExtra(MyHelper.F_COMPATIBILITIES_GARMENT_ID_1,
            chooser[((AdapterContextMenuInfo)item.getMenuInfo()).position]
                .getSelected().getGarment().getId());

        CommonStore.getInstance().put(CommonStore.EDIT_COMPATIBILITY_EDITABLE,
            true);

        startActivityForResult(compatibilityIntent, ACTIVITY_COMPATIBILITY);
        return true;

      case MENU_VIEW:
        Intent intent = new Intent(this, ChosenGarmentView.class);
        intent.putExtra(Petronius.ACTIVITY_TYPE,
            ChosenGarmentView.ACTIVITY_TYPE_VIEW);
        intent.putExtra(MyHelper.F_CLOTHES_TYPE, ((AdapterContextMenuInfo)item
            .getMenuInfo()).position);
        startActivityForResult(intent, ACTIVITY_VIEW);
        return true;
    }

    return super.onContextItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(this, ChosenClothesPicker.class);
    intent.putExtra(Petronius.ACTIVITY_TYPE,
        ChosenClothesPicker.ACTIVITY_TYPE_PICK);
    intent.putExtra(MyHelper.F_CLOTHES_TYPE, position);
    startActivityForResult(intent, ACTIVITY_LIST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    super.onActivityResult(requestCode, resultCode, intent);

    if(resultCode != RESULT_OK || requestCode == ACTIVITY_VIEW)
      return;

    if(requestCode == ACTIVITY_SAVE){
      endMe(RESULT_OK);
      return;
    }

    if(requestCode == ACTIVITY_COMPATIBILITY)
      updateCompatibilities();

    updateData();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event){
    if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
      manageCancel();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  private void manageCancel(){
    if(chooser.length == 0)
      endMe(RESULT_CANCEL);

    (new AlertDialog.Builder(this)).setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.save_choices).setMessage(R.string.choices_not_saved)
        .setCancelable(false).setNeutralButton(R.string.dont_save,
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){
                endMe(RESULT_CANCEL);
              }
            }).setPositiveButton(R.string.save,
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){
                saveHistory();
              }
            }).setNegativeButton(R.string.cancel,
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){}
            }).show();
    return;
  }

  @Override
  public void finish(){
    clearState();
    super.finish();
  }

  private void clearState(){
    CommonStore.getInstance().remove(CommonStore.CLOTHES_CHOOSER_CHOOSER);
  }

  private void endMe(int result){
    setResult(result);
    finish();
  }

  private void saveHistory(){
    Intent intent = new Intent(this, SaveHistory.class);
    intent.putExtra(Petronius.ACTIVITY_TYPE, SaveHistory.ACTIVITY_TYPE_SAVE);
    intent.putExtra(ClothesChooser.CHOICE_DATE, choiceDate);
    startActivityForResult(intent, ACTIVITY_SAVE);
  }

  private void updateCompatibilities(){
    new UpdateCompatibility(this,
        new UpdateCompatibility.EndTaskListener<Chooser[]>(){
          @Override
          public void notify(Chooser[] result){
            if(result == null)
              return;

            updateData();
          }
        }).execute(chooser);
  }

  private void updateData(){
    setListAdapter(new ChosenClothesAdapter(this, chooser));
  }
}
