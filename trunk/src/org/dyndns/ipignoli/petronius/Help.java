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

import java.io.IOException;
import java.io.InputStream;
import org.dyndns.ipignoli.petronius.util.CommonStore;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class Help extends Activity{

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    setContentView(R.layout.help);
    setTitle(R.string.help);
  }

  @Override
  protected void onResume(){
    super.onResume();

    WebView view = (WebView)findViewById(R.id.view_help);
    InputStream in =
        getResources().openRawResource(
            (Integer)CommonStore.getInstance().get(CommonStore.HELP_PAGE));

    String data = null;
    try{
      int size = in.available();
      byte[] buffer = new byte[size];
      in.read(buffer);
      in.close();
      data = new String(buffer);
    }catch(IOException e){
      data =
          getResources().getString(R.string.err_load_help) + ": "
              + e.getMessage();
      e.printStackTrace();
    }
    view.loadData(data, "text/html", "utf-8");
  }

  @Override
  public void finish(){
    CommonStore.getInstance().remove(CommonStore.HELP_PAGE);
    super.finish();
  }
}
