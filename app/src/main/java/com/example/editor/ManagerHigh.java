package com.example.editor;

import android.content.Context;

public class ManagerHigh extends ManagerMid {
    Context context;

    public ManagerHigh(Context context) {
        this.context = context;
    }

    public int recoverTab() {// 重新打开软件时恢复所有窗口
        // TODO 如果打开的文件被删,直接将临时文件转换成新文件
        return -1;
    }

    public int checkFile() {// 在软件运行中恢复被删除的文件
        return -1;
    }
}
