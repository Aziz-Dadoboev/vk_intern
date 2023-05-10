package com.example.vk_intern;

public class MyListItem {
    private String path;
    private long date;
    private long size;
    private boolean isDirectory;
    private String type;
    private int hashCode;
    private String name;

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

    public void setName(String name) {
        this.name = name;
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

    public void setDate(long date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
}
