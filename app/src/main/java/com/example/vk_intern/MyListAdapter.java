package com.example.vk_intern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    Context context;
    List<MyListItem> files;

    public MyListAdapter(Context context, List<MyListItem> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyListItem selectedFile = files.get(position);
        holder.text.setText(selectedFile.getName());
        Date fileDate = new Date(selectedFile.getDate());
        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(fileDate);
        holder.date.setText(formattedDate);

        String fileSize = selectedFile.getSize() + "KB";
        holder.size.setText(fileSize);

        if (selectedFile.isDirectory()) {
            holder.icon.setImageResource(R.drawable.folder_icon_img);
        } else {
            String type = selectedFile.getType();
            if (type.equals("pdf") || type.equals("apk")) {
                Log.d("PDF", "PDF FOUND");
            }
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
                String path = selectedFile.getPath();
                intent.putExtra("path", path);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = "image/*";
                    intent.setDataAndType(Uri.parse(selectedFile.getPath()), type);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "Cannot open file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
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
