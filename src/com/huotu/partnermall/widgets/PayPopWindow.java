package com.huotu.partnermall.widgets;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huotu.android.library.libpay.alipay.AliOrderInfo;
import com.huotu.android.library.libpay.alipay.AliPayInfo;
import com.huotu.android.library.libpay.alipay.AliPayUtil;
import com.huotu.android.library.libpay.weixin.WeiXinOrderInfo;
import com.huotu.android.library.libpay.weixin.WeiXinPayInfo;
import com.huotu.android.library.libpay.weixin.WeiXinPayUtil;
import com.huotu.partnermall.BaseApplication;
import com.huotu.partnermall.config.Constants;
import com.huotu.partnermall.inner.R;
import com.huotu.partnermall.listener.PoponDismissListener;
import com.huotu.partnermall.model.PayModel;
import com.huotu.partnermall.utils.EncryptUtil;
import com.huotu.partnermall.utils.WindowUtils;

import java.math.BigDecimal;

/**
 * 支付弹出框
 */
public class PayPopWindow extends PopupWindow implements View.OnClickListener{
    private Button wxPayBtn;
    private Button alipayBtn;
    private Button alipayMobileBtn;
    private Button cancelBtn;
    private View payView;
    private Activity aty;
    private Handler mHandler;
    private BaseApplication application;
    private PayModel payModel;
    //public ProgressPopupWindow progress;


    public PayPopWindow(final Activity aty, final Handler mHandler, final PayModel payModel) {
        super();
        this.aty = aty;
        this.mHandler = mHandler;
        this.application = BaseApplication.single;
        this.payModel = payModel;

        this.setOnDismissListener(new PoponDismissListener(aty));

        //progress = new ProgressPopupWindow(aty);
        LayoutInflater inflater = (LayoutInflater) aty.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        payView = inflater.inflate(R.layout.pop_pay_ui, null);
        wxPayBtn = (Button) payView.findViewById(R.id.wxPayBtn);
        alipayBtn = (Button) payView.findViewById(R.id.alipayBtn);
        alipayMobileBtn = (Button) payView.findViewById(R.id.alipayMobileBtn);
        cancelBtn = (Button) payView.findViewById(R.id.cancelBtn);

        showPayType();

        wxPayBtn.setOnClickListener(this);
        alipayBtn.setOnClickListener(this);
        alipayMobileBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //设置SelectPicPopupWindow的View
        this.setContentView(payView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

        this.setOutsideTouchable(true);

        this.setBackgroundDrawable(ContextCompat.getDrawable(aty , R.drawable.share_window_bg));

        WindowUtils.backgroundAlpha(aty, 0.4f);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alipayBtn) {
            aliPay();
        } else if (v.getId() == R.id.wxPayBtn) {
            wxPay();
        } else if (v.getId() == R.id.alipayMobileBtn) {
            aliMobilePay();
        } else if (v.getId() == R.id.cancelBtn) {
            dismissView();
        }
    }
    /***
     *  支付宝原始支付
     */
    protected void aliMobilePay(){
        dismissView();
        if (!application.scanAliPay()) {//缺少支付信息
            NoticePopWindow noticePop = new NoticePopWindow(aty, "缺少支付信息");
            noticePop.showNotice();
            noticePop.showAtLocation(aty.findViewById(R.id.titleText), Gravity.CENTER, 0, 0);
        }else{
            payModel.setAttach(payModel.getCustomId() + "_0");
            payModel.setNotifyurl(application.obtainMerchantUrl() + application.readAlipayNotify());
            AliPayInfo aliPayInfo = new AliPayInfo();
            aliPayInfo.setSellerId( application.readAlipayParentId() );
            aliPayInfo.setPartner(application.readAlipayParentId());
            aliPayInfo.setRsaPrivate(EncryptUtil.getInstance().decryptDES( application.readAlipayAppKey() , Constants.getDES_KEY() ));
            aliPayInfo.setNotifyUrl( payModel.getNotifyurl() );

            AliOrderInfo aliOrderInfo = new AliOrderInfo();
            aliOrderInfo.setTotalfee( new BigDecimal( payModel.getAliAmount()));
            aliOrderInfo.setSubject(payModel.getTradeNo());
            aliOrderInfo.setOrderNo(payModel.getTradeNo());
            aliOrderInfo.setBody(payModel.getDetail());

            AliPayUtil aliPayUtil = new AliPayUtil(aty , mHandler , aliPayInfo);
            aliPayUtil.pay(aliOrderInfo);
        }
    }

    protected void aliPay() {
        Message msg = new Message();
        msg.what = Constants.PAY_NET;
        payModel.setPaymentType("1");
        msg.obj = payModel;
        mHandler.sendMessage(msg);
        dismissView();
    }

    protected void wxPay(){
        dismissView();
        if (!application.scanWx()) {//缺少支付信息
            NoticePopWindow noticePop = new NoticePopWindow(aty, "缺少支付信息");
            noticePop.showNotice();
            noticePop.showAtLocation(aty.findViewById(R.id.titleText), Gravity.CENTER, 0, 0);
        } else {
            //progress.showProgress("正在加载支付信息");
            //progress.showAtLocation( aty.findViewById(R.id.titleText), Gravity.CENTER, 0, 0 );
            payModel.setAttach(payModel.getCustomId() + "_0");
            //添加微信回调路径
            payModel.setNotifyurl(application.obtainMerchantUrl() + application.readWeixinNotify());
//            PayFunc payFunc = new PayFunc(aty, payModel, application, mHandler, aty, progress);
//            payFunc.wxPay();

            WeiXinOrderInfo weiXinOrderInfo = new WeiXinOrderInfo();
            weiXinOrderInfo.setBody(payModel.getDetail());
            weiXinOrderInfo.setOrderNo(payModel.getTradeNo());
            weiXinOrderInfo.setTotal_fee(payModel.getAmount());
            weiXinOrderInfo.setAttach(payModel.getAttach());

            String wxAppId = application.readWxpayAppId();
            String wxAppSecret = EncryptUtil.getInstance().decryptDES( application.readWxpayAppKey() , Constants.getDES_KEY());
            String wxPartner=application.readWxpayParentId();
            String notifyUrl =application.obtainMerchantUrl() + application.readWeixinNotify();

            WeiXinPayInfo weiXinPayInfo = new WeiXinPayInfo( wxAppId , wxPartner , wxAppSecret , notifyUrl);
            WeiXinPayUtil weiXinPayUtil = new WeiXinPayUtil(aty , mHandler , weiXinPayInfo);
            weiXinPayUtil.pay(weiXinOrderInfo);
            //progress.dismissView();
        }
    }

    public void dismissView() {
        dismiss();
    }


    protected void showPayType() {
        String key = application.readAlipayAppKey();
        String partnerid = application.readAlipayParentId();
        boolean isWebAliPay = application.readIsWebAliPay();
//        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(partnerid)) {
//            alipayBtn.setVisibility(View.GONE);
//        }
        if( isWebAliPay ){
            alipayBtn.setVisibility(View.VISIBLE);
            alipayMobileBtn.setVisibility(View.GONE);
        }else {
            alipayBtn.setVisibility(View.GONE);
            if( TextUtils.isEmpty( key ) || TextUtils.isEmpty( partnerid ) ) {
                alipayMobileBtn.setVisibility(View.GONE);
            }else{
                alipayMobileBtn.setVisibility(View.VISIBLE);
            }
        }

        key = application.readWxpayAppKey();
        partnerid = application.readWxpayParentId();
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty( partnerid ) ){
            wxPayBtn.setVisibility(View.GONE);
        }
    }
}
