package com.example.editor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

    ManagerHigh managerHigh;

    static int button_move = 280;// 底端button平移距离
    static final int button_id = 1234321;// 防id冲突

    static int window_num;// 调用哪个dialog/manager
    static int cur_num;// 当前窗口号
    static int total_num;// 总窗口号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("fuck", "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 重复操作无影响
        initFunc();
        initButton();

        clearData();// TODO 测试用

        // 恢复窗口数目
        SharedPreferences pNum = getSharedPreferences("num", MODE_PRIVATE);
        cur_num = pNum.getInt("cur_num", -1);
        total_num = pNum.getInt("total_num", 0);
        
        managerHigh.fuck(cur_num + " : " + total_num);// TODO

        // 从外部打开
        Intent intent = getIntent();
        managerHigh.outerOpen(intent);// 在内部判断是否是由外部打开
    }

    public void initFunc() {// 初始化函数,窗口等
        // 初始化窗口
        closeDialog = new CloseDialog();
        editDialog = new EditDialog();
        newManager = new NewManager();
        openManager = new OpenManager();
        saveManager = new SaveManager();

        // 初始化功能函数
        View view = getWindow().getDecorView().findViewById(android.R.id.content);// TODO
        managerHigh = new ManagerHigh(MainActivity.this, getExternalFilesDir("").getAbsolutePath(), view);

        // 检查权限
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int check_result = ActivityCompat.checkSelfPermission(this, permission);// `允许`返回0,`拒绝`返回-1
        if (check_result != PackageManager.PERMISSION_GRANTED) {// 没有`写`权限
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);// 获取`写`权限
        }
    }

    public void initButton() {// 按钮增加监听
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
                managerHigh.saveCursor();// 保存光标
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
                managerHigh.saveCursor();// 保存光标
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
                managerHigh.saveTemp();// 先要将内容更新至临时文件
                managerHigh.saveCursor();// 保存光标

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
                    closeDialog.show(getSupportFragmentManager(), "close");// 提示是否保存新建文件
                } else {// 有绑定的文件
                    if (managerHigh.diffFile(path, tempPath)) {// 两个文件不同
                        editDialog.show(getSupportFragmentManager(), "edit");// 提示是否保存
                    } else {
                        managerHigh.unlinkTempFile(tempPath);// 解除与`打开文件`的绑定
                        managerHigh.closeTab();// 直接关闭窗口
                    }
                }
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

    public void resClose() {// 关闭新建的文件
        if (closeDialog.result == 1) {// 选择`保存`
            newManager.show(getSupportFragmentManager(), "new");// 打开文件管理器
        } else if (closeDialog.result == 0) {// 关闭提示框
            return;
        } else if (closeDialog.result == -1) {// 直接删除新建的文件
            SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
            String tempPath = pTab.getString(cur_num + "", null);// TODO 必须非空
            managerHigh.unlinkTempFile(tempPath);
            managerHigh.closeTab();
        }
    }

    public void resEdit() {// 关闭已经存在的文件
        if (editDialog.result == 1) {// 选择`保存`
            SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
            String tempPath = pTab.getString(cur_num + "", null);// TODO 必须非空
            SharedPreferences pFile = getSharedPreferences("file", MODE_PRIVATE);
            String path = pFile.getString(tempPath, null);// TODO 必须非空
            managerHigh.writeFile(path);// 写入绑定的文件
            managerHigh.unlinkTempFile(tempPath);// 删除临时文件
            managerHigh.closeTab();// 关闭窗口
        } else if (editDialog.result == 0) {// 关闭提示框
            return;
        } else if (editDialog.result == -1) {// 删除临时文件,不改动绑定的文件
            SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
            String tempPath = pTab.getString(cur_num + "", null);// TODO 必须非空
            managerHigh.unlinkTempFile(tempPath);// 放弃修改
            managerHigh.closeTab();// 关闭窗口
        }
    }

    public void resNew() {
        if (newManager.result == 0) {// `取消`保存文件,并`取消`关闭文件
            return;
        } else if (newManager.result == 1) {// 保存文件,并将临时文件删除,关闭文件
            // 保存文件
            managerHigh.writeFile(newManager.path);// 写入新建的文件
            SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
            String tempPath = pTab.getString(cur_num + "", null);// TODO 必须非空
            // TODO 不需要解除绑定
            managerHigh.unlinkTempFile(tempPath);// 删除临时文件
            managerHigh.closeTab();
        }
    }

    public void resOpen() {// 主动打开文件
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

            // 更新文件名
            managerHigh.loadName();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        managerHigh.fuck("pause");

        managerHigh.saveNum();// 保存窗口号
        managerHigh.saveTemp();
        managerHigh.saveCursor();// 保存光标
    }

    @Override
    public void onResume() {
        super.onResume();
        managerHigh.fuck("resume");

        // 恢复被删除的临时文件
        managerHigh.checkFile();
        SharedPreferences pTab = getSharedPreferences("tab", MODE_PRIVATE);
        String tempPath = pTab.getString(cur_num + "", null);
        if (tempPath != null) {// 打开了文件
            managerHigh.readFile(tempPath);// 更新文件
            managerHigh.loadCursor();// 调整光标
        }
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

        // 清空光标位置
        SharedPreferences pCursor = getSharedPreferences("cursor", MODE_PRIVATE);
        editor = pCursor.edit();
        editor.clear();
        editor.commit();

        // 清空窗口数据
        SharedPreferences pNum = getSharedPreferences("num", MODE_PRIVATE);
        editor = pNum.edit();
        editor.clear();
        editor.commit();

        // TODO 其余测试
//        managerHigh.diffFile(managerHigh.appPath + "/temp0", managerHigh.appPath + "/temp1");
    }
}
