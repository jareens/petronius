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

import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.ui.ProgressUpdater;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;


public abstract class MyAsyncTask<Params, Progress, Result> extends
    AsyncTask<Params, Integer, Result> implements ProgressUpdater<Integer>{

  public static interface EndTaskListener<Result>{
    public void notify(Result result);
  }

  protected ProgressDialog        progressDialog;

  private String                  name;
  private Activity                activity;
  private EndTaskListener<Result> callback;

  private Result                  result;
  private Exception               error;

  public MyAsyncTask(String name, Activity activity,
      EndTaskListener<Result> callback){
    this.name = name;
    this.activity = activity;
    this.callback = callback;

    progressDialog = new ProgressDialog(activity);
    progressDialog.setCancelable(false);
    progressDialog.setTitle(name);
    progressDialog.setMessage(name + " "
        + activity.getResources().getString(R.string.in_progress));

    error = null;
  }

  @Override
  protected void onPreExecute(){
    super.onPreExecute();
    progressDialog.show();
  }

  @Override
  protected Result doInBackground(Params... params){
    try{
      return doTheWork(params);
    }catch(Exception e){
      setError(e);
      return null;
    }
  }

  @Override
  protected void onPostExecute(Result result){
    super.onPostExecute(result);

    this.result = result;

    try{
      progressDialog.dismiss();
    }catch(Exception e){}

    if(getError() != null){
      getError().printStackTrace();
      (new AlertDialog.Builder(getActivity())).setIcon(
          android.R.drawable.ic_dialog_alert).setTitle(
          getActivity().getResources().getString(R.string.err_on) + " "
              + getName()).setMessage(getError().getMessage()).setCancelable(
          false).setPositiveButton(R.string.ok, null).show();
    }

    callback.notify(result);
  }

  @Override
  protected void onProgressUpdate(Integer... values){
    super.onProgressUpdate(values);
    progressDialog.setProgress(values[0]);
  }

  protected abstract Result doTheWork(Params... params) throws Exception;

  public Result getResult(){
    return result;
  }

  public Exception getError(){
    return error;
  }

  public void setError(Exception error){
    this.error = error;
  }

  public String getName(){
    return name;
  }

  public Activity getActivity(){
    return activity;
  }

  public void updateProgress(Integer value){
    publishProgress(value);
  }
}
