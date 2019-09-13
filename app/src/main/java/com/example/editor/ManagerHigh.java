package com.example.editor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ManagerHigh extends ManagerMid {

    public ManagerHigh (Context context, String appPath, View view) {
        super(context, appPath, view);

    }

    public int recoverTab() {// 重新打开软件时恢复所有窗口
        // TODO 如果打开的文件被删,直接将临时文件转换成新文件
        return -1;
    }

    public int saveTemp() {// 临时保存
        if (MainActivity.cur_num == -1) {// 没有打开文件
            return 1;
        }

        try {
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            String tempPath = pTab.getString(MainActivity.cur_num + "", null);// TODO 必须非null
            File tempFile = new File(tempPath);
            if (tempFile.exists() == false) {// 如果文件不存在则创建
                tempFile.createNewFile();// TODO 父文件夹不存在
            }
            writeFile(tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int checkFile() {// 在软件运行中恢复被删除的文件
        return -1;
    }

    public int outerOpen(Intent intent) {// 由其他应用打开的文件
        String action = intent.getAction();// 判断本软件启动的方式
        if (action.equals("android.intent.action.VIEW")) {// 由其他软件打开本软件
            Uri uri = intent.getData();

            // 获取文件地址
            String path = getPathFromUri(context, uri);// TODO 参数
            if (checkReopen(path)) {// 这个文件已经被打开过
                Toast.makeText(context, path + " already loaded", Toast.LENGTH_SHORT).show();
            }

            // 加载文件
            String tempPath = newTempFile();// 创建副本
            readFile(path);// 加载真实文件
            writeFile(tempPath);// 将文本框内容导入临时文件

            // 绑定临时文件
            SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pFile.edit();
            editor.putString(tempPath, path);
            editor.commit();
            loadTempFile(tempPath);// 包含加载文件名

            return 1;
        }

        return 0;
    }
}
