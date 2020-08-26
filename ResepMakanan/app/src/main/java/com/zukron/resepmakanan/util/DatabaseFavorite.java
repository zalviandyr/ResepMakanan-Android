package com.zukron.resepmakanan.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.zukron.resepmakanan.R;
import com.zukron.resepmakanan.model.Meal;

import java.util.ArrayList;

/**
 * Project name is Resep Makanan
 * Created by Zukron Alviandy R on 8/26/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
public class DatabaseFavorite {
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseFavorite(Context context) {
        String appName = context.getResources().getString(R.string.app_name);
        sqLiteDatabase = context.openOrCreateDatabase(appName, Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS favorite(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_meal TEXT," +
                "name TEXT," +
                "thumb TEXT);");
    }

    public void insert(Meal meal) {
        String sql = "INSERT INTO favorite(id_meal, name, thumb) VALUES (?, ?, ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, meal.getId());
        statement.bindString(2, meal.getName());
        statement.bindString(3, meal.getThumb());

        statement.execute();
    }

    public ArrayList<Meal> getAll() {
        ArrayList<Meal> meals = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM favorite", null);
        if (cursor.moveToFirst()) {
            meals = new ArrayList<>();
            do {
                meals.add(new Meal(
                        // index 0 tidak, karena id
                        cursor.getString(1),
                        cursor.getString(2),
                        null, null,
                        cursor.getString(3),
                        null, null, null
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return meals;
    }

    public void delete(String idMeal) {
        String sql = "DELETE FROM favorite WHERE id_meal = ?";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, idMeal);

        statement.executeUpdateDelete();
    }

    public boolean isExists(String idMeal) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM favorite WHERE id_meal = " + idMeal, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 1;
    }
}
