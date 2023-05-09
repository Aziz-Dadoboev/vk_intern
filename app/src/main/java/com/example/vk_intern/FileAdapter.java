package com.example.vk_intern;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    Context context;
    List<File> fileList;
    public FileAdapter (Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File selectedFile = fileList.get(position);
        holder.text.setText(selectedFile.getName());

        Date fileDate = new Date(selectedFile.lastModified());
        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(fileDate);
        holder.date.setText(formattedDate);

        String fileSize = selectedFile.length() / 1024 + "KB";
        holder.size.setText(fileSize);

        if (selectedFile.isDirectory()) {
            holder.icon.setImageResource(R.drawable.folder_icon_img);
        } else {
            String type = getFileExtension(selectedFile.getName());
            switch (type) {
                case "jpg":
                    holder.icon.setImageResource(R.drawable.jpg);
                case "jpeg":
                    holder.icon.setImageResource(R.drawable.jpg);
                    break;
                case "pdf":
                    holder.icon.setImageResource(R.drawable.pdf);
                    break;
                case "png":
                    holder.icon.setImageResource(R.drawable.png);
                    break;
                case "txt":
                    holder.icon.setImageResource(R.drawable.txt);
                    break;
                case "xml":
                    holder.icon.setImageResource(R.drawable.xml);
                    break;
                default:
                    holder.icon.setImageResource(R.drawable.file_icon_img);
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (selectedFile.isDirectory()) {
                Intent intent = new Intent(context, FilesActivity.class);
                String path = selectedFile.getAbsolutePath();
                intent.putExtra("path", path);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = "image/*";
                    intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "Cannot open file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView icon;
        TextView size;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text_view);
            icon = itemView.findViewById(R.id.icon_image_view);
            size = itemView.findViewById(R.id.item_size_view);
            date = itemView.findViewById(R.id.item_date_view);
        }
    }



}
