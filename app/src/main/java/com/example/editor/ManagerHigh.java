package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;

public class ManagerHigh extends ManagerMid {
    Context context;

    public ManagerHigh(Context context) {
        this.context = context;
    }

    public int recoverTab() {// 重新打开软件时恢复所有窗口
        // TODO 如果打开的文件被删,直接将临时文件转换成新文件
        return -1;
    }

    public int saveTemp() {// 临时保存
        if (MainActivity.cur_num == -1) {// 没有打开文件
            return 1;
        }

        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        String tempPath = pTab.getString(MainActivity.cur_num + "", null);// TODO 必须非null
        File tempFile = new File(tempPath);
        if (tempFile.exists() == false) {// 如果文件不存在则创建
            try {
                tempFile.createNewFile();// TODO 父文件夹不存在
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int checkFile() {// 在软件运行中恢复被删除的文件
        return -1;
    }
}
