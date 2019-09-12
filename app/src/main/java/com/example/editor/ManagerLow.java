package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

public class ManagerLow {

    public int unlinkTempFile(String tempPath) {// 删除临时文件,并解除其文件绑定
        return -1;
    }

    public int readFile(String path, EditText text) {// 读取文件到输入框
        return -1;
    }

    public int writeFile(String path, EditText text) {// 将输入框内容写入文件
        return -1;
    }

    public int saveTemp() {// 临时保存
        return -1;
    }

    public int saveNum() {// 保存数字
        return -1;
    }

    public int saveCursor() {// 保存光标位置
        return -1;
    }

    public void fuck(String log) {
        Log.i("fuck", log);
    }
}
