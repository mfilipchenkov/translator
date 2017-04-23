package ru.yandex.mobilization.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import ru.yandex.mobilization.models.FavoritesItem;
import ru.yandex.mobilization.models.HistoryItem;
import ru.yandex.mobilization.providers.DbProvider;

public class FavoritesService {
    public static final String TABLE_NAME = "FAVORITES";

    private DbProvider dbProvider;
    private Context context;

    public FavoritesService(Context context) {
        this.dbProvider = new DbProvider(context);
        this.context = context;
    }

    public static String getCreateTableQuery() {
        return "create table " + TABLE_NAME + " ("
                + "ID integer primary key autoincrement,"
                + "SOURCE_TEXT text,"
                + "TRANSLATED_TEXT text,"
                + "LANG_FROM text,"
                + "LANG_TO text,"
                + "HISTORY_ID int,"
                + "CURRENT_DATE integer" + ");";
    }

    public void addToFavorites(HistoryItem history) {
        ContentValues data = new ContentValues();

        SQLiteDatabase db = this.dbProvider.getWritableDatabase();

        try {
            data.put("SOURCE_TEXT", history.getSourceText());
            data.put("TRANSLATED_TEXT", history.getText());
            data.put("LANG_FROM", history.getFrom());
            data.put("LANG_TO", history.getTo());
            data.put("CURRENT_DATE", history.getLongDate());
            data.put("HISTORY_ID", history.getId());
            db.insert(TABLE_NAME, null, data);
        } catch(Exception e) {
            Toast.makeText(this.context, "Не удалось добавить перевод в Избранное", Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }

    public ArrayList<FavoritesItem> getFavorites() {
        SQLiteDatabase db = this.dbProvider.getWritableDatabase();
        ArrayList<FavoritesItem> result = new ArrayList<>();

        try {
            result = new ArrayList<>();
            Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

            int idIndex, sourceIndex, textIndex, fromIndex, toIndex, dateIndex, historyIdIndex;

            if (c.moveToFirst()) {
                do {
                    idIndex = c.getColumnIndex("ID");
                    sourceIndex = c.getColumnIndex("SOURCE_TEXT");
                    textIndex = c.getColumnIndex("TRANSLATED_TEXT");
                    fromIndex = c.getColumnIndex("LANG_FROM");
                    toIndex = c.getColumnIndex("LANG_TO");
                    dateIndex = c.getColumnIndex("CURRENT_DATE");
                    historyIdIndex = c.getColumnIndex("HISTORY_ID");

                    FavoritesItem item = new FavoritesItem(
                            c.getInt(idIndex),
                            c.getString(textIndex),
                            c.getString(sourceIndex),
                            c.getString(fromIndex),
                            c.getString(toIndex),
                            c.getLong(dateIndex),
                            c.getInt(historyIdIndex)
                    );
                    result.add(item);
                }
                while (c.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(context, "Не удалось получить Избранное", Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
        return result;
    }

    public void delete(FavoritesItem item) {
        SQLiteDatabase db = this.dbProvider.getWritableDatabase();

        try {
            String whereClause = "ID=?";
            String[] whereArgs = {String.valueOf(item.getId())};
            db.delete(TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            Toast.makeText(context, "Не удалось удалить строку", Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }

    public FavoritesItem get(long id) {
        SQLiteDatabase db = this.dbProvider.getWritableDatabase();
        FavoritesItem item = null;
        int idIndex, sourceIndex, textIndex, fromIndex, toIndex, dateIndex, historyIdIndex;
        try {
            String query = "select * from " + TABLE_NAME + " where HISTORY_ID=" + id + ";";
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                idIndex = c.getColumnIndex("ID");
                sourceIndex = c.getColumnIndex("SOURCE_TEXT");
                textIndex = c.getColumnIndex("TRANSLATED_TEXT");
                fromIndex = c.getColumnIndex("LANG_FROM");
                toIndex = c.getColumnIndex("LANG_TO");
                dateIndex = c.getColumnIndex("CURRENT_DATE");
                historyIdIndex = c.getColumnIndex("HISTORY_ID");

                item = new FavoritesItem(
                        c.getInt(idIndex),
                        c.getString(textIndex),
                        c.getString(sourceIndex),
                        c.getString(fromIndex),
                        c.getString(toIndex),
                        c.getLong(dateIndex),
                        c.getInt(historyIdIndex)
                );
            }
        } catch (Exception e) {
            Toast.makeText(context, "Не удалось получить Избранное", Toast.LENGTH_LONG).show();
        } finally {
            db.close();
            return item;
        }
    }
}
