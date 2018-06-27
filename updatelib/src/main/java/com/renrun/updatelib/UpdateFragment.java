package com.renrun.updatelib;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;

/**
 * Created by vice on 2017/10/9 16:51
 * E-mail:vicedev1001@gmail.com
 */

public class UpdateFragment extends DialogFragment {
    private static final String UPDATEINFO = "updateinfo";
    private static final String FILENAME = "filename";
    private ProgressBar pbProgress;
    private TextView tvUpdateMsg;
    private Button btnCancel;
    private Button btnUpdate;
    private RequestCall downloadCall;
    private TextView tvVersion;
    private TextView tvSize;

    public static UpdateFragment newInstance(UpdateInfo updateInfo, String fileName) {
        UpdateFragment updateFragment = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(UPDATEINFO, updateInfo);
        bundle.putSerializable(FILENAME, fileName);
        updateFragment.setArguments(bundle);
        return updateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = View.inflate(getActivity(), R.layout.fragment_update, null);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        pbProgress = (ProgressBar) rootView.findViewById(R.id.pb_progress);
        tvUpdateMsg = (TextView) rootView.findViewById(R.id.tv_update_msg);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnUpdate = (Button) rootView.findViewById(R.id.btn_update);
        tvVersion = (TextView) rootView.findViewById(R.id.tv_version);
        tvSize = (TextView) rootView.findViewById(R.id.tv_size);

        final UpdateInfo updateInfo = (UpdateInfo) getArguments().get(UPDATEINFO);
        tvUpdateMsg.setText(updateInfo.getItem().getUpdateMsg());

        final String fileName = getArguments().getString(FILENAME);

        final String force = updateInfo.getItem().getForce();

        //支持强制更新
        setCancelable(false);

        //版本号
        if (!TextUtils.isEmpty(updateInfo.getItem().getVersionName())) {
            tvVersion.setText(String.format("最新版本：%s", updateInfo.getItem().getVersionName()));
        }
        //apk大小
        if (!TextUtils.isEmpty(updateInfo.getItem().getFilesize())) {
            tvSize.setText(String.format("    大小：%sM", new DecimalFormat("#.00").format(Double.parseDouble(updateInfo.getItem().getFilesize()) / 1024 / 1024)));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadCall != null) {
                    downloadCall.cancel();
                }
                dismiss();
                //如果
                if (!TextUtils.isEmpty(force) && !"0".equals(force)) {
                    //获取PID
                    android.os.Process.killProcess(android.os.Process.myPid());
                    //常规java、c#的标准退出法，返回值为0代表正常退出
                    System.exit(0);
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndPermission.with(getActivity())
                        .requestCode(300)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                                AlertDialog.newBuilder(getActivity())
                                        .setTitle("温馨提醒")
                                        .setMessage("没有这个权限没有办法使用App更新功能哟~")
                                        .setPositiveButton("给你权限", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                rationale.resume();
                                            }
                                        })
                                        .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                rationale.cancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                String apkName = fileName.endsWith(".apk") ? fileName : fileName + ".apk";
                                btnUpdate.setEnabled(false);
                                pbProgress.setVisibility(View.VISIBLE);
                                downloadApk(updateInfo.getItem().getDownloadUrl(), apkName);
                            }

                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                Toast.makeText(getActivity(), "拒绝权限将无法使用更新功能~", Toast.LENGTH_LONG).show();
                            }
                        })
                        .start();
            }
        });

    }

    private void downloadApk(String downloadUrl, String fileName) {
        downloadCall = OkHttpUtils
                .post()
                .addHeader("Accept-Encoding", "identity")//不加这个的话contentLength一直拿到是-1,这样progress会出问题
                .url(downloadUrl)
                .build();

        downloadCall.execute(new FileCallBack(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText("点击重试");
            }

            @Override
            public void onResponse(File response, int id) {
                if (response != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = VersionFileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".versionProvider", new File(response.getAbsolutePath()));
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(response), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
                dismiss();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                Log.e("progress-->", progress + "-->" + progress / total + "-->" + total);
                pbProgress.setProgress((int) (100 * progress));
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //设置全屏
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
    }
}
