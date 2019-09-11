package com.example.editor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    CloseDialog closeDialog;// 0
    EditDialog editDialog;// 1
    NewManager newManager;// 2
    OpenManager openManager;// 3
    SaveManager saveManager;// 4
    static int window_num;// 调用哪个窗口

    ManagerLow managerLow;
    ManagerMid managerMid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 总初始化
        initMain();

        // 检查权限
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int check_result = ActivityCompat.checkSelfPermission(this, permission);// `允许`返回0,`拒绝`返回-1
        if (check_result != PackageManager.PERMISSION_GRANTED) {// 没有`写`权限
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);// 获取`写`权限
        }
    }

    public void initMain() {// 初始化窗口和功能函数


        // 初始化窗口
        closeDialog = new CloseDialog();
        editDialog = new EditDialog();
        newManager = new NewManager();
        openManager = new OpenManager();
        saveManager = new SaveManager();
        window_num = -1;

        // 初始化功能函数
        managerLow = new ManagerLow(MainActivity.this);
        managerMid = new ManagerMid(MainActivity.this);
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
    }

    public void resSave() {
    }
}
