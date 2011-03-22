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

package org.dyndns.ipignoli.petronius.choice;

import org.dyndns.ipignoli.petronius.clothes.Garment;

public abstract class Parameter{
  private String name;
  protected Garment garment;
  protected int value;
  protected double weight;
  protected int max,min;

  public Parameter(String name,Garment garment,int min,int max){
    this.name=name;
    this.garment=garment;
    this.min=min;
    this.max=max;
    try{
      value=(int)(computeValue()*(min+100d/(max-min)));
    }catch(Exception e){
      value=min;
    }
    weight=computeWeight();
  }

  public String getName(){
    return name;
  }

  public double getWeightedValue(){
    return value*weight;
  }

  protected abstract int computeValue();
  
  protected double computeWeight(){
    return 1.0;
  }
}
