package com.themeUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.DBSchema.ThemeBaseHelper;
import com.DBSchema.ThemeCursorWrapper;
import com.DBSchema.ThemeDbSchema;


public class ThemeLab {
    private static ThemeLab sThemeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ThemeLab(Context context){
        mContext=context.getApplicationContext();
        mDatabase=new ThemeBaseHelper(mContext).getWritableDatabase();
    }


    public static ThemeLab get(Context context) {
            if(sThemeLab==null){
                sThemeLab=new ThemeLab(context);
        }
        return sThemeLab;
    }
    public void addTheme(theme theme){
        ContentValues values=getContentValues(theme);
        mDatabase.insert(ThemeDbSchema.ThemeTable.NAME,null,values);
    }

    public void updateTheme(theme theme){
        ContentValues values=getContentValues(theme);
        mDatabase.update(ThemeDbSchema.ThemeTable.NAME,values, ThemeDbSchema.ThemeTable.Cols.INDEX+" = ?",new String[]
                {"1"});
    }
    public theme getTheme(){
        ThemeCursorWrapper cursor=queryThemes(ThemeDbSchema.ThemeTable.Cols.INDEX+" = ?",new String[]{"1"});
        try {
            if (cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTheme();
        }finally {
            cursor.close();
        }
    }
    private static ContentValues getContentValues(theme theme){
        ContentValues values=new ContentValues();
        values.put(ThemeDbSchema.ThemeTable.Cols.NUMBER,theme.getNumber());
        values.put(ThemeDbSchema.ThemeTable.Cols.INDEX,"1");
        return values;
    }


    private ThemeCursorWrapper queryThemes(String whereClause, String[] whereArgs){
        Cursor cursor=mDatabase.query(
                ThemeDbSchema.ThemeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ThemeCursorWrapper(cursor);
    }
}
