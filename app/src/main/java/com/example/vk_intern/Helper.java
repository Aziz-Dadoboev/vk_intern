package com.example.vk_intern;

import android.os.AsyncTask;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

public class Helper {
    //sorts based on the files name
    public static class SortFileName implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static class SortFileNameDesc implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            return o2.getName().compareTo(o1.getName());
        }
    }

    // Sort by SIZE
    public static class SortFileSizeDesc implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            return Long.compare(o2.getSize(), o1.getSize());
        }
    }

    public static class SortFileSize implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            return Long.compare(o1.getSize(), o2.getSize());
        }
    }

    // Sort by DATE
    public static class SortFileDate implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            Date date1 = new Date(o1.getDate());
            Date date2 = new Date(o2.getDate());
            return date1.compareTo(date2);
        }
    }

    public static class SortFileDateDesc implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            Date date1 = new Date(o1.getDate());
            Date date2 = new Date(o2.getDate());
            return date2.compareTo(date1);
        }
    }

    // Sort by TYPE
    public static class SortFileType implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            String fileType1 = o1.getType();
            String fileType2 = o2.getType();
            return fileType1.compareToIgnoreCase(fileType2);
        }
    }

    public static class SortFileTypeDesc implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            String fileType1 = o1.getType();
            String fileType2 = o2.getType();
            return fileType2.compareToIgnoreCase(fileType1);
        }
    }

    //sorts based on a file or folder. folders will be listed first
    public static class SortFolder implements Comparator<MyListItem> {
        @Override
        public int compare(MyListItem o1, MyListItem o2) {
            if (o1.isDirectory() == o2.isDirectory())
                return 0;
            else if (o1.isDirectory() && !o2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}
