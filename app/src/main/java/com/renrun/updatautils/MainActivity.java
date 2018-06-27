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
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUtils.checkUpdate(MainActivity.this, "http://www.rongzj.com/index_wx.php/App/version",
                        1, "2", "c50bccef02f6265945a5202a2ebd2042", true, "test.apk");
            }
        });
    }
}
