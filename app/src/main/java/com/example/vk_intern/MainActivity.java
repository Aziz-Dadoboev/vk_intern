package com.example.vk_intern;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
            File rootDirectory = new File(path);
            List<File> allFiles = getAllFilesInDirectory(rootDirectory);
            // Save to Data Base
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            for (File file : allFiles) {
                String hashcode = calculateHashCode(file);
                String type = FileAdapter.getFileExtension(file.getName());
                if (hashcode.equals("") || type.equals("")) {
                    continue;
                }
                if (dbHelper.addFile(file, hashcode, type)) {
                    Toast.makeText(this, "Inserted successfully", Toast.LENGTH_SHORT).show();
                    Log.d("DATABASE", "INSERTED");
                } else {
                    Log.d("DATABASE", "FAILED INSERTING");
                    Toast.makeText(this, "Inserted Failed", Toast.LENGTH_SHORT).show();
                }
            }
            intent.putExtra("path", path);
            startActivity(intent);
            finish();
        } else {
            askPermission();
        }
    }

    private boolean checkPermission() {
        int ans = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return ans == PackageManager.PERMISSION_GRANTED;
    }
    private void askPermission () {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is required!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private List<File> getAllFilesInDirectory(File directory) {
        List<File> fileList = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file);
                    } else if (file.isDirectory()) {
                        fileList.addAll(getAllFilesInDirectory(file));
                    }
                }
            }
        }
        return fileList;
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