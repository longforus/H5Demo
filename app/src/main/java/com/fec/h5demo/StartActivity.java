package com.fec.h5demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.fec.h5demo.bean.BaseBean;
import com.fec.h5demo.bean.JsonBean;

public class StartActivity extends AppCompatActivity {

    public static final String EXTRA_KEY = "extra_key";
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        BaseBean extra = getIntent().getParcelableExtra(EXTRA_KEY);
        if (extra != null) {
            tv_1.setText(extra.showtype);
            tv_2.setText(extra.dataString);
            tv_id.setText(extra.jsid);
        }
        JsonBean json = getIntent().getParcelableExtra("json");
        if (json != null) {
            tv_1.setText(json.getShowtype());
            tv_2.setText(json.getDataString());
            tv_id.setText(json.getClassX());
        }
    }

    private void initView() {
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_id = (TextView) findViewById(R.id.tv_id);
    }
}
