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

package org.dyndns.ipignoli.petronius.db;

public class ClothesFilter implements DataFilter{

  private String garmentTypeFilter;
  private String garmentNameFilter;
  private String garmentEleganceMinFilter;
  private String garmentEleganceMaxFilter;
  private String garmentSeasonFilter;
  private String garmentWeatherFilter;
  private String garmentAvailableFilter;
  private String inverseGarmentTypeFilter;

  public ClothesFilter(){
    garmentTypeFilter = "";
    garmentNameFilter = "";
    garmentEleganceMinFilter = "";
    garmentEleganceMaxFilter = "";
    garmentSeasonFilter = "";
    garmentWeatherFilter = "";
    garmentAvailableFilter = "";
    inverseGarmentTypeFilter = "";
  }

  @Override
  public String getFilter(){
    StringBuffer ret = new StringBuffer();
    this.getStringFilter(inverseGarmentTypeFilter, ret);
    this.getStringFilter(garmentTypeFilter, ret);
    this.getStringFilter(garmentNameFilter, ret);
    this.getStringFilter(garmentEleganceMinFilter, ret);
    this.getStringFilter(garmentEleganceMaxFilter, ret);
    this.getStringFilter(garmentSeasonFilter, ret);
    this.getStringFilter(garmentWeatherFilter, ret);
    this.getStringFilter(garmentAvailableFilter, ret);
    return ret.length() > 0 ? ret.toString() : null;
  }

  private void getStringFilter(String filter, StringBuffer buffer){
    buffer.append(buffer.length() > 0 && filter.length() > 0 ? " AND " : "");
    buffer.append(filter.length() > 0 ? filter : "");
  }

  public void setGarmentTypeFilter(String filter){
    garmentTypeFilter =
        (filter == null || filter.length() == 0) ? "" : MyHelper.F_CLOTHES_TYPE
            + "=" + filter;
  }

  public void setGarmentNameFilter(String filter){
    garmentNameFilter =
        (filter == null || filter.length() == 0) ? "" : MyHelper.F_CLOTHES_NAME
            + " LIKE '%" + filter + "%'";
  }

  public void setGarmentEleganceMinFilter(String filter){
    garmentEleganceMinFilter =
        (filter == null || filter.length() == 0) ? ""
            : MyHelper.F_CLOTHES_ELE_MAX + " >= " + filter;
  }

  public void setGarmentEleganceMaxFilter(String filter){
    garmentEleganceMaxFilter =
        (filter == null || filter.length() == 0) ? ""
            : MyHelper.F_CLOTHES_ELE_MIN + " <= " + filter;
  }

  public void setGarmentSeasonFilter(String filter){
    garmentSeasonFilter =
        (filter == null || filter.length() == 0) ? ""
            : MyHelper.F_CLOTHES_SEASONS + " & ( 1<<" + filter + " ) > 0";
  }

  public void setGarmentWeatherFilter(String filter){
    garmentWeatherFilter =
        (filter == null || filter.length() == 0 || "0".equals(filter)) ? ""
            : "( " + MyHelper.F_CLOTHES_WEATHER + " = " + "0 OR "
                + MyHelper.F_CLOTHES_WEATHER + " = " + filter + " )";
  }

  public void setGarmentAvailableFilter(boolean filter){
    garmentAvailableFilter =
        (filter ? MyHelper.F_CLOTHES_AVAILABLE + " = 1" : "");
  }

  public void setInverseGarmentTypeFilter(String filter){
    inverseGarmentTypeFilter =
        (filter == null || filter.length() == 0) ? "" : MyHelper.F_CLOTHES_TYPE
            + "!=" + filter;
  }
}
