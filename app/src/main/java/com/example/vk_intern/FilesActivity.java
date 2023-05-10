package com.example.vk_intern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    List<MyListItem> files;
    private RecyclerView.Adapter<MyListAdapter.ViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.options_toolbar);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        // Get Views
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFiles = findViewById(R.id.no_files_textview);
        ImageView goBack = findViewById(R.id.back_toolbar_imageview);

        // Go Back Button on Toolbar
        goBack.setOnClickListener(v -> finish());

        // Get Files
        String path = getIntent().getStringExtra("path");

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            files = dbHelper.getFilesInDirectoryFromDb(path);
            if (files.isEmpty()) {
                noFiles.setVisibility(View.VISIBLE);
                return;
            }
            noFiles.setVisibility(View.INVISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            files.sort(new Helper.SortFileName());
            files.sort(new Helper.SortFolder());
            recyclerAdapter = new MyListAdapter(getApplicationContext(), files);
            recyclerView.setAdapter(recyclerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", "DATABASE Reading Failed");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_by_name_asc) { // Name A-Z
            files.sort(new Helper.SortFileName());
        } else if (item.getItemId() == R.id.sort_by_name_desc) { // Name Z-A
            files.sort(new Helper.SortFileNameDesc());
        } else if (item.getItemId() == R.id.sort_by_size_desc) { // Size Z-A
            files.sort(new Helper.SortFileSizeDesc());
        } else if (item.getItemId() == R.id.sort_by_size_asc) { // Size A-Z
            files.sort(new Helper.SortFileSize());
        } else if (item.getItemId() == R.id.sort_by_date_asc) { // Date A-Z
            files.sort(new Helper.SortFileDate());
        } else if (item.getItemId() == R.id.sort_by_date_desc) { // Date Z-A
            files.sort(new Helper.SortFileDateDesc());
        } else if (item.getItemId() == R.id.sort_by_type_asc) { // Type A-Z
            files.sort(new Helper.SortFileType());
        } else if (item.getItemId() == R.id.sort_by_type_desc) { // Type Z-A
            files.sort(new Helper.SortFileTypeDesc());
        }
        recyclerAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}