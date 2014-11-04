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

import java.util.GregorianCalendar;
import org.dyndns.ipignoli.petronius.R;
import org.dyndns.ipignoli.petronius.clothes.Garment;
import org.dyndns.ipignoli.petronius.compatibilities.Compatibility;
import org.dyndns.ipignoli.petronius.compatibilities.CompatibilityException;
import org.dyndns.ipignoli.petronius.history.HistoryRecord;
import org.dyndns.ipignoli.petronius.history.HistoryRecordException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyHelper extends SQLiteOpenHelper{

  public static final String  TABLE_CLOTHES                  = "clothes";
  public static final String  F_CLOTHES_ID                   = "_id";
  public static final String  F_CLOTHES_NAME                 = "name";
  public static final String  F_CLOTHES_TYPE                 = "type";
  public static final String  F_CLOTHES_GRADE                = "grade";
  public static final String  F_CLOTHES_DATE                 = "pdate";
  public static final String  F_CLOTHES_ELE_MIN              = "elemin";
  public static final String  F_CLOTHES_ELE_MAX              = "elemax";
  public static final String  F_CLOTHES_SEASONS              = "seasons";
  public static final String  F_CLOTHES_WEATHER              = "weather";
  public static final String  F_CLOTHES_AVAILABLE            = "available";
  public static final String  F_CLOTHES_IMAGE                = "image";

  public static final String  TABLE_HISTORY                  = "history";
  public static final String  F_HISTORY_ID                   = "_id";
  public static final String  F_HISTORY_DATE                 = "hdate";
  public static final String  F_HISTORY_GARMENT_ID           = "garment_id";

  public static final String  TABLE_COMPATIBILITIES          =
                                                                 "compatibilities";
  public static final String  F_COMPATIBILITIES_ID           = "_id";
  public static final String  F_COMPATIBILITIES_GARMENT_ID_1 = "garment_id_1";
  public static final String  F_COMPATIBILITIES_GARMENT_ID_2 = "garment_id_2";
  public static final String  F_COMPATIBILITIES_LEVEL        = "level";

  public static final String  TABLE_CHOICE                   = "choice";
  public static final String  F_CHOICE_DATE                  = "date";
  public static final String  F_CHOICE_ELE_MIN               = "elemin";
  public static final String  F_CHOICE_ELE_MAX               = "elemax";
  public static final String  F_CHOICE_SEASON                = "season";
  public static final String  F_CHOICE_WEATHER               = "weather";
  public static final String  F_CHOICE_PULL_JACKET           = "pull_jacket";
  public static final String  F_CHOICE_SHIRT                 = "shirt";
  public static final String  F_CHOICE_SKIRT_TROUSERS        = "skirt_trousers";
  public static final String  F_CHOICE_COAT                  = "coat";
  public static final String  F_CHOICE_SHOES                 = "shoes";

  protected static final int  DATABASE_VERSION               = 1;

  private static final String CREATE_CLOTHES                 =
                                                                 " ("
                                                                     + F_CLOTHES_ID
                                                                     + " integer primary key autoincrement, "
                                                                     + F_CLOTHES_NAME
                                                                     + " text not null, "
                                                                     + F_CLOTHES_TYPE
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_GRADE
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_DATE
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_ELE_MIN
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_ELE_MAX
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_SEASONS
                                                                     + " integer not null, "
                                                                     + F_CLOTHES_WEATHER
                                                                     + " integer not null,"
                                                                     + F_CLOTHES_AVAILABLE
                                                                     + " integer not null,"
                                                                     + F_CLOTHES_IMAGE
                                                                     + " integer not null"
                                                                     + " );";

  private static final String CREATE_HISTORY                 =
                                                                 " ("
                                                                     + F_HISTORY_ID
                                                                     + " integer primary key autoincrement, "
                                                                     + F_HISTORY_DATE
                                                                     + " integer not null, "
                                                                     + F_HISTORY_GARMENT_ID
                                                                     + " integer not null"
                                                                     + " );";

  private static final String CREATE_COMPATIBILITIES         =
                                                                 " ("
                                                                     + F_COMPATIBILITIES_ID
                                                                     + " integer primary key autoincrement, "
                                                                     + F_COMPATIBILITIES_GARMENT_ID_1
                                                                     + " integer not null,"
                                                                     + F_COMPATIBILITIES_GARMENT_ID_2
                                                                     + " integer not null,"
                                                                     + F_COMPATIBILITIES_LEVEL
                                                                     + " integer not null"
                                                                     + " );";

  private Context             context;

  public MyHelper(Context context){
    super(context,
        context.getResources().getText(R.string.database).toString(), null,
        DATABASE_VERSION);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db){
    db.execSQL("CREATE TABLE " + TABLE_CLOTHES + CREATE_CLOTHES);
    db.execSQL("CREATE TABLE " + TABLE_HISTORY + CREATE_HISTORY);
    db
        .execSQL("CREATE TABLE " + TABLE_COMPATIBILITIES
            + CREATE_COMPATIBILITIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

  public void clearDatabase(){
    getWritableDatabase().execSQL("DELETE FROM " + TABLE_CLOTHES);
    getWritableDatabase().execSQL("DELETE FROM " + TABLE_HISTORY);
    getWritableDatabase().execSQL("DELETE FROM " + TABLE_COMPATIBILITIES);
  }

  public void saveDatabase(){
    try{
      getWritableDatabase().execSQL("DROP TABLE " + TABLE_CLOTHES + "_tmp");
      getWritableDatabase().execSQL("DROP TABLE " + TABLE_HISTORY + "_tmp");
      getWritableDatabase().execSQL(
          "DROP TABLE " + TABLE_COMPATIBILITIES + "_tmp");
    }catch(SQLException e){}

    getWritableDatabase().execSQL(
        "CREATE TABLE " + TABLE_CLOTHES + "_tmp" + CREATE_CLOTHES);
    getWritableDatabase().execSQL(
        "CREATE TABLE " + TABLE_HISTORY + "_tmp" + CREATE_HISTORY);
    getWritableDatabase().execSQL(
        "CREATE TABLE " + TABLE_COMPATIBILITIES + "_tmp"
            + CREATE_COMPATIBILITIES);

    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_CLOTHES + "_tmp SELECT * FROM " + TABLE_CLOTHES);
    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_HISTORY + "_tmp SELECT * FROM " + TABLE_HISTORY);
    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_COMPATIBILITIES + "_tmp SELECT * FROM "
            + TABLE_COMPATIBILITIES);
  }

  public void restoreDatabase(){
    clearDatabase();

    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_CLOTHES + " SELECT * FROM " + TABLE_CLOTHES
            + "_tmp");
    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_HISTORY + " SELECT * FROM " + TABLE_HISTORY
            + "_tmp");
    getWritableDatabase().execSQL(
        "INSERT INTO " + TABLE_COMPATIBILITIES + " SELECT * FROM "
            + TABLE_COMPATIBILITIES + "_tmp");
  }

  public long insert(Garment garment){
    return getWritableDatabase().insert(TABLE_CLOTHES, null,
        createContentValues(garment));
  }

  public long insert(HistoryRecord historyRecord) throws HistoryRecordException{
    if(existsHistoryRecord(historyRecord.getDate(), historyRecord
        .getGarmentId()))
      throw new HistoryRecordException(context.getResources().getString(
          R.string.err_history_record_already_present));
    return getWritableDatabase().insert(TABLE_HISTORY, null,
        createContentValues(historyRecord));
  }

  public long insert(Compatibility compatibility) throws CompatibilityException{
    if(existsCompatibility(compatibility.getGarmentId1(), compatibility
        .getGarmentId2()))
      throw new CompatibilityException(context.getResources().getString(
          R.string.err_compatibility_already_present));
    return getWritableDatabase().insert(TABLE_COMPATIBILITIES, null,
        createContentValues(compatibility));
  }

  public boolean update(Garment garment){
    return getWritableDatabase().update(TABLE_CLOTHES,
        createContentValues(garment), F_CLOTHES_ID + "=" + garment.getId(),
        null) > 0;
  }

  public boolean update(HistoryRecord historyRecord){
    return getWritableDatabase().update(TABLE_HISTORY,
        createContentValues(historyRecord),
        F_HISTORY_ID + "=" + historyRecord.getId(), null) > 0;
  }

  public boolean update(Compatibility compatibility){
    return getWritableDatabase().update(TABLE_COMPATIBILITIES,
        createContentValues(compatibility),
        F_COMPATIBILITIES_ID + "=" + compatibility.getId(), null) > 0;
  }

  public boolean deleteGarment(long id){
    if(getWritableDatabase().delete(TABLE_CLOTHES, F_CLOTHES_ID + "=" + id,
        null) <= 0)
      return false;

    deleteGarmentHistoryRecords(id);
    deleteGarmentCompatibilities(id);
    return true;
  }

  public boolean deleteHistoryRecord(long id){
    return getWritableDatabase().delete(TABLE_HISTORY, F_HISTORY_ID + "=" + id,
        null) > 0;
  }

  public boolean deleteCompatibility(long id){
    return getWritableDatabase().delete(TABLE_COMPATIBILITIES,
        F_COMPATIBILITIES_ID + "=" + id, null) > 0;
  }

  private void deleteGarmentHistoryRecords(long garmentId){
    getWritableDatabase().delete(TABLE_HISTORY,
        F_HISTORY_GARMENT_ID + "=" + garmentId, null);
  }

  public boolean updateHistory(long date){
    return getWritableDatabase().delete(TABLE_HISTORY,
        F_HISTORY_DATE + "<" + date, null) > 0;
  }

  private void deleteGarmentCompatibilities(long garmentId){
    getWritableDatabase().delete(
        TABLE_COMPATIBILITIES,
        F_COMPATIBILITIES_GARMENT_ID_1 + "=" + garmentId + " OR "
            + F_COMPATIBILITIES_GARMENT_ID_2 + "=" + garmentId, null);
  }

  public Cursor fetchGarments(){
    return fetchGarments(null);
  }

  public Cursor fetchGarments(DataFilter filter){
    return getWritableDatabase().query(TABLE_CLOTHES, null,
        filter == null ? null : filter.getFilter(), null, null, null,
        F_CLOTHES_TYPE + " ASC, " + F_CLOTHES_NAME + " ASC");
  }

  public Cursor fetchHistoryRecords(){
    return fetchHistoryRecords(null);
  }

  public Cursor fetchHistoryRecords(DataFilter filter){
    return getWritableDatabase().query(TABLE_HISTORY, null,
        filter == null ? null : filter.getFilter(), null, null, null,
        F_HISTORY_DATE + " DESC, " + F_HISTORY_GARMENT_ID + " ASC");
  }

  public Cursor fetchCompatibilities(){
    return fetchCompatibilities(null);
  }

  public Cursor fetchCompatibilities(DataFilter filter){
    return getWritableDatabase().query(
        TABLE_COMPATIBILITIES,
        null,
        filter == null ? null : filter.getFilter(),
        null,
        null,
        null,
        F_COMPATIBILITIES_GARMENT_ID_1 + " ASC, "
            + F_COMPATIBILITIES_GARMENT_ID_2 + " ASC");
  }

  public Cursor fetchGarmentIds(){
    return fetchGarmentIds(null);
  }

  public Cursor fetchGarmentIds(DataFilter filter){
    return getWritableDatabase().query(TABLE_CLOTHES, new String[]{
      F_CLOTHES_ID
    }, filter == null ? null : filter.getFilter(), null, null, null,
        F_CLOTHES_ID + " ASC");
  }

  public Cursor fetchHistoryRecordIds(){
    return fetchHistoryRecordIds(null);
  }

  public Cursor fetchHistoryRecordIds(DataFilter filter){
    return getWritableDatabase().query(TABLE_HISTORY, new String[]{
      F_HISTORY_ID
    }, filter == null ? null : filter.getFilter(), null, null, null,
        F_HISTORY_ID + " ASC");
  }

  public Cursor fetchCompatibilityIds(){
    return fetchCompatibilityIds(null);
  }

  public Cursor fetchCompatibilityIds(DataFilter filter){
    return getWritableDatabase().query(
        TABLE_COMPATIBILITIES,
        new String[]{
          F_COMPATIBILITIES_ID
        },
        filter == null ? null : filter.getFilter(),
        null,
        null,
        null,
        F_COMPATIBILITIES_GARMENT_ID_1 + " ASC, "
            + F_COMPATIBILITIES_GARMENT_ID_2 + " ASC");
  }

  public int getTotGarments(){
    Cursor ret = null;
    try{
      ret =
          getWritableDatabase().query(TABLE_CLOTHES, null, null, null, null,
              null, null);
      return ret.getCount();
    }finally{
      if(ret != null)
        ret.close();
    }
  }

  public int getTotHistoryRecords(){
    Cursor ret = null;
    try{
      ret =
          getWritableDatabase().query(TABLE_HISTORY, null, null, null, null,
              null, null);
      return ret.getCount();
    }finally{
      if(ret != null)
        ret.close();
    }
  }

  public int getTotCompatibilities(){
    Cursor ret = null;
    try{
      ret =
          getWritableDatabase().query(TABLE_COMPATIBILITIES, null, null, null,
              null, null, null);
      return ret.getCount();
    }finally{
      if(ret != null)
        ret.close();
    }
  }

  public Garment fetchGarment(long id) throws SQLException{
    Cursor cursor = null;
    try{
      cursor =
          getWritableDatabase().query(true, TABLE_CLOTHES, null,
              F_CLOTHES_ID + "=" + id, null, null, null, null, null);

      if(cursor == null)
        return null;

      cursor.moveToFirst();

      return new Garment(cursor.getLong(0), cursor.getString(1), cursor
          .getInt(2), cursor.getInt(3), cursor.getLong(4), cursor.getInt(5),
          cursor.getInt(6), (byte)cursor.getInt(7), cursor.getInt(8), cursor
              .getInt(9) == 1 ? true : false, cursor.getLong(9));
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  public HistoryRecord fetchHistoryRecord(long id) throws SQLException{
    Cursor cursor = null;
    try{
      cursor =
          getWritableDatabase().query(true, TABLE_HISTORY, null,
              F_HISTORY_ID + "=" + id, null, null, null, null, null);

      if(cursor == null)
        return null;

      cursor.moveToFirst();

      return new HistoryRecord(cursor.getLong(0), cursor.getLong(1), cursor
          .getLong(2));
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  public Compatibility fetchCompatibility(long id) throws SQLException{
    Cursor cursor = null;
    try{
      cursor =
          getWritableDatabase().query(true, TABLE_COMPATIBILITIES, null,
              F_COMPATIBILITIES_ID + "=" + id, null, null, null, null, null);

      if(cursor == null)
        return null;

      cursor.moveToFirst();

      return new Compatibility(cursor.getLong(0), cursor.getLong(1), cursor
          .getLong(2), cursor.getInt(3));
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  public boolean existsHistoryRecord(GregorianCalendar date, Long garmentId){
    Cursor cursor = null;
    try{
      cursor =
          getWritableDatabase().query(
              true,
              TABLE_HISTORY,
              null,
              F_HISTORY_DATE + "=" + date.getTimeInMillis() + " AND "
                  + F_HISTORY_GARMENT_ID + " = " + garmentId, null, null, null,
              null, null);

      if(cursor == null)
        return false;

      cursor.moveToFirst();
      return cursor.getCount() > 0;
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  public boolean existsCompatibility(Long garmentId1, Long garmentId2){
    Cursor cursor = null;
    try{
      cursor =
          getWritableDatabase().query(
              true,
              TABLE_COMPATIBILITIES,
              null,
              "( " + F_COMPATIBILITIES_GARMENT_ID_1 + "=" + garmentId1
                  + " AND " + F_COMPATIBILITIES_GARMENT_ID_2 + "=" + garmentId2
                  + " ) OR ( " + F_COMPATIBILITIES_GARMENT_ID_1 + "="
                  + garmentId2 + " AND " + F_COMPATIBILITIES_GARMENT_ID_2 + "="
                  + garmentId1 + " )", null, null, null, null, null);

      if(cursor == null)
        return false;

      cursor.moveToFirst();
      return cursor.getCount() > 0;
    }finally{
      if(cursor != null)
        cursor.close();
    }
  }

  private ContentValues createContentValues(Garment garment){
    ContentValues values = new ContentValues();
    values.put(F_CLOTHES_NAME, garment.getName());
    values.put(F_CLOTHES_TYPE, garment.getType());
    values.put(F_CLOTHES_GRADE, garment.getGrade());
    values.put(F_CLOTHES_DATE, garment.getDate().getTimeInMillis());
    values.put(F_CLOTHES_ELE_MIN, garment.getEleganceMin());
    values.put(F_CLOTHES_ELE_MAX, garment.getEleganceMax());
    values.put(F_CLOTHES_SEASONS, garment.getSeasons());
    values.put(F_CLOTHES_WEATHER, garment.getWeather());
    values.put(F_CLOTHES_AVAILABLE, garment.isAvailable() ? 1 : 0);
    values.put(F_CLOTHES_IMAGE, garment.getImage());
    return values;
  }

  private ContentValues createContentValues(HistoryRecord historyRecord){
    ContentValues values = new ContentValues();
    values.put(F_HISTORY_DATE, historyRecord.getDate().getTimeInMillis());
    values.put(F_HISTORY_GARMENT_ID, historyRecord.getGarmentId());
    return values;
  }

  private ContentValues createContentValues(Compatibility compatibility){
    ContentValues values = new ContentValues();
    values.put(F_COMPATIBILITIES_GARMENT_ID_1, compatibility.getGarmentId1());
    values.put(F_COMPATIBILITIES_GARMENT_ID_2, compatibility.getGarmentId2());
    values.put(F_COMPATIBILITIES_LEVEL, compatibility.getLevel());
    return values;
  }
}
