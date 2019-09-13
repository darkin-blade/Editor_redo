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
        fuck("recover tab");

        // 需要将窗口数目临时置为初始值
        SharedPreferences pNum = context.getSharedPreferences("num", Context.MODE_PRIVATE);
        int cur_backup = pNum.getInt("cur", -1);
        int total_backup = pNum.getInt("total", 0);
        MainActivity.cur_num = -1;
        MainActivity.total_num = 0;
        fuck(cur_backup + "/" + total_backup);

        // 逐个打开文件
        try {
            String tempPath = null;
            String path = null;
            File tempFile = null;
            File file = null;
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
            SharedPreferences.Editor eFile = pFile.edit();
            for (int i = 0; i < total_backup ; i ++) {
                // 检查临时文件
                tempPath = pTab.getString(i + "", null);// 获取对应临时文件路径 TODO 必须非空
                tempFile = new File(tempPath);
                if (tempFile.exists() == false) {// 没有就恢复
                    tempFile.createNewFile();// TODO 父文件夹不存在
                }

                // 加载文件
                loadTempFile(tempPath);// 如果有绑定的话,会修改tab名,会加载光标位置
                path = pFile.getString(tempPath, null);
                file = new File(path);
                if (file.exists() == false) {// 绑定的文件不存在了,解除绑定
                    eFile.putString(tempPath, null);
                }
            }
            eFile.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 切换窗口
        MainActivity.cur_num = cur_backup;
        MainActivity.total_num = total_backup;

        return 0;
    }

    public int checkFile() {// TODO 在软件运行中恢复被删除的(临时)文件
        try {
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            String tempPath = null;
            File tempFile = null;
            for (int i = 0; i < MainActivity.total_num ; i ++) {
                tempPath = pTab.getString(i + "", null);// TODO 必须非空
                tempFile = new File(tempPath);
                if (tempFile.exists() == false) {// 被删掉了
                    tempFile.createNewFile();
                }
            }

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int outerOpen(Intent intent) {// TODO 由其他应用打开的文件
        fuck("outer open");
        Uri uri = intent.getData();

        // 获取文件地址
        String path = getPathFromUri(context, uri);// TODO 参数
        if (checkReopen(path)) {// 这个文件已经被打开过
            Toast.makeText(context, path + " already loaded", Toast.LENGTH_SHORT).show();
            return -1;
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

        return 0;
    }
}
