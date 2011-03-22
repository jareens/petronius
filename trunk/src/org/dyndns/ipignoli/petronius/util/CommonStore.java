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

import java.util.HashMap;


public class CommonStore extends HashMap<String, Object>{

  public static final String CLOTHES_CHOOSER_CHOOSER            =
                                                                    "ClothesChooser.Chooser";
  public static final String CLOTHES_CHOOSER_CHOICE_OPTIONS     =
                                                                    "ClothesChooser.choiceOptions";

  public static final String CLOTHES_LIST_FIRST_DISPLAYED       =
                                                                    "ClothesList.firstDisplayed";
  public static final String CLOTHES_LIST_LIST_TOP              =
                                                                    "ClothesList.listTop";
  public static final String CLOTHES_LIST_GARMENT_ID            =
                                                                    "ClothesList.garmentId";

  public static final String COMPATIBILITY_LIST_GARMENT_ID_1    =
                                                                    "CompatibilityList.garmentId1";

  public static final String EDIT_COMPATIBILITY_COMPATIBILITY   =
                                                                    "EditCompatibility.compatibility";
  public static final String EDIT_COMPATIBILITY_EDITABLE        =
                                                                    "EditCompatibility.editable";

  public static final String EDIT_GARMENT_GARMENT               =
                                                                    "EditGarment.garment";
  public static final String EDIT_GARMENT_EDITABLE              =
                                                                    "EditGarment.editable";

  public static final String EDIT_HISTORY_RECORD_HISTORY_RECORD =
                                                                    "EditHistoryRecord.historyRecord";
  public static final String EDIT_HISTORY_RECORD_EDITABLE       =
                                                                    "EditHistoryRecord.editable";

  public static final String HELP_PAGE                          = "Help.page";

  public static final String HISTORY_LIST_GARMENT_ID            =
                                                                    "HistoryList.GarmentId";

  public static final String MANAGE_ARCHIVE_SD_CARD             =
                                                                    "ManageArchive.sdCard";
  public static final String MANAGE_ARCHIVE_FILE_FILTER         =
                                                                    "ManageArchive.fileFilter";

  public static final String PETRONIUS_TAB_SELECTED             =
                                                                    "Petronius.tabSelected";

  public static final String SAVE_ARCHIVE_SAVE_FILE             =
                                                                    "SaveArchive.saveFile";

  public static final String SAVE_HISTORY_CHOICE_DATE           =
                                                                    "SaveHistory.choiceDate";
  public static final String SAVE_HISTORY_CHECKED               =
                                                                    "SaveHistory.checked";

  private static final long  serialVersionUID                   = 1L;

  private static CommonStore instance;

  public static CommonStore getInstance(){
    if(instance != null)
      return instance;

    return instance = new CommonStore();
  }

  private CommonStore(){
    super();
  }
}
