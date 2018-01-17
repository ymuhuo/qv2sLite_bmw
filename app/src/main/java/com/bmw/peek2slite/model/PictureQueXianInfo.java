package com.bmw.peek2slite.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by admin on 2017/3/2.
 */
@Table(name = "PictureQueXianInfo", execAfterTableCreated = "")
public class PictureQueXianInfo implements Comparable {

    private int id;
    @Column(column = "distance")
    private String distance;
    @Column(column = "style")
    private String style;
    @Column(column = "grade")
    private String grade;
    @Column(column = "name")
    private String name;
    @Column(column = "clock")
    private String clock;
    @Column(column = "length")
    private String length;
    @Column(column = "detail")
    private String detail;





    public PictureQueXianInfo() {

    }

    public PictureQueXianInfo(String distance, String style, String grade, String name, String clock, String length, String detail) {
        this.distance = distance;
        this.style = style;
        this.grade = grade;
        this.name = name;
        this.clock = clock;
        this.length = length;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int compareTo(Object o) {
        PictureQueXianInfo pictureQueXianInfo = (PictureQueXianInfo) o;

        int id = pictureQueXianInfo.getId();

        return Integer.valueOf(this.id).compareTo(id);
    }
}
