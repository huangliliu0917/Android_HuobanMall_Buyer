package com.huotu.partnermall.ui;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.huotu.partnermall.BaseApplication;
import com.huotu.partnermall.config.Constants;
import com.huotu.partnermall.inner.R;
import com.huotu.partnermall.listener.PoponDismissListener;
import com.huotu.partnermall.model.ColorBean;
import com.huotu.partnermall.model.MerchantBean;
import com.huotu.partnermall.model.SysModel;
import com.huotu.partnermall.service.LocationService;
import com.huotu.partnermall.ui.base.BaseActivity;
import com.huotu.partnermall.ui.guide.GuideActivity;
import com.huotu.partnermall.ui.login.LoginActivity;
import com.huotu.partnermall.utils.ActivityUtils;
import com.huotu.partnermall.utils.AuthParamUtils;
import com.huotu.partnermall.utils.HttpUtil;
import com.huotu.partnermall.utils.KJLoger;
import com.huotu.partnermall.utils.PropertiesUtil;
import com.huotu.partnermall.utils.XMLParserUtils;
import com.huotu.partnermall.widgets.MsgPopWindow;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    private RelativeLayout mSplashItem_iv = null;
    private
    BaseApplication application;
    private Intent locationI = null;
    private boolean isConnection = false;// 假定无网络连接
    private
    MsgPopWindow popWindow;
    public DownloadManager downloadManager;

    @Override
    protected void findViewById() {
        mSplashItem_iv = (RelativeLayout) findViewById(R.id.welcomeTips);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BaseApplication) SplashActivity.this.getApplication();
        setContentView(R.layout.activity_splash);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.SCREEN_DENSITY = metrics.density;
        Constants.SCREEN_HEIGHT = metrics.heightPixels;
        Constants.SCREEN_WIDTH = metrics.widthPixels;

        mHandler = new Handler ( getMainLooper ( ) );
        findViewById ( );
        initView ( );


//        this.startActivity(new Intent(this, GoodManageActivity.class));
//        return;
    }

    @Override
    protected void initView() {
        AlphaAnimation anima = new AlphaAnimation(0.0f, 1.0f);
        anima.setDuration(Constants.ANIMATION_COUNT);// 设置动画显示时间
        mSplashItem_iv.setAnimation(anima);
        anima.setAnimationListener(
                new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        //检测网络
                        isConnection = application.checkNet(SplashActivity.this);
                        if (!isConnection) {
                            application.isConn = false;
                            //无网络日志
                            popWindow = new MsgPopWindow(SplashActivity.this, new
                                    SettingNetwork(), new CancelNetwork(), "网络连接错误",
                                    "请打开你的网络连接！", false);
                            popWindow.showAtLocation(SplashActivity.this.findViewById(R.id.welcomeTips), Gravity.CENTER, 0, 0);
                            popWindow.setOnDismissListener(new PoponDismissListener(SplashActivity.this));
                        } else {
                            application.isConn = true;
                            //定位
                            locationI = new Intent(SplashActivity.this,
                                    LocationService.class);
                            SplashActivity.this.startService(locationI);
                            //加载商家信息
                            //判断
                            if (!application.checkMerchantInfo()) {
                                //设置商户信息
                                MerchantBean merchant = XMLParserUtils.getInstance().readMerchantInfo(SplashActivity.this);
                                KJLoger.i("商户信息获取成功。");
                                //写入文件
                                if (null != merchant) {
                                    application.writeMerchantInfo(merchant);
                                } else {
                                    KJLoger.e("载入商户信息失败。");
                                }
                            }
                                                   /*if(!application.checkMenuInfo ())
                                                   {
                                                       //设置菜单
                                                       List<MenuBean> menus = XMLParserUtils.getInstance ().readMenuInfo ( SplashActivity.this );
                                                       //写入文件
                                                       if(null != menus && !menus.isEmpty ())
                                                       {
                                                           application.writeMenus ( menus );
                                                       }
                                                       else
                                                       {
                                                           KJLoger.e ( "载入主菜单失败。" );
                                                       }
                                                   }*/
                            //设置
                            //加载颜色配置信息
                            if (!application.checkColorInfo()) {
                                try {
                                    InputStream is = SplashActivity.this.getAssets

                                            ().open("color.properties");
                                    ColorBean color = PropertiesUtil
                                            .getInstance()
                                            .readProperties(is);
                                    application.writeColorInfo(color);
                                    //记录颜色值
                                    KJLoger.i("记录颜色值.");
                                } catch (IOException e) {
                                    KJLoger.e(e.getMessage());
                                }
                            }
                            //配置SYS信息
                            if (!application.checkSysInfo()) {
                                SysModel sysModel = XMLParserUtils.getInstance().readSys(SplashActivity.this);
                                application.writeSysInfo(sysModel);
                            }
                            //获取数据包更新信息
                            String packageUrl = Constants.INTERFACE_PREFIX + "mall/CheckDataPacket";
                            String packageVersion = application.readPackageVersion();
                            if (TextUtils.isEmpty(packageVersion)) {
                                packageVersion = "0.0.1";
                                application.writePackageVersion(packageVersion);
                            }
                            packageUrl += "?customerId=" + application.readMerchantId() + "&dataPacketVersion=" + packageVersion;
                            AuthParamUtils paramPackage = new AuthParamUtils(application, System.currentTimeMillis(), packageUrl, SplashActivity.this);
                            final String packageUrls = paramPackage.obtainUrls();
                            HttpUtil.getInstance().doVolleyPackage(SplashActivity.this, application, packageUrls);

                            //获取商家域名
                            //获取商户站点
                            String rootUrl = Constants.INTERFACE_PREFIX + "mall/getmsiteurl";
                            rootUrl += "?customerId=" + application.readMerchantId();
                            AuthParamUtils paramUtil = new AuthParamUtils(application, System.currentTimeMillis(), rootUrl, SplashActivity.this);
                            final String rootUrls = paramUtil.obtainUrls();
                            HttpUtil.getInstance().doVolleySite(SplashActivity.this, application, rootUrls);
                            //获取商户logo信息
                            String logoUrl = Constants.INTERFACE_PREFIX + "mall/getConfig";
                            logoUrl += "?customerId=" + application.readMerchantId();
                            AuthParamUtils paramLogo = new AuthParamUtils(application, System.currentTimeMillis(), logoUrl, SplashActivity.this);
                            final String logoUrls = paramLogo.obtainUrls();
                            HttpUtil.getInstance().doVolleyLogo(
                                    SplashActivity.this, application,
                                    logoUrls);
                            //获取商户支付信息
                            String targetUrl = Constants.INTERFACE_PREFIX + "PayConfig?customerid=";
                            targetUrl += application.readMerchantId();//动态获取商户编号，现在暂时使用3447////application.readMerchantId ();
                            AuthParamUtils paramUtils = new AuthParamUtils(application, System.currentTimeMillis(), targetUrl, SplashActivity.this);
                            final String url = paramUtils.obtainUrls();
                            HttpUtil.getInstance().doVolley(SplashActivity.this, application, url);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (application.isConn) {
                            //是否首次安装
                            if (application.isFirst()) {
                                ActivityUtils.getInstance().skipActivity(SplashActivity.this, GuideActivity.class);
                                //写入初始化数据
                                application.writeInitInfo("inited");
                            } else {
                                //判断是否登录
                                if (application.isLogin()) {
                                    ActivityUtils.getInstance().skipActivity(SplashActivity.this, HomeActivity.class);

                                } else {
                                    ActivityUtils.getInstance()
                                            .skipActivity(
                                                    SplashActivity
                                                            .this,
                                                    LoginActivity.class);
                                }
                            }

                        }
                    }
                });
    }

    @Override
    protected
    void onResume ( ) {
        super.onResume ( );
        JPushInterface.onResume ( SplashActivity.this );

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(SplashActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationI) {
            stopService(locationI);
        }
    }

    //设置网络点击事件
    private class SettingNetwork implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = null;
            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
            if (android.os.Build.VERSION.SDK_INT > 10) {
                intent = new Intent(
                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            } else {
                intent = new Intent();
                ComponentName component = new ComponentName(
                        "com.android.settings",
                        "com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            SplashActivity.this
                    .startActivity(intent);

        }
    }

    //取消设置网络
    private class CancelNetwork implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            popWindow.dismiss();
            // 未设置网络，关闭应用
            closeSelf(SplashActivity.this);
        }
    }
}

