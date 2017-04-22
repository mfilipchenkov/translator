package ru.yandex.mobilization.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import ru.yandex.mobilization.models.HistoryItem;
import ru.yandex.mobilization.models.TranslationItem;
import ru.yandex.mobilization.providers.DbProvider;

public class HistoryService {
    public static final String TABLE_NAME = "HISTORY";

    private DbProvider dbProvider;
    private Context context;

    public HistoryService(Context context) {
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
                + "CURRENT_DATE integer" + ");";
    }

    public void addToHistory(TranslationItem item) {
        ContentValues data = new ContentValues();
        String fromName = item.getFrom().getName();
        String toName = item.getTo().getName();
        long lDate = item.getDate().getTime();

        SQLiteDatabase db = this.dbProvider.getWritableDatabase();

        try {
            data.put("SOURCE_TEXT", item.getSourceText());
            data.put("TRANSLATED_TEXT", item.getText());
            data.put("LANG_FROM", fromName);
            data.put("LANG_TO", toName);
            data.put("CURRENT_DATE", lDate);
            db.insert(TABLE_NAME, null, data);
        } catch(Exception e) {
            Toast.makeText(this.context, "Не удалось добавить перевод в Историю", Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }

    public ArrayList<HistoryItem> getHistory() {
        SQLiteDatabase db = this.dbProvider.getWritableDatabase();
        ArrayList<HistoryItem> result = new ArrayList<>();

        try {
            result = new ArrayList<>();
            Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

            int idIndex, sourceIndex, textIndex, fromIndex, toIndex, dateIndex;

            if (c.moveToFirst()) {
                do {
                    idIndex = c.getColumnIndex("ID");
                    sourceIndex = c.getColumnIndex("SOURCE_TEXT");
                    textIndex = c.getColumnIndex("TRANSLATED_TEXT");
                    fromIndex = c.getColumnIndex("LANG_FROM");
                    toIndex = c.getColumnIndex("LANG_TO");
                    dateIndex = c.getColumnIndex("CURRENT_DATE");

                    HistoryItem item = new HistoryItem(
                        c.getInt(idIndex),
                        c.getString(textIndex),
                        c.getString(sourceIndex),
                        c.getString(fromIndex),
                        c.getString(toIndex),
                        c.getLong(dateIndex)
                    );
                    result.add(item);
                }
                while (c.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(context, "Не удалось получить данные Истории", Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
        return result;
    }
}
