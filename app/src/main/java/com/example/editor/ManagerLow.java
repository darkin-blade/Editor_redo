package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ManagerLow {

    Context context;
    EditText text;
    String appPath;

    public ManagerLow (Context context, EditText text, String appPath) {// 构造方法
        this.context = context;
        this.text = text;
        this.appPath = appPath;// 软件目录
    }

    public int unlinkTempFile(String tempPath) {// 删除临时文件,并解除其文件绑定
        return -1;
    }

    public int readFile(String path, EditText text) {// 读取文件到输入框
        return -1;
    }

    public int writeFile(String path) {// 将输入框内容写入文件
        try {
            // 检查被写文件是否存在
            File file = new File(path);
            if (file.exists() == false) {
                file.createNewFile();// TODO 父目录不存在
            }

            // 计算长度
            String content = text.getText().toString();
            int origin_len = (int) file.length();// 被写文件的大小
            int new_len = content.getBytes().length;// 写入的字符串大小
            if (origin_len > new_len) {// 删除原文件
                file.delete();
                file.createNewFile();
            }

            // 写入文件
            RandomAccessFile raFile = new RandomAccessFile(file, "rw");
            raFile.write(content.getBytes());
            raFile.close();
            
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

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
