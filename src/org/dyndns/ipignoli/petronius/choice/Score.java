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

public class Score implements Comparable<Score>{

  private boolean selected;
  private int     type;
  private int     chosenIndex;
  private int     score;

  public Score(ChosenGarment garment, boolean selected, int chosenIndex){
    this.selected = selected;
    type = garment.getGarment().getType();
    this.chosenIndex = chosenIndex;
    score = garment.getTotal();
  }

  public int getType(){
    return type;
  }

  public int getChosenIndex(){
    return chosenIndex;
  }

  public int getScore(){
    return score;
  }

  @Override
  public int compareTo(Score another){
    if(another == null)
      return 1;

    if(selected && !another.selected)
      return -1;

    if(!selected && another.selected)
      return 1;

    if(getScore() > another.getScore())
      return -1;

    if(getScore() < another.getScore())
      return 1;

    return 0;
  }
}
