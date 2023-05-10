package com.example.vk_intern;

public class MyListItem {
    private String path;
    private final long date;
    private final long size;
    private final boolean isDirectory;
    private final String type;
    private final int hashCode;
    private final String name;

    public MyListItem(String path, long date, long size, boolean isDirectory, String type, int hashCode, String name) {
        this.path = path;
        this.date = date;
        this.size = size;
        this.isDirectory = isDirectory;
        this.type = type;
        this.hashCode = hashCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public long getSize() {
        return size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getType() {
        return type;
    }

    public int getHashCode() {
        return hashCode;
    }
}
