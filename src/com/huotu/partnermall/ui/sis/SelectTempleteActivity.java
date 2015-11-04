package com.huotu.partnermall.ui.sis;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huotu.partnermall.image.BitmapLoader;
import com.huotu.partnermall.inner.R;
import com.huotu.partnermall.utils.ViewHolderUtil;
import com.huotu.partnermall.widgets.NetworkImageViewCircle;

import java.util.ArrayList;
import java.util.List;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class SelectTempleteActivity extends Activity implements View.OnClickListener{

    FeatureCoverFlow flow;
    CoverFlowAdapter adapter;
    List<TemplateModel> data;
    TextView back;
    TextView operate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sis_activity_select_templete);

        back=(TextView)findViewById(R.id.sis_selecttemplate_back);
        back.setOnClickListener(this);
        operate=(TextView)findViewById(R.id.sis_selecttemplate_operate);
        operate.setOnClickListener(this);

        flow = (FeatureCoverFlow)findViewById(R.id.sis_selecttemplate_show);
        adapter =new CoverFlowAdapter(this);

        data=new ArrayList<>();
        TemplateModel model = new TemplateModel();
        model.setUrl("http://file27.mafengwo.net/M00/C3/AE/wKgB6lQOj0WAV9CnAADdQ7JRe5c84.jpeg");
        model.setTitle("aaaaaaaaaa");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://news.xinhuanet.com/photo/2015-10/29/128371793_14460865923871n.jpg");
        model.setTitle("bbbbbbbbb");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://photocdn.sohu.com/20101028/Img276615376.jpg");
        model.setTitle("ccccccccc");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://tse3.mm.bing.net/th?id=OIP.Mf7393380e0097336300acdabcccc5947o0&pid=15.1");
        model.setTitle("dddddddddd");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://img1.gtimg.com/kid/pics/28359/28359562.jpg");
        model.setTitle("eeeeeeeeee");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://i3.sinaimg.cn/IT/2010/0606/20106691227.jpg");
        model.setTitle("ffffffffff");
        data.add(model);
        model = new TemplateModel();
        model.setUrl("http://tse2.mm.bing.net/th?id=OIP.M2a098aa358ab6282fe7bc98da41c6fbeo0&pid=15.1");
        model.setTitle("gggggggggg");
        data.add(model);

        adapter.setData(data);

        flow.setAdapter(adapter);

        flow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ToastUtils.showLongToast();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if( v.getId()==R.id.sis_selecttemplate_back){
            this.finish();
        }else if( v.getId()==R.id.sis_selecttemplate_operate){

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN){
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class TemplateModel {
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        private String url;
        private String title;
    }

    public class CoverFlowAdapter extends BaseAdapter {

        private List<TemplateModel> mData = new ArrayList<>(0);
        private Context mContext;

        public CoverFlowAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<TemplateModel> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int pos) {
            return mData.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.sis_selecttemplate_item, null);
            }

            NetworkImageViewCircle iv = ViewHolderUtil.get( rowView , R.id.sis_selecttemplate_item_pic );
            TextView tv = ViewHolderUtil.get(rowView , R.id.sis_selecttemplate_item_title);

            TemplateModel model = mData.get(position);
            BitmapLoader.create().displayUrl( mContext , iv , model.getUrl() , R.drawable.sis_pic , R.drawable.sis_pic );

            tv.setText(model.getTitle());

            return rowView;
        }
    }
}
