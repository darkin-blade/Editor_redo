package com.example.editor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    CloseDialog closeDialog;// 0
    EditDialog editDialog;// 1
    NewManager newManager;// 2
    OpenManager openManager;// 3
    SaveManager saveManager;// 4
    static int window_num;// 调用哪个窗口

    ManagerHigh managerHigh;

    int button_move = 280;// 底端button平移距离
    final int button_id = 1234321;// 防id冲突

    static int cur_num;// 当前窗口号
    static int total_num;// 总窗口号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParam();
        initButton();
    }

    public void initParam() {// 初始化窗口和功能函数
        // 初始化窗口
        closeDialog = new CloseDialog();
        editDialog = new EditDialog();
        newManager = new NewManager();
        openManager = new OpenManager();
        saveManager = new SaveManager();
        window_num = -1;

        // 初始化功能函数
        EditText text= findViewById(R.id.text_input);
        managerHigh = new ManagerHigh(MainActivity.this, text, getExternalFilesDir(".").getAbsolutePath() + "/");

        // 初始化窗口号
        cur_num = -1;
        total_num = 0;

        // 检查权限
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int check_result = ActivityCompat.checkSelfPermission(this, permission);// `允许`返回0,`拒绝`返回-1
        if (check_result != PackageManager.PERMISSION_GRANTED) {// 没有`写`权限
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);// 获取`写`权限
        }
    }

    public void initButton() {
        // 控制`隐藏/显示`按钮
        Button btnCtrl = findViewById(R.id.ctrlButton);
        btnCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            }
        });

        // `新建`按钮
        Button btnNew = findViewById(R.id.newButton);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerHigh.saveTemp();
                String tempPath = managerHigh.newTempFile();
                managerHigh.loadTempFile(tempPath);
            }
        });

        // `打开`按钮
        Button btnOpen = findViewById(R.id.openButton);
        btnOpen.setOnClickListener(new View.OnClickListener() {// 点击`打开`按钮
            @Override
            public void onClick(View view) {
                managerHigh.saveTemp();
                openManager.show(getSupportFragmentManager(), "open");
            }
        });

        // `保存`按钮
        Button btnSave = findViewById(R.id.saveButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // `关闭`按钮
        Button btnClose = findViewById(R.id.closeButton);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        switch (window_num) {
            case 0:
                resClose();
                break;
            case 1:
                resEdit();
                break;
            case 2:
                resNew();
                break;
            case 3:
                resOpen();
                break;
            case 4:
                resSave();
        }
    }

    public void resClose() {
    }

    public void resEdit() {
    }

    public void resNew() {
    }

    public void resOpen() {
        if (openManager.result == 0) {// `取消`打开文件
            return;
        } else {
            String tempPath = managerHigh.newTempFile();// 新建临时文件
            managerHigh.readFile()
        }
    }

    public void resSave() {
    }

    @Override
    public void onPause() {
        super.onPause();
        managerHigh.fuck("pause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        managerHigh.fuck("destroy");
    }
}
