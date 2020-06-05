package com.example.notepad20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.DBSchema.NoteBaseHelper;
import com.DBSchema.NoteCursorWrapper;
import com.DBSchema.NoteDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class NoteLab {
    private static NoteLab sNoteLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private NoteLab(Context context){
        mContext=context.getApplicationContext();
        mDatabase=new NoteBaseHelper(mContext).getWritableDatabase();

    }
    public void addNote(Note note){
        ContentValues values=getContentValues(note);
        mDatabase.insert(NoteDbSchema.NoteTable.NAME,null,values);
    }
    public List<Note> getNotes(){
        List<Note> notes=new ArrayList<>();

        NoteCursorWrapper cursor=queryNotes(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return notes;
    }
    public List<Note> getCollectedNotes(){
        List<Note> notes=new ArrayList<>();
        NoteCursorWrapper cursor=queryNotes(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                if (cursor.getNote().isLiked()){
                    notes.add(cursor.getNote());
                }
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return notes;
    }
    public Note getNote(UUID id){
        NoteCursorWrapper cursor=queryNotes(NoteDbSchema.NoteTable.Cols.UUID+" = ?",new String[]{id.toString()});
        try {
            if (cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        }finally {
            cursor.close();
        }
    }
    public void updateNote(Note crime){
        String uuidString=crime.getId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(NoteDbSchema.NoteTable.NAME,values, NoteDbSchema.NoteTable.Cols.UUID+" = ?",new String[]
                {uuidString});
    }
    public void deleteNote(UUID id){
        String uuidString=id.toString();
        mDatabase.delete(NoteDbSchema.NoteTable.NAME, NoteDbSchema.NoteTable.Cols.UUID+" = ?",new String[]{uuidString});

    }
    public static NoteLab get(Context context) {
        if(sNoteLab ==null){
            sNoteLab =new NoteLab(context);
        }
        return sNoteLab;
    }

    private static ContentValues getContentValues(Note note){
        ContentValues values=new ContentValues();
        values.put(NoteDbSchema.NoteTable.Cols.UUID,note.getId().toString());
        values.put(NoteDbSchema.NoteTable.Cols.TITLE,note.getTitle());
        values.put(NoteDbSchema.NoteTable.Cols.DATE,note.getDate().getTime());
        values.put(NoteDbSchema.NoteTable.Cols.COLLECTED,note.isLiked()?1:0);
        values.put(NoteDbSchema.NoteTable.Cols.DETAIL,note.getDetail());
        values.put(NoteDbSchema.NoteTable.Cols.PICTURE,note.getPicturePath());
        values.put(NoteDbSchema.NoteTable.Cols.LINK,note.getLink());
        return values;
    }
    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs){
        Cursor cursor=mDatabase.query(
                NoteDbSchema.NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }

    public File getPhotoFile(Note note){
        File filesDir=mContext.getFilesDir();
        return new File(filesDir,note.getPhotoFilename());
    }
    public void deletePhotoFile(Note note){
        File filesDir=mContext.getFilesDir();
        File file=new File(filesDir,note.getPhotoFilename());
        if (file.exists()&&file.isFile()){
           file.delete();
        }
    }
}
