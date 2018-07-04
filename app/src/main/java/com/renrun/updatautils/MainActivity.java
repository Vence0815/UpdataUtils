package com.renrun.updatautils;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.renrun.updatelib.UpdateUtils;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTest = findViewById(R.id.btn_test);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdateUtils.checkUpdate(MainActivity.this, "http://demo3.renrunkeji.com:8083/index_wx.php/App/version",
//                        1, "1", "a6c9d4e334e87b47dc326daf031ba44e", true, "test.apk");
//            }
//        });
    }
}
