package com.renrun.updatelib;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
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
                .addParams("app_id", appid)
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
                    public void onResponse(UpdateInfo updateInfo, int id) {
                        if (updateInfo != null) {
                            if (updateInfo.getR() == 1) {
                                if (checkshouldDownload(updateInfo, versionCode)) {
                                    UpdateFragment.newInstance(updateInfo, fileName).show(context.getSupportFragmentManager(), null);
                                } else {
                                    if (showToast) {
                                        Toast.makeText(context, "暂无更新~", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else if (TextUtils.isEmpty(updateInfo.getMsg())) {
                                if (showToast) {
                                    Toast.makeText(context, updateInfo.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (showToast) {
                                    Toast.makeText(context, "暂无更新~", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (showToast) {
                                Toast.makeText(context, "暂无更新~", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private static boolean checkshouldDownload(UpdateInfo updateInfo, int versionCode) {
        try {
            if (updateInfo.getItem() == null) {
                return false;
            }

            if (!TextUtils.isEmpty(updateInfo.getItem().getVersion())) {
                if (Integer.parseInt(updateInfo.getItem().getVersion()) > versionCode) {
                    return !TextUtils.isEmpty(updateInfo.getItem().getDownloadUrl());
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
            UpdateInfo updateInfo = null;
            try {
                String string = response.body().string();
                updateInfo = new Gson().fromJson(string, UpdateInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            return updateInfo;
        }
    }
}
