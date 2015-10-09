package com.huotu.partnermall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huotu.partnermall.inner.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 *本demo将在授权页面底部显示一个“关注官方微博”的提示框，
 *用户可以在授权期间对这个提示进行控制，选择关注或者不关
 *注，如果用户最后确定关注此平台官方微博，会在授权结束以
 *后执行关注的方法。
 */
public
class ShareAdapter extends BaseAdapter {


    private static String[] shareNames = new String[] { "新浪微博" , "微信朋友圈" , "QQ空间" };
    private        int[]    shareIcons = new int[] {
            R.drawable.logo_sinaweibo , R.drawable.logo_wechatmoments , R.drawable.logo_qzone
    };

    private LayoutInflater inflater;

    public
    ShareAdapter ( Context context ) {
        inflater = LayoutInflater.from ( context );
    }

    @Override
    public
    int getCount ( ) {
        return shareNames.length;
    }

    @Override
    public
    Object getItem ( int position ) {
        return null;
    }

    @Override
    public
    long getItemId ( int position ) {
        return 0;
    }

    @Override
    public
    View getView ( int position, View convertView, ViewGroup parent ) {
        if ( convertView == null ) {
            convertView = inflater.inflate(R.layout.share_item, null);
        }
        ImageView shareIcon = (ImageView) convertView.findViewById(R.id.share_icon);
        TextView shareTitle = (TextView ) convertView.findViewById(R.id.share_title);
        shareIcon.setImageResource(shareIcons[position]);
        shareTitle.setText(shareNames[position]);

        return convertView;
    }
}