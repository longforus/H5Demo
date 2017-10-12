package com.fec.h5demo.func;

import android.content.Context;
import android.widget.Toast;

/**
 * 使用解析url地址所执行的方法
 */
public class FuncImpl implements IFunc {

        private Context mContext;

        public FuncImpl(Context context) {
                mContext = context;
        }

        @Override
        public void test() {
                Toast.makeText(mContext, "利用a标签调用了安卓方法", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void test1(String var) {
                Toast.makeText(mContext, "利用a标签调用.var:"+var, Toast.LENGTH_SHORT).show();
        }
    }