package com.renrun.updatelib;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by vice on 2017/10/9 16:11
 * E-mail:vicedev1001@gmail.com
 */

public class UpdateUtils {

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("update-->"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * @param context     这个必须是FragmentActivity
     * @param url         获取更新信息的url地址
     * @param versionCode 当前本地的版本号，用于和获取到的最新版本做比较
     * @param appid
     * @param at
     * @param showToast   是否要弹出无更新的提示
     * @param fileName    下载保存的apk名字
     */
    public static void checkUpdate(final FragmentActivity context, String url, final int versionCode, String appid, String at, final boolean showToast, final String fileName) {

        OkHttpUtils
                .get()
                .url(url)
                .addParams("appid", appid)
                .addParams("at", at)
                .addParams("os", "android")
                .build()
                .execute(new UpdateCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error-->", e.toString());
                        if (showToast) {
                            Toast.makeText(context, "网络有些问题~", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResponse(UpdateInfo response, int id) {
                        if (response != null) {
                            if (response.getR() == 1) {
                                if (checkshouldDownload(response, versionCode)) {
                                    UpdateFragment.newInstance(response, fileName).show(context.getSupportFragmentManager(), null);
                                } else {
                                    if (showToast) {
                                        Toast.makeText(context, "暂无更新~", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else if (TextUtils.isEmpty(response.getMsg())) {
                                if (showToast) {
                                    Toast.makeText(context, response.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (showToast) {
                                    Toast.makeText(context, "暂无更新~", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    private static boolean checkshouldDownload(UpdateInfo response, int versionCode) {
        try {
            if (response.getItem() == null) {
                return false;
            }

            if (!TextUtils.isEmpty(response.getItem().getVersion())) {
                if (Integer.parseInt(response.getItem().getVersion()) > versionCode) {
                    return !TextUtils.isEmpty(response.getItem().getDownloadUrl());
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static abstract class UpdateCallback extends Callback<UpdateInfo> {
        @Override
        public UpdateInfo parseNetworkResponse(Response response, int id) throws Exception {
            String string = response.body().string();
            UpdateInfo updateInfo = new Gson().fromJson(string, UpdateInfo.class);
            return updateInfo;
        }
    }
}
