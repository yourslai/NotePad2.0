package com.DBSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.notepad20.Note;

import java.util.Date;
import java.util.UUID;



public class NoteCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote(){
        String uuidString=getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.UUID));
        String title=getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.TITLE));
        String detail=getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.DETAIL));
        long date=getLong(getColumnIndex(NoteDbSchema.NoteTable.Cols.DATE));
        int isLiked=getInt(getColumnIndex(NoteDbSchema.NoteTable.Cols.COLLECTED));
        String picture=getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.PICTURE));
        String link=getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.LINK));

        Note note=new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(new Date(date));
        note.setLiked(isLiked!=0);
        note.setDetail(detail);
        note.setPicturePath(picture);
        note.setLink(link);



        return note;
    }
}
