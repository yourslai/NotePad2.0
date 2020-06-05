package com.DBSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.themeUtil.theme;

public class ThemeCursorWrapper extends CursorWrapper {
    public ThemeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public theme getTheme(){
        theme theme=new theme();
        int num=getInt(getColumnIndex(ThemeDbSchema.ThemeTable.Cols.NUMBER));
        theme.setNumber(num);
        return theme;
    }
}
