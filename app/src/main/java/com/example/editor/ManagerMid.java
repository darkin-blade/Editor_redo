package com.example.editor;

import android.content.Context;

import java.io.File;

public class ManagerMid extends ManagerLow {

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
        return null;
    }

    public int changeTab() {
        return -1;
    }
}
