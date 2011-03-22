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
import org.dyndns.ipignoli.petronius.db.MyHelper;
import org.dyndns.ipignoli.petronius.ui.AbstractClothesListPicker;
import org.dyndns.ipignoli.petronius.ui.ChosenClothesAdapter;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import android.os.Bundle;


public class ChosenClothesListPicker extends AbstractClothesListPicker{

  public static final int ACTIVITY_TYPE_PICK = 1;

  private Chooser[]       chooser;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    long garmentId = getIntent().getExtras().getLong(MyHelper.F_CLOTHES_ID);

    Chooser[] chosen =
        (Chooser[])CommonStore.getInstance().get(
            CommonStore.CLOTHES_CHOOSER_CHOOSER);
    chooser = new Chooser[chosen.length - 1];
    int index = 0;
    for(Chooser c: chosen)
      if(c.getSelected().getGarment().getId() != garmentId)
        chooser[index++] = c;
  }

  @Override
  protected void updateData(){
    setListAdapter(new ChosenClothesAdapter(this, chooser));
  }
}
