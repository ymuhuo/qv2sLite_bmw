package com.bmw.peek2slite.presenter;

/**
 * Created by admin on 2016/9/5.
 */
public interface ControlPresenter {

    void  resetSocket();
    void startThread();

    void up();          //上命令
    void down();        //下命令
    void size_add();    //变倍变长
    void size_sub();    //变倍变短
    void zoom_add();    //聚焦近
    void zoom_sub();    //聚焦远
    void stop();        //停止命令
    void low_beam_open(int strength);    //近光灯开
    void low_beam_close();  //近光灯关
    void high_beam_open(int strength);   //远光灯开
    void high_beam_close(); //远光灯关
//    void getAngle();   //获取俯仰角
    void getBattery();   //获取电量；
    void getLaserRanging();  //激光测距
//    void getBoom();  //获取机芯倍数

    void open_picfangdou();
    void close_picfangdou();
    void open_kuandongtai();
    void close_kuandongtai();
    void open_lightyizhi();
    void close_lightyizhi();
    void open_touwu();
    void close_touwu();
    void open_gaoganguang();
    void close_gaoganguang();


    void release();     //释放资源
    void autoHorizontal(); //自动水平
    boolean isSocketNull();




}
