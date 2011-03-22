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
import java.util.Iterator;
import java.util.List;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.choice.Chooser;
import org.dyndns.ipignoli.petronius.choice.ChosenGarment;
import org.dyndns.ipignoli.petronius.choice.Score;
import android.app.Activity;
import android.app.ProgressDialog;


public class UpdateCompatibility extends
    MyAsyncTask<Chooser[], Integer, Chooser[]>{

  public UpdateCompatibility(Activity activity,
      EndTaskListener<Chooser[]> callback){
    super(activity.getResources().getString(R.string.compatibility_update),
        activity, callback);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
  }

  @Override
  protected Chooser[] doTheWork(Chooser[]... chosen) throws Exception{

    progressDialog.setMax(chosen[0].length);
    int progress = 0;

    List<Score> scores = new ArrayList<Score>();
    for(int i = 0; i < chosen[0].length; i++){
      if(chosen[0][i].size() == 0)
        continue;
      scores.add(new Score(chosen[0][i].getSelected(), chosen[0][i]
          .isSelected(),i));
    }
    Collections.sort(scores);

    for(Iterator<Score> itScores = scores.iterator(); itScores.hasNext();){
      int index = itScores.next().getChosenIndex();
      for(Iterator<ChosenGarment> itChosen = chosen[0][index].iterator(); itChosen
          .hasNext();)
        itChosen.next().updateCompatibility(chosen[0], scores);
      Collections.sort(chosen[0][index]);
      updateProgress(++progress);
    }

    return chosen[0];
  }
}
