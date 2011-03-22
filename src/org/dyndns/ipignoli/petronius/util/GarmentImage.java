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

package org.dyndns.ipignoli.petronius.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.dyndns.ipignoli.petronius.Petronius;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.clothes.Types;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;


public class GarmentImage{

  public static final String  IMAGE_PREFIX    = "garment_icon_";
  public static final String  IMAGE_EXTENSION = ".png";

  private static GarmentImage instance;

  private Context             context;
  private int[]               defaultImages;

  public static GarmentImage getInstance(){
    if(instance == null)
      instance = new GarmentImage(MyContext.getInstance().getContext());
    return instance;
  }

  private GarmentImage(Context context){
    this.context = context;
    defaultImages = new int[Types.getInstance().getTotTypes()];
    // defaultImages[Types.getInstance().getIndex(
    // context.getResources().getString(R.string.pull_jacket))] =
    // context.getResources().getDrawable(R.drawable.jacket);
    // defaultImages[Types.getInstance().getIndex(
    // context.getResources().getString(R.string.shirt))] =
    // context.getResources().getDrawable(R.drawable.shirt);
    // defaultImages[Types.getInstance().getIndex(
    // context.getResources().getString(R.string.skirt_trousers))] =
    // context.getResources().getDrawable(R.drawable.trousers);
    // defaultImages[Types.getInstance().getIndex(
    // context.getResources().getString(R.string.coat))] =
    // context.getResources().getDrawable(R.drawable.coat);
    // defaultImages[Types.getInstance().getIndex(
    // context.getResources().getString(R.string.shoes))] =
    // context.getResources().getDrawable(R.drawable.shoes);
    defaultImages[Types.getInstance().getIndex(
        context.getResources().getString(R.string.pull_jacket))] =
        R.drawable.ic_list_jacket;
    defaultImages[Types.getInstance().getIndex(
        context.getResources().getString(R.string.shirt))] = R.drawable.ic_list_shirt;
    defaultImages[Types.getInstance().getIndex(
        context.getResources().getString(R.string.skirt_trousers))] =
        R.drawable.ic_list_trousers;
    defaultImages[Types.getInstance().getIndex(
        context.getResources().getString(R.string.coat))] = R.drawable.ic_list_coat;
    defaultImages[Types.getInstance().getIndex(
        context.getResources().getString(R.string.shoes))] = R.drawable.ic_list_shoes;
  }

  public Bitmap getGarmentImage(Garment garment){
    return getGarmentImage(garment.getImage(), garment.getId(), garment
        .getType());
  }

  public Bitmap getGarmentImage(long image, long garmentId, int garmentType){
    if(image == 1)
      try{
        return BitmapFactory.decodeStream(context.openFileInput(IMAGE_PREFIX
            + garmentId + IMAGE_EXTENSION));
      }catch(FileNotFoundException e){}
    return BitmapFactory.decodeResource(context.getResources(),
        defaultImages[garmentType]);
  }

  public void resetGarmentImage(long garmentId){
    context.deleteFile(IMAGE_PREFIX + garmentId + IMAGE_EXTENSION);
  }

  public void saveGarmentImage(String path, long garmentId)
      throws FileNotFoundException, IOException{
    Bitmap image =
        Bitmap.createScaledBitmap(BitmapFactory
            .decodeStream(new FileInputStream(path)), Petronius.ICON_WIDTH,
            Petronius.ICON_HEIGHT, true);
    FileOutputStream fos =
        context.openFileOutput(IMAGE_PREFIX + garmentId + IMAGE_EXTENSION,
            Activity.MODE_PRIVATE);
    image.compress(CompressFormat.PNG, 90, fos);
    fos.flush();
    fos.close();
  }
}
