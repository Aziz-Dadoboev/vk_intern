package com.example.vk_intern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission()) {
            Intent intent = new Intent(MainActivity.this, FilesActivity.class);
            String path = Environment.getExternalStorageDirectory().getPath();

            // get list of files
            File rootDirectory = new File(path);
            List<File> allFiles = new ArrayList<>();
            getAllFilesInDirectory(rootDirectory, allFiles);

            // Save to Data Base
            try (DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext())) {
                for (File file : allFiles) {
                    String hashcode = "Folder";
                    String type = "Folder";
                    if (file.isFile()) {
                        hashcode = calculateHashCode(file);
                        type = FileAdapter.getFileExtension(file.getName());
                    }
                    if (dbHelper.addFile(file, hashcode, type)) {
                        Toast.makeText(this, "Inserted successfully", Toast.LENGTH_SHORT).show();
                        Log.d("DATABASE", "INSERTED");
                    } else {
                        Log.d("DATABASE", "FAILED INSERTING");
                        Toast.makeText(this, "Inserted Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DATABASE_ERROR", "Failed to save");
            }

            intent.putExtra("path", path);
            startActivity(intent);
            finish();
        } else {
            askPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastOpened", System.currentTimeMillis());
        editor.apply();
    }

    private boolean checkPermission() {
        int read = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
    }
    private void askPermission () {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is required!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void getAllFilesInDirectory(File directory, List<File> listFiles) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            listFiles.add(file);
            if (file.isDirectory()) {
                getAllFilesInDirectory(file, listFiles);
            }
        }
    }

    private String calculateHashCode(File file) {
        if (file.isDirectory()) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };
            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }
            fis.close();
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}