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
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_files_database.db";
    private static final String TABLE_NAME = "files_data_table";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FILE_PATH = "file_path";
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
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FILE_PATH, file.getAbsolutePath());
        cv.put(COLUMN_LAST_MODIFIED, String.valueOf(file.lastModified()));
        cv.put(COLUMN_FILE_SIZE, String.valueOf(file.length()/1024));
        cv.put(COLUMN_IS_DIRECTORY, file.isDirectory());
        cv.put(COLUMN_FILE_TYPE, type);
        cv.put(COLUMN_HASH_CODE, hashcode);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        return true;
    }

    public List<File> getAllFiles(String path) {
        List<File> fileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_FILE_PATH,
                COLUMN_HASH_CODE
        };

        String selection = COLUMN_FILE_PATH + " LIKE ?";
        String[] selectionArgs = { path + "%" };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int colIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
            String filePath = cursor.getString(colIndex);
            colIndex = cursor.getColumnIndex(COLUMN_HASH_CODE);
            String hashCode = cursor.getString(colIndex);

            File file = new File(filePath, hashCode);
            fileList.add(file);
        }

        cursor.close();
        db.close();
        return fileList;
    }
}
