package com.cjl.hotfix;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * bug类
 * @author chenjieliang
 */
public class ParamsSort {

    public void math(Activity activity) {
        int a = 10;
        int b = 0;
        Toast.makeText(activity,"ParamsSort >>> " + (a/b),Toast.LENGTH_SHORT).show();
    }

    public void math() {
        int a = 10;
        int b = 0;
        Log.i("test","ParamsSort >>> " + (a/b));
    }
}
