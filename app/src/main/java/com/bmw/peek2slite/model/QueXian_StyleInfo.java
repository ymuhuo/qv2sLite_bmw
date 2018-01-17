package com.bmw.peek2slite.model;

import java.util.List;

/**
 * Created by admin on 2017/3/6.
 */

public class QueXian_StyleInfo {

    private List<QueXian_GradeInfo> gradeList;
    private int index;
    private String name;
    private String code;
    private String define;

    public QueXian_StyleInfo() {
    }

    public List<QueXian_GradeInfo> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<QueXian_GradeInfo> gradeList) {
        this.gradeList = gradeList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    @Override
    public String toString() {
        return "QueXian_StyleInfo{" +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", define='" + define + '\'' +
                "gradeList=" + gradeList +
                '}';
    }
}
