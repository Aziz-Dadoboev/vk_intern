package com.example.vk_intern;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseTask extends AsyncTask<Void, Void, Void> {

    public interface FinishListener {
        void processFinish();
    }

    FinishListener finishListener;
    private final Context context;

    public DataBaseTask(Context context, FinishListener finishListener) {
        this.context = context;
        this.finishListener = finishListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // get list of files
        String path = Environment.getExternalStorageDirectory().getPath();
        File rootDirectory = new File(path);
        List<File> allFiles = new ArrayList<>();
        getAllFilesInDirectory(rootDirectory, allFiles);

        // Save to Data Base
        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            for (File file : allFiles) {
                String hashcode = "Folder";
                String type = "Folder";
                if (file.isFile()) {
                    hashcode = calculateHashCode(file);
                    type = FileAdapter.getFileExtension(file.getName());
                }
                if (dbHelper.addFile(file, hashcode, type)) {
                    Log.d("DATABASE", "INSERTED");
                } else {
                    Log.d("DATABASE", "FAILED INSERTING");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DATABASE_ERROR", "Failed to save");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        String path = Environment.getExternalStorageDirectory().getPath();
        Intent intent = new Intent(context, FilesActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
        finishListener.processFinish();
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
