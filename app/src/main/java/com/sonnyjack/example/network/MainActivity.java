package com.sonnyjack.example.network;

import android.Manifest;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sonnyjack.example.network.bean.Result2;
import com.sonnyjack.library.network.http.HttpManager;
import com.sonnyjack.permission.IRequestPermissionCallBack;
import com.sonnyjack.permission.PermissionUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        //request();
        startDownload();
    }

    private void request() {
        HttpParams httpParams = new HttpParams();
        httpParams.setContext(this);
        httpParams.setLoading(true);
        httpParams.setHttpUrl("http://wthrcdn.etouch.cn/weather_mini");
        httpParams.setTag(System.currentTimeMillis());
        httpParams.addParam("citykey", "101010100");
        /*HttpManager.getInstance().getAsync(httpParams, new HttpCallBack() {
            @Override
            public void onSuccess(HttpParams httpParams, String body) {
                Log.e(TAG, body);
            }
        });*/

        /*HttpManager.getInstance().getAsync(httpParams, new HttpObjectCallBack<Result>() {
            @Override
            public void onSuccess(HttpParams httpParams, Result result) {
                Log.e(TAG, result.getData());
                Log.e(TAG, result.getDesc());
                Log.e(TAG, result.getStatus());
            }
        });*/

        HttpManager.getInstance().getAsync(httpParams, new HttpObjectCallBack<Result2>() {
            @Override
            public void onSuccess(Result2 result) {
                Log.e(TAG, result.getDesc());
                Log.e(TAG, result.getStatus());
            }
        });
    }

    private void startDownload() {
        ArrayList<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionUtils.getInstances().requestPermission(this, permissionList, new IRequestPermissionCallBack() {
            @Override
            public void onGranted() {
                download();
            }

            @Override
            public void onDenied() {

            }
        });
    }

    private void download(){
        HttpParams httpParams = new HttpParams();
        httpParams.setContext(this);
        httpParams.setLoading(true);
        httpParams.setHttpUrl("http://dldir1.qq.com/weixin/android/weixin661android1220_1.apk");
        httpParams.setSaveFilePath(getRootFolderAbsolutePath());
        httpParams.setSaveFileName(buildFileNameByUrl(httpParams.getHttpUrl()));
        HttpManager.getInstance().download(httpParams, new HttpCallBack() {
            @Override
            public void onSuccess(String body) {
                Toast.makeText(MainActivity.this, "下载成功：" + body, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(int error, String message) {
                super.onFail(error, message);
                Toast.makeText(MainActivity.this, "下载失败：" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static String getRootFolderAbsolutePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return Environment.getRootDirectory().getAbsolutePath();
        }
    }

    public static String buildFileNameByUrl(String url) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            String tempUrl = url;
            int index = url.indexOf("?");
            if (index > 0) {
                tempUrl = url.substring(0, index);
            }
            index = tempUrl.lastIndexOf("/");
            fileName = tempUrl.substring(index + 1, tempUrl.length());
        }
        return fileName;
    }
}
