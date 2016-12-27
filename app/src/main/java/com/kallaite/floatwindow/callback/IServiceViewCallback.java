package com.kallaite.floatwindow.callback;

import android.content.Context;

/**
 * Created on 16-12-26.
 */
public interface IServiceViewCallback {

    /**
     * 不带动画，创建悬浮球
     */
    void createFloatBall();

    /**
     * 带动画，创建悬浮球。
     *
     */
    void creatFloatBallWithAnimation();

    /**
     * 带动画带延时，创建悬浮球。
     *
     */
    void creatFloatBallWithAnimationDelay();

    /**
     * 移除悬浮球
     *
     */
    void removeFloatBall();

    /**
     * 不带动画，创建悬浮框主视图
     */
    void createHomeWindow();

    /**
     * 带动画,创建悬浮框主视图
     */
    void createHomeWindowWithAnimation();


    /**
     * 将悬浮窗从屏幕上移除。
     *
     */
    void removeHomeWindow();

    /**
     * 创建已安装应用窗口
     * @param pos
     */
    void createInstalledAppWindow(String pos);

    /**
     * 移除安装应用窗口
     */
    void removeInstalledAppWindow();

    /**
     * 创建收藏应用窗口
     */
    void createCollectView();

    /**
     * 移除收藏窗口
     */
    void removeCollectView();

    /**
     * 移除所有悬浮窗
     */
    void removeAllWindow(boolean resetFloatBall);

    /**
     * 安装应用
     * @param name
     */
    void addPackage(String name );

    /**
     * 卸载应用
     * @param name
     */
    void removePackage(String name);

    void setFloatBallSize(int size);

    int getFloatBallSize();


}
