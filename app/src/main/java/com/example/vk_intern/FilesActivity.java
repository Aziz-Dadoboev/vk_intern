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
import java.util.Date;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private List<File> directoryListing;
    private RecyclerView.Adapter<FileAdapter.ViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.options_toolbar);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
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
        if (item.getItemId() == R.id.sort_by_name_asc) { // Name A-Z
            directoryListing.sort(new SortFileName());
        } else if (item.getItemId() == R.id.sort_by_name_desc) { // Name Z-A
            directoryListing.sort(new SortFileNameDesc());
        } else if (item.getItemId() == R.id.sort_by_size_desc) { // Size Z-A
            directoryListing.sort(new SortFileSizeDesc());
        } else if (item.getItemId() == R.id.sort_by_size_asc) { // Size A-Z
            directoryListing.sort(new SortFileSize());
        } else if (item.getItemId() == R.id.sort_by_date_asc) { // Date A-Z
            directoryListing.sort(new SortFileDate());
        } else if (item.getItemId() == R.id.sort_by_date_desc) { // Date Z-A
            directoryListing.sort(new SortFileDateDesc());
        } else if (item.getItemId() == R.id.sort_by_type_asc) { // Type A-Z
            directoryListing.sort(new SortFileType());
        } else if (item.getItemId() == R.id.sort_by_type_desc) { // Type Z-A
            directoryListing.sort(new SortFileTypeDesc());
        }
        recyclerAdapter.notifyDataSetChanged();
//        switch (item.getItemId()) {
//            case R.id.sort_by_name_asc:
//                directoryListing.sort(new SortFileName());
//                recyclerAdapter.notifyDataSetChanged();
//                Toast.makeText(this, "Сортировка по названию (А-Я)", Toast.LENGTH_SHORT).show();
//                return true;
//            case sortByNameDesc:
//                directoryListing.sort(new SortFileNameDesc());
//                recyclerAdapter.notifyDataSetChanged();
//                Toast.makeText(this, "Сортировка по названию (Я-А)", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
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

    // Sort by SIZE
    public static class SortFileSizeDesc implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            long size1 = f1.length();
            long size2 = f2.length();

            return Long.compare(size2, size1);
        }
    }

    public static class SortFileSize implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            long size1 = f1.length();
            long size2 = f2.length();

            return Long.compare(size1, size2);
        }
    }

    // Sort by DATE
    public static class SortFileDate implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            Date date1 = new Date(f1.lastModified());
            Date date2 = new Date(f2.lastModified());
            return date1.compareTo(date2);
        }
    }

    public static class SortFileDateDesc implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            Date date1 = new Date(f1.lastModified());
            Date date2 = new Date(f2.lastModified());
            return date2.compareTo(date1);
        }
    }

    // Sort by TYPE
    public static class SortFileType implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            String fileType1 = FileAdapter.getFileExtension(f1.getName());
            String fileType2 = FileAdapter.getFileExtension(f2.getName());
            return fileType1.compareToIgnoreCase(fileType2);
        }
    }

    public static class SortFileTypeDesc implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            String fileType1 = FileAdapter.getFileExtension(f1.getName());
            String fileType2 = FileAdapter.getFileExtension(f2.getName());
            return fileType2.compareToIgnoreCase(fileType1);
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