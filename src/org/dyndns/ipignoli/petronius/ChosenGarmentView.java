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
import org.dyndns.ipignoli.petronius.choice.ChosenGarment;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.ParameterAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import org.dyndns.ipignoli.petronius.util.MyContext;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ChosenGarmentView extends ListActivity{

  public static final int ACTIVITY_TYPE_VIEW = 1;

  private int             garmentType;
  private ChosenGarment   chosen;

  private TextView        chosenGarmentName;
  private Button          buttonOK;

  @Override
  protected void onCreate(Bundle bundle){
    super.onCreate(bundle);
    this.setTitle(R.string.chosen_garment_view);

    setContentView(R.layout.parameter_list);

    Bundle extras = getIntent().getExtras();
    garmentType = (Integer)extras.get(MyHelper.F_CLOTHES_TYPE);

    chosenGarmentName = (TextView)findViewById(R.id.chosen_garment_name);

    buttonOK = (Button)findViewById(R.id.parameter_ok);
    buttonOK.setOnClickListener(new View.OnClickListener(){
      public void onClick(View view){
        endMe(RESULT_OK);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void onResume(){
    super.onResume();
    
    MyContext.initialize(this);

    chosen =
        ((List<Chooser>)CommonStore.getInstance().get(
            CommonStore.CLOTHES_CHOOSER_CHOOSER)).get(garmentType).getSelected();

    updateData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.chosen_garment_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item){
    switch(item.getItemId()){
      case R.id.chosen_garment_help:
        Intent helpIntent = new Intent(this, Help.class);

        CommonStore.getInstance().put(CommonStore.HELP_PAGE, R.raw.chosen_garment_help);

        startActivity(helpIntent);
        return true;
    }

    return super.onMenuItemSelected(featureId, item);
  }

  private void endMe(int result){
    setResult(result);
    finish();
  }

  private void updateData(){
    chosenGarmentName.setText(chosen.getGarment().getName() + ":");
    setListAdapter(new ParameterAdapter(this, chosen.getParameters()));
  }
}
