package com.bmw.peek2slite.model;

/**
 * Created by admin on 2017/3/6.
 */

public class QueXian_GradeInfo {
    private int level;
    private String content;

    public QueXian_GradeInfo() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "QueXian_GradeInfo{" +
                "level=" + level +
                ", content='" + content + '\'' +
                '}';
    }
}
