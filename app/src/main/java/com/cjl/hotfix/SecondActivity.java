package com.cjl.hotfix;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cjl.hotfixlib.FixDexUtil;
import com.cjl.hotfixlib.utils.Constants;
import com.cjl.hotfixlib.utils.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void show(View view) {
        ParamsSort paramsSort = new ParamsSort();
        paramsSort.math(this);
    }

    public void fix(View view) {
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                fixByDex();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                restartApp();
            }
        }.execute();
    }

    private void fixByDex(){
        try {
            File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()
                    + File.separator + Constants.DEX_NAME);
            if (targetFile.exists()) {
                targetFile.delete();
                //Toast.makeText(this,"删除已存在的target dex",Toast.LENGTH_SHORT).show();
            }

            File sourceFile = new File(Environment.getExternalStorageDirectory(), Constants.DEX_NAME);
            if (!sourceFile.exists()) {
                InputStream sourceInputStream = getAssets().open(Constants.DEX_NAME);
                FileUtil.copy(sourceInputStream,targetFile);

            } else {
                FileUtil.copy(sourceFile, targetFile);
            }
            //Toast.makeText(this,"复制dex成功",Toast.LENGTH_SHORT).show();
            FixDexUtil.loadFixedDex(SecondActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restartApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}
