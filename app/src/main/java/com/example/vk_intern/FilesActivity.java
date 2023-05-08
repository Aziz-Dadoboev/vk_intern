package com.example.vk_intern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private final int sortByNameAsc = R.id.sort_by_name_asc;
    private final int sortByNameDesc = R.id.sort_by_name_desc;
    private List<File> directoryListing;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<FileAdapter.ViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.options_toolbar);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        TextView noFiles = findViewById(R.id.no_files_textview);
        ImageView goBack = findViewById(R.id.back_toolbar_imageview);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        File[] files = root.listFiles();
        if (files == null || files.length == 0) {
            noFiles.setVisibility(View.VISIBLE);
            return;
        }
        noFiles.setVisibility(View.INVISIBLE);

        directoryListing = new ArrayList<>(Arrays.asList(files));
        directoryListing.sort(new SortFileName());
        directoryListing.sort(new SortFolder());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new FileAdapter(getApplicationContext(), directoryListing);
        recyclerView.setAdapter(recyclerAdapter);
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
        switch (item.getItemId()) {
            case sortByNameAsc:
                directoryListing.sort(new SortFileName());
                recyclerAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Сортировка по названию (А-Я)", Toast.LENGTH_SHORT).show();
                return true;
            case sortByNameDesc:
                directoryListing.sort(new SortFileNameDesc());
                recyclerAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Сортировка по названию (Я-А)", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //sorts based on the files name
    public static class SortFileName implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    }

    public static class SortFileNameDesc implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f2.getName().compareTo(f1.getName());
        }
    }

    //sorts based on a file or folder. folders will be listed first
    public static class SortFolder implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1.isDirectory() == f2.isDirectory())
                return 0;
            else if (f1.isDirectory() && !f2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}