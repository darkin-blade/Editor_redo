package com.example.editor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    CloseDialog closeDialog;// 0
    EditDialog editDialog;// 1
    NewManager newManager;// 2
    OpenManager openManager;// 3
    SaveManager saveManager;// 4
    static int window_num;// 调用哪个窗口

    ManagerHigh managerHigh;

    static int button_move = 280;// 底端button平移距离
    static final int button_id = 1234321;// 防id冲突

    static int cur_num;// 当前窗口号
    static int total_num;// 总窗口号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParam();
        initButton();

        // 从外部打开
        Intent intent = getIntent();
        managerHigh.outerOpen(intent);// 在内部判断是否是由外部打开

        File file1 = new File("/a/.././././a");
        File file2 = new File("/a/a/..");
        managerHigh.fuck((file1 == file2) + "");
    }

    public void initParam() {// 初始化窗口和功能函数
        clearData();// TODO 测试用

        // 初始化窗口
        closeDialog = new CloseDialog();
        editDialog = new EditDialog();
        newManager = new NewManager();
        openManager = new OpenManager();
        saveManager = new SaveManager();
        window_num = -1;

        // 初始化功能函数
        View view = getWindow().getDecorView().findViewById(android.R.id.content);// TODO
        managerHigh = new ManagerHigh(MainActivity.this, getExternalFilesDir("").getAbsolutePath(), view);

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
                // 获取当前页面临时文件路径
                SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
                String tempPath = pTab.getString(cur_num + "", null);
                if (tempPath == null) {// 没有打开文件
                    Toast.makeText(MainActivity.this, "you don't open any file", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 获取绑定文件的路径
                SharedPreferences pFile = getSharedPreferences("file", MODE_PRIVATE);
                String path = pFile.getString(tempPath, null);
                if (path == null) {// 新建的文件
                    saveManager.show(getSupportFragmentManager(), "save");
                } else {// 有绑定的文件
                    managerHigh.writeFile(path);// TODO 比较两个文件
                }
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
        managerHigh.fuck("resOpen");
        if (openManager.result == 0) {// `取消`打开文件
            return;
        } else {
            // 检查文件是否重复打开
            if (managerHigh.checkReopen(openManager.path)) {
                Toast.makeText(this, openManager.path + " already loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            // 加载临时文件
            String tempPath = managerHigh.newTempFile();// 新建临时文件
            managerHigh.readFile(openManager.path);// 读取到输入框
            managerHigh.writeFile(tempPath);// 将输入框中内容写入临时文件

            // 绑定文件
            SharedPreferences pFile = getSharedPreferences("file", MODE_PRIVATE);
            SharedPreferences.Editor editor = pFile.edit();
            editor.putString(tempPath, openManager.path);
            editor.commit();

            managerHigh.loadTempFile(tempPath);// 加载临时文件
        }
    }

    public void resSave() {// 主动保存
        if (saveManager.result == 0) {// 取消保存
            return;
        } else {
            // 将内容写入新的文件
            managerHigh.writeFile(saveManager.path);

            // 绑定文件
            SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
            String tempPath = pTab.getString(cur_num + "", null);// TODO 必须非空
            SharedPreferences pFile = getSharedPreferences("file", MODE_PRIVATE);// 绑定文件
            SharedPreferences.Editor editor = pFile.edit();
            editor.putString(tempPath, saveManager.path);
            editor.commit();
        }
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

    private void clearData() {
        SharedPreferences.Editor editor = null;

        // 清空窗口和临时文件的绑定
        SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
        editor = pTab.edit();
        editor.clear();
        editor.commit();

        // 清空临时文件和`文件`的绑定
        SharedPreferences pFile = getSharedPreferences("file", MODE_PRIVATE);
        editor = pFile.edit();
        editor.clear();
        editor.commit();
    }
}
