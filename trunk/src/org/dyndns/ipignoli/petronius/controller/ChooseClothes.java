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

package org.dyndns.ipignoli.petronius.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.choice.ChoiceOptions;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.clothes.Types;
import org.dyndns.ipignoli.petronius.db.MyHelper;
import android.app.Activity;
import android.app.ProgressDialog;


public class ChooseClothes extends
    MyAsyncTask<ChoiceOptions, Integer, List<Chooser>>{

  private MyHelper dbHelper;

  public ChooseClothes(Activity activity,
      EndTaskListener<List<Chooser>> callback){
    super(activity.getResources().getString(R.string.clothes_choice), activity,
        callback);
    dbHelper = new MyHelper(activity);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
  }

  @Override
  protected List<Chooser> doTheWork(ChoiceOptions... options) throws Exception{
    List<Chooser> chosen =
        new ArrayList<Chooser>(Types.getInstance().getTotSet(
            options[0].getGarmentTypes()));

    progressDialog.setMax(Types.getInstance().getTotSet(
        options[0].getGarmentTypes()));

    Chooser chooser;
    for(int i = 0; i < Types.getInstance().getTotSet(
        options[0].getGarmentTypes()); i++){
      chooser =
          new Chooser(options[0].getGarmentType(i), options[0], dbHelper,
              getActivity().getResources());
      if(chooser.size() > 0){
        Collections.sort(chooser);
        chosen.add(chooser);
      }
      updateProgress(i + 1);
    }

    return chosen;
  }
}
