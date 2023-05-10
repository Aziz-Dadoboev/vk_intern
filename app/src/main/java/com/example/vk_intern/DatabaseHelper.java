package com.example.vk_intern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_files_database.db";
    public static final String TABLE_NAME = "files_data_table";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FILE_PATH = "file_path";

    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_LAST_MODIFIED = "last_modified";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_IS_DIRECTORY = "is_directory";
    private static final String COLUMN_FILE_TYPE = "file_type";
    private static final String COLUMN_HASH_CODE = "hash_code";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FILES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_FILE_NAME + " TEXT,"
                + COLUMN_LAST_MODIFIED + " TEXT,"
                + COLUMN_FILE_SIZE + " INTEGER,"
                + COLUMN_IS_DIRECTORY + " BOOLEAN,"
                + COLUMN_FILE_TYPE + " TEXT,"
                + COLUMN_HASH_CODE + " TEXT);";
        try {
            db.execSQL(CREATE_FILES_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addFile (File file, String hashcode, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if the file already exists in the database
        Cursor cursor = db.query(TABLE_NAME, new String[] { COLUMN_FILE_PATH }, COLUMN_FILE_PATH + " = ?", new String[] { file.getAbsolutePath() }, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FILE_PATH, file.getAbsolutePath());
        cv.put(COLUMN_FILE_NAME, file.getName());
        cv.put(COLUMN_LAST_MODIFIED, String.valueOf(file.lastModified()));
        cv.put(COLUMN_FILE_SIZE, String.valueOf(file.length()/1024));
        cv.put(COLUMN_IS_DIRECTORY, file.isDirectory());
        cv.put(COLUMN_FILE_TYPE, type);
        cv.put(COLUMN_HASH_CODE, hashcode);

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public List<File> getChangedFiles(long lastOpened) {
        SQLiteDatabase db = getReadableDatabase();
        List<File> changedFiles = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE last_modified > ?", new String[] { String.valueOf(lastOpened) });
        if (cursor.moveToFirst()) {
            do {
                int colIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
                File file = new File(cursor.getString(colIndex));
                if (file.exists()) {
                    changedFiles.add(file);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return changedFiles;
    }

    public List<MyListItem> getFilesInDirectoryFromDb(String directoryPath) {
        SQLiteDatabase db = getReadableDatabase();
        List<MyListItem> files = new ArrayList<>();
        String[] selectionArgs = new String[] {
                directoryPath + "/%",
                directoryPath + "/%/%"
        };
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_FILE_PATH + " LIKE ? OR " + COLUMN_FILE_PATH + " LIKE ?",
//                selectionArgs);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_FILE_PATH + " LIKE ? AND NOT " + COLUMN_FILE_PATH + " LIKE ?",
                selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                int colIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
                System.out.println("Index: " + colIndex);
                String filePath = cursor.getString(colIndex);
                colIndex = cursor.getColumnIndex(COLUMN_FILE_NAME);
                String fileName = cursor.getString(colIndex);
                colIndex = cursor.getColumnIndex(COLUMN_LAST_MODIFIED);
                long lastModified = cursor.getLong(colIndex);
                colIndex = cursor.getColumnIndex(COLUMN_FILE_SIZE);
                long fileSize = cursor.getLong(colIndex);
                colIndex = cursor.getColumnIndex(COLUMN_IS_DIRECTORY);
                boolean isDirectory = cursor.getInt(colIndex) != 0;
                colIndex = cursor.getColumnIndex(COLUMN_FILE_TYPE);
                String fileType = cursor.getString(colIndex);
                colIndex = cursor.getColumnIndex(COLUMN_HASH_CODE);
                int hashCode = cursor.getInt(colIndex);
                MyListItem item = new MyListItem(filePath, lastModified, fileSize, isDirectory, fileType, hashCode, fileName);
                files.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return files;
    }
}
