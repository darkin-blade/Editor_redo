package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class ManagerLow {
    Context context;

    public ManagerLow(Context context) {
        this.context = context;
    }

    public int unlinkTempFile(String tempPath) {// 删除临时文件,并解除其文件绑定
        SharedPreferences pFile = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        SharedPreferences.Editor eFile = pFile.edit();
        eFile.putString(tempPath, null);// 解除临时文件与文件的绑定
        // TODO
        return -1;
    }

    public int readFile(String path, EditText text) {
        return -1;
    }

    public int writeFile(String path, EditText text) {
        return -1;
    }
}
