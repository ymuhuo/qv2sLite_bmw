package com.bmw.peek2slite.model;

import java.util.List;

/**
 * Created by admin on 2017/3/6.
 */

public class QueXianInfo {

    private List<QueXian_StyleInfo> styleList;
    private int index;
    private String name;
    private String code;

    public QueXianInfo() {
    }

    public List<QueXian_StyleInfo> getStyleList() {
        return styleList;
    }

    public void setStyleList(List<QueXian_StyleInfo> styleList) {
        this.styleList = styleList;
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

    @Override
    public String toString() {
        return "QueXianInfo{" +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                "styleList=" + styleList +
                '}';
    }
}
