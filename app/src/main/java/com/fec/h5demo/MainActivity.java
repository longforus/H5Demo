package com.fec.h5demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.fec.h5demo.bean.BaseBean;
import com.fec.h5demo.bean.JsonBean;
import com.fec.h5demo.func.FuncImpl;
import com.fec.h5demo.func.IJavaFunc;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.fec.h5demo.StartActivity.EXTRA_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String BASE_HEADER = "mc://";
    public static final String CLASS_HEADER = BASE_HEADER + "mcvc";
    public static final String FUNC_HEADER = BASE_HEADER + "mcfunc";
    private EditText et;
    private Button btn_go;
    private WebView wv;
    private Button btn_call;
    private Button btn_call1;
    private WebViewJavascriptBridge bridge;
    private Button btn_call2;
    private Button btn_call3;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGson = new Gson();
        initView();
        initEvent();
    }

    private void initEvent() {
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        wv.addJavascriptInterface(new IJavaFunc() {//添加js能够调用的方法接口
            @JavascriptInterface
            @Override
            public void makeToast(String msg, boolean lengthLong) {
                Toast.makeText(MainActivity.this, msg, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
           @JavascriptInterface
            @Override
            public void gotoActivity(String arg) {//使用json传输数据
                JsonBean jsonBean = mGson.fromJson(arg, JsonBean.class);
                startActivity(new Intent(jsonBean.getClassX()).putExtra("json", jsonBean));
            }
        }, "app");

        bridge = new WebViewJavascriptBridge(this, wv, new UserServerHandler());
        bridge.setCustomWebViewClient(new KCWebViewClient());
        bridge.registerHandler("handler1", new WebViewJavascriptBridge.WVJBHandler() {//定义给Js调用的handler
            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    jsCallback.callback("handler1 收到了:" + data);
                }
            }
        });
        bridge.registerHandler("gotoActivity", new WebViewJavascriptBridge.WVJBHandler() {//定义给Js调用的handler
            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (!TextUtils.isEmpty(data)) {
                    JsonBean jsonBean = mGson.fromJson(data, JsonBean.class);
                    startActivity(new Intent(jsonBean.getClassX()).putExtra("json", jsonBean));
                    if (null != jsCallback) {
                        jsCallback.callback("启动了activity:" + jsonBean.getClassX());
                    }
                }
            }
        });

        wv.loadUrl("file:///android_asset/demo.html");
        btn_go.setOnClickListener(this);
        btn_call.setOnClickListener(this);
        btn_call1.setOnClickListener(this);
    }

    private void initView() {
        et = (EditText) findViewById(R.id.et);
        btn_go = (Button) findViewById(R.id.btn_go);
        wv = (WebView) findViewById(R.id.wv);
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_call1 = (Button) findViewById(R.id.btn_call1);
        btn_call2 = (Button) findViewById(R.id.btn_call2);
        btn_call2.setOnClickListener(this);
        btn_call3 = (Button) findViewById(R.id.btn_call3);
        btn_call3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go:
                wv.loadUrl("file:///android_asset/demo.html");
                break;
            case R.id.btn_call:
                String jsCode = "toastAlert()";//调用js内部的方法
                wv.loadUrl("javascript:" + jsCode);
                //wv.loadUrl("javascript:alert('哈哈哈')" );
                break;
            case R.id.btn_call1:
                String param = et.getText().toString().trim();
                wv.loadUrl("javascript:actionFromNativeWithParam(" + "'" + param + "'" + ")");//传参调用js代码
                break;
            case R.id.btn_call2:
                bridge.send("呵呵", new WebViewJavascriptBridge.WVJBResponseCallback() {
                    @Override
                    public void callback(String data) {
                        Log.i("test", "java说:呵呵  js回复说:" + data);
                    }
                });
                break;
            case R.id.btn_call3:
                bridge.callHandler("showAlert", "99", new WebViewJavascriptBridge.WVJBResponseCallback() {
                    @Override
                    public void callback(String data) {
                        Log.i("test", "调用js方法回调: " + data);
                    }
                });//调用js方法
                break;
        }
    }

    public class KCWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            et.setText(url);
            if (url.startsWith(BASE_HEADER)) {
                String[] value = url.split("\\?");
                String[] val;
                switch (value[0]) {
                    case CLASS_HEADER://解析url获取bean 并传递给相应的activity
                        val = value[1].split("&");
                        String className = val[0].split("=")[1];
                        try {
                            Class clazz = Class.forName("com.fec.h5demo.bean." + className);
                            BaseBean instance = (BaseBean) clazz.newInstance();
                            for (int i = 1; i < val.length; i++) {
                                String[] field = val[i].split("=");
                                Field field1 = clazz.getField(field[0]);
                                field1.set(instance, field[1]);
                            }
                            startActivity(new Intent(MainActivity.this, StartActivity.class).putExtra(EXTRA_KEY, instance));//实际情况中,
                            // 这里是要启动不同的activity或者是不同的操作这里以一个activity代替
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case FUNC_HEADER:
                        try {
                            FuncImpl func = new FuncImpl(MainActivity.this);//根据url从固定的实现类中调用方法  这种方法存在传参不好确定的问题.有的方法不需要参数,有的需要参数,在这里无法进行确定
                            Class clazz = func.getClass();
                            val = value[1].split("&");
                            Method method = clazz.getDeclaredMethod(val[0].split("=")[1]);
                            method.invoke(func);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    static class UserServerHandler implements WebViewJavascriptBridge.WVJBHandler {//默认js调用处理

        @Override
        public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
            if (null != jsCallback) {
                jsCallback.callback("Java:默认处理收到" + data);
            }
        }
    }
}
