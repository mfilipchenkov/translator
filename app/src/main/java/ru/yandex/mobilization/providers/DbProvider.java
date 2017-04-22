package ru.yandex.mobilization.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.yandex.mobilization.services.FavoritesService;
import ru.yandex.mobilization.services.HistoryService;

public class DbProvider extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MOBILIZATION";

    public DbProvider(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryService.getCreateTableQuery());
        db.execSQL(FavoritesService.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
