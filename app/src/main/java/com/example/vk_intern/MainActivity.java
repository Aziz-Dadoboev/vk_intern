package com.example.vk_intern;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }

        Log.d("PROCESS" , "Started");
        String path = Environment.getExternalStorageDirectory().toString();
        Log.d("FILES", "Path: " + path);
        File directory = new File(path);
//        getAllFiles(directory);
        getFilesRecursive(directory);
        Log.d("PROCESS", "Finished");

    }

    private void getAllFiles(File directory) {
        File[] files = directory.listFiles();
        assert files != null;
        Log.d("FILES", "Size: "+ files.length);
        for (File value : files) {
            Log.d("FILES", "FileName:" + value.getName());
            Log.d("FILES", "Is Directory : " + value.isDirectory());
        }
    }

    private void getFilesRecursive(File directory) {
        if (directory == null) {
            return;
        }
        if (directory.isFile()) {
            Log.d("FILES", "Filename: " + directory.getName());
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File value : files) {
            getFilesRecursive(value);
        }
    }
}