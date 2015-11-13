package com.huotu.partnermall.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.huotu.partnermall.BaseApplication;
import com.huotu.partnermall.config.Constants;
import com.huotu.partnermall.model.AccountModel;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 授权参数构建类
 */
public
class AuthParamUtils {

    private
    BaseApplication application;

    private String url;

    private long timestamp;
    private
    Context context;

    public
    AuthParamUtils(BaseApplication application, long timestamp, String url, Context context)
    {
        this.application = application;
        this.timestamp = timestamp;
        this.url = url;
        this.context = context;
    }

    public String obtainUrl()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
            if(!application.obtainMerchantUrl ().equals ( url ))
            {
                //获取url中的参数
                //String params = url.substring ( url.indexOf ( ".aspx?" ) + 6, url.length ( ) );
                String params  = "";
                if( url.contains("?") ){
                    params = url.substring( url.indexOf("?")+1 , url.length() );
                }
                String[] str = null;
                if( !TextUtils.isEmpty( params )) {
                    str = params.split("&");
                }
                if ( null != str && str.length > 0 ) {
                    for ( String map : str ) {
                        //获取参数
                        String[] values = map.split ( "=" );
                        if ( 2 == values.length ) {
                            paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                        }
                        else if ( 1 == values.length ) {
                            paramMap.put ( values[ 0 ], null );
                        }
                    }
                }

                //添加额外固定参数
                paramMap.put ( "version", application.getAppVersion ( context ) );
                paramMap.put ( "operation", Constants.OPERATION_CODE );
                paramMap.put ( "buserId", application.readUserId ( ) );
                //1、timestamp
                paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ),
                                                                "UTF-8" ) );
                //appid
                paramMap.put ( "appid", URLEncoder.encode ( Constants.APP_ID , "UTF-8" ));
                //unionid
                paramMap.put ( "unionid", URLEncoder.encode ( application.readUserUnionId ( ), "UTF-8" ) );

                //生成sigin
                paramMap.put ( "sign", getSign ( paramMap ) );

                builder.append ( url );
                builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
                builder.append ( "&appid="+paramMap.get ( "appid" ) );
                builder.append ( "&unionid="+paramMap.get ( "unionid" ) );
                builder.append ( "&sign="+paramMap.get ( "sign" ) );
                builder.append ( "&buserId="+application.readUserId ( ) );
                builder.append ( "&version=" + application.getAppVersion ( context ) );
                builder.append ( "&operation=" + Constants.OPERATION_CODE );
            }
            else
            {
                paramMap.put ( "version", application.getAppVersion ( context ) );
                paramMap.put ( "operation", Constants.OPERATION_CODE );
                paramMap.put ( "buserId", application.readUserId() );
                paramMap.put ( "customerid", application.readMerchantId ( ) );
                //添加额外固定参数
                //1、timestamp
                paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
                //appid
                paramMap.put ( "appid", URLEncoder.encode ( Constants.APP_ID , "UTF-8" ));
                //unionid
                paramMap.put (
                        "unionid", URLEncoder.encode (
                                application.readUserUnionId ( ),
                                "UTF-8" ) );
                //生成sigin
                paramMap.put ( "sign", getSign ( paramMap ) );

                builder.append ( url );
                builder.append ( "?timestamp=" + paramMap.get ( "timestamp" ) );
                builder.append ( "&customerid="+application.readMerchantId ( ) );
                builder.append ( "&appid="+paramMap.get ( "appid" ) );
                builder.append ( "&unionid="+paramMap.get ( "unionid" ) );
                builder.append ( "&sign="+paramMap.get ( "sign" ) );
                builder.append ( "&buserId="+application.readUserId ( ) );
                builder.append ( "&version=" + application.getAppVersion ( context ) );
                builder.append ( "&operation=" + Constants.OPERATION_CODE );
            }

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.e ( e.getMessage ( ) );
                return null;
            }

    }

    /**
     * 获取post参数
     * @return
     */
    public
    Map obtainParams(AccountModel account)
    {
            Map< String, Object > paramMap = new HashMap< String, Object > ( );
            paramMap.put ( "customerId", application.readMerchantId ( ) );
            paramMap.put ( "sex", String.valueOf ( account.getSex ( ) ) );
            paramMap.put ( "nickname", account.getNickname ( ) );
            paramMap.put ( "openid", account.getOpenid ( ) );
            paramMap.put ( "city", account.getCity ( ) );
            paramMap.put ( "country", account.getCountry ( ) );
            paramMap.put ( "province", account.getProvince ( ) );
            paramMap.put ( "headimgurl", account.getAccountIcon ( ) );
            paramMap.put ( "unionid", account.getAccountUnionId ( ) );
            paramMap.put ( "timestamp", String.valueOf ( timestamp ) );
            paramMap.put ( "appid", Constants.APP_ID );
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constants.OPERATION_CODE );
            paramMap.put ( "sign", getSign ( paramMap ) );
            //去掉null值
            paramMap = removeNull(paramMap);
            return paramMap ;
    }

    private Map removeNull(Map map)
    {
        Map<String, String> lowerMap = new HashMap< String, String > (  );
        Iterator lowerIt = map.entrySet ().iterator ();
        while ( lowerIt.hasNext () )
        {

            Map.Entry entry = ( Map.Entry ) lowerIt.next ();
            Object value = entry.getValue ( );
            if( null != value )
            {
                lowerMap.put ( String.valueOf ( entry.getKey () ).toLowerCase (), String.valueOf ( value ) );
            }
        }

        return lowerMap;
    }

    public String obtainUrls()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
                //获取url中的参数
                String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
                String[] str = params.split ( "&" );
                if ( str.length > 0 ) {
                    for ( String map : str ) {
                        //获取参数
                        String[] values = map.split ( "=" );
                        if ( 2 == values.length ) {
                            paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                        }
                        else if ( 1 == values.length ) {
                            paramMap.put ( values[ 0 ], null );
                        }
                    }
                }

                //添加额外固定参数
                paramMap.put ( "version", application.getAppVersion ( context ) );
                paramMap.put ( "operation", Constants.OPERATION_CODE );
                //1、timestamp
                paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
                //appid
                paramMap.put ( "appid", URLEncoder.encode ( Constants.APP_ID , "UTF-8" ));
                //生成sigin
                paramMap.put ( "sign", getSign ( paramMap ) );

                builder.append ( url );
                builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
                builder.append ( "&appid="+paramMap.get ( "appid" ) );
                builder.append ( "&sign="+paramMap.get ( "sign" ) );
                builder.append ( "&version=" + application.getAppVersion ( context ) );
                builder.append ( "&operation=" + Constants.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.e ( e.getMessage ( ) );
            return null;
        }

    }

    public String obtainUrlName()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
            //获取url中的参数
            String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
            String[] str = params.split ( "&" );
            if ( str.length > 0 ) {
                for ( String map : str ) {
                    //获取参数
                    String[] values = map.split ( "=" );
                    if ( 2 == values.length ) {
                        paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                    }
                    else if ( 1 == values.length ) {
                        paramMap.put ( values[ 0 ], null );
                    }
                }
            }

            //添加额外固定参数
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constants.OPERATION_CODE );
            //1、timestamp
            paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
            //appid
            paramMap.put ( "appid", URLEncoder.encode ( Constants.APP_ID , "UTF-8" ));
            //生成sigin
            paramMap.put ( "sign", getSign ( paramMap ) );

            builder.append ( url );
            builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
            builder.append ( "&appid="+paramMap.get ( "appid" ) );
            builder.append ( "&sign="+paramMap.get ( "sign" ) );
            builder.append ( "&version=" + application.getAppVersion ( context ) );
            builder.append ( "&operation=" + Constants.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.e ( e.getMessage ( ) );
            return null;
        }

    }

    public String obtainUrlOrder()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
            //获取url中的参数
            String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
            String[] str = params.split ( "&" );
            if ( str.length > 0 ) {
                for ( String map : str ) {
                    //获取参数
                    String[] values = map.split ( "=" );
                    if ( 2 == values.length ) {
                        paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                    }
                    else if ( 1 == values.length ) {
                        paramMap.put ( values[ 0 ], null );
                    }
                }
            }

            //添加额外固定参数
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constants.OPERATION_CODE );
            //1、timestamp
            paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
            //appid
            paramMap.put ( "appid", URLEncoder.encode ( Constants.APP_ID , "UTF-8" ));
            //生成sigin
            paramMap.put ( "sign", getSign ( paramMap ) );

            builder.append ( url );
            builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
            builder.append ( "&appid="+paramMap.get ( "appid" ) );
            builder.append ( "&sign="+paramMap.get ( "sign" ) );
            builder.append ( "&version=" + application.getAppVersion ( context ) );
            builder.append ( "&operation=" + Constants.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.e ( e.getMessage ( ) );
            return null;
        }
    }

    private String getSign(Map map)
    {
        String values = this.doSort(map);
        KJLoger.i ( "sign", values );
        // values = URLEncoder.encode(values);
        //String signHex =DigestUtils.md5DigestAsHex(values.toString().getBytes("UTF-8")).toLowerCase();
        String signHex = EncryptUtil.getInstance().encryptMd532(values);
        KJLoger.i("signHex", signHex);
        return signHex;
    }

    /**
     *
     * @方法描述：获取sign码第二步：参数排序
     * @方法名：doSort
     * @参数：@param map
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    private String doSort(Map<String, String> map)
    {
        //将MAP中的key转成小写
        Map<String, String> lowerMap = new HashMap< String, String > (  );
        Iterator lowerIt = map.entrySet ().iterator ();
        while ( lowerIt.hasNext () )
        {
            Map.Entry entry = ( Map.Entry ) lowerIt.next ();
            Object value = entry.getValue ( );
            if( ! TextUtils.isEmpty ( String.valueOf ( value ) ) )
            {
                lowerMap.put ( String.valueOf ( entry.getKey () ).toLowerCase (), String.valueOf ( value ) );
            }
        }

        TreeMap<String, String> treeMap = new TreeMap< String, String > ( lowerMap );
        StringBuffer buffer = new StringBuffer();
        Iterator it = treeMap.entrySet ().iterator ();
        while(it.hasNext ())
        {
            Map.Entry entry =(Map.Entry) it.next();
            buffer.append ( entry.getKey ()+"=" );
            buffer.append ( entry.getValue ()+"&" );
        }
        String suffix = buffer.substring ( 0, buffer.length ()-1 )+Constants.APP_SECRET;
        return suffix;
    }

}
