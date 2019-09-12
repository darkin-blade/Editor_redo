package com.example.editor;

import android.content.Context;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

public class ManagerMid extends ManagerLow {

    public ManagerMid (Context context, EditText text, String appPath) {
        super(context, text, appPath);
    }

    public int loadTempFile(String tempPath) {// 将临时文件加载到tab
        return -1;
    }

    public int unlinkTempFile(String tempPath) {// 从磁盘中删除文件并解绑
        return -1;
    }

    public int closeTab() {// 关闭当前窗口
        return -1;
    }

    public String newTempFile() {// 在app目录新建临时文件
        try {
            String tempPath = null;
            File tempFile = null;
            for (int i = 0; i < 1000; i++) {
                shit(i < 100);
                tempPath = appPath + "temp" + i;// 临时文件名
                tempFile = new File(tempPath);
                if (tempFile.exists() == false) {// 找到合适的文件名
                    break;
                }
            }
            tempFile.createNewFile();//

            return tempPath;// 返回临时文件的绝对路径
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int changeTab() {
        return -1;
    }
}
