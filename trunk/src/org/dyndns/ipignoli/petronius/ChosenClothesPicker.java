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

import java.util.List;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.ui.ChosenClothesPickerAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


public class ChosenClothesPicker extends ListActivity{

  public static final int ACTIVITY_TYPE_PICK = 1;

  private Integer         chosenIndex;
  private Chooser         chooser;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);

    setTitle(R.string.chosen_clothes_picker);

    Bundle extras = getIntent().getExtras();
    chosenIndex = (Integer)extras.get(ClothesChoice.CHOSEN_INDEX);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void onResume(){
    super.onResume();

    MyContext.initialize(this);

    chooser =
        ((List<Chooser>)CommonStore.getInstance().get(
            CommonStore.CLOTHES_CHOOSER_CHOOSER)).get(chosenIndex);
    updateData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.clothes_list_picker_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.clothes_list_picker_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.clothes_list_picker_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    chooser.setSelected(chooser.get(position));
    endMe(RESULT_OK);
  }

  private void endMe(int result){
    setResult(result);
    finish();
  }

  private void updateData(){
    setListAdapter(new ChosenClothesPickerAdapter(this, chooser));
  }
}
