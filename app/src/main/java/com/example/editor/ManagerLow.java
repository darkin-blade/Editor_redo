package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ManagerLow {

    Context context;
    String appPath;
    View view;

    public ManagerLow (Context context, String appPath, View view) {// 构造方法
        this.context = context;
        this.appPath = appPath;// 软件目录
        this.view = view;// 标签栏
    }

    public int unlinkTempFile(String tempPath) {// 删除临时文件,并解除其文件绑定
        return -1;
    }

    public int readFile(String path) {// 读取文件到输入框
        // TODO path == null
        try {
            // 检查文件
            File file = new File(path);
            if (file.exists() == false) {// 如果不存在就创建
                file.createNewFile();
            }
            int file_len = (int) file.length();
            shit(file_len + 16 > 0);// 检查溢出
            byte[] file_content = new byte[file_len];// 存储文件内容

            // 读取文件内容
            RandomAccessFile raFile = new RandomAccessFile(file, "r");
            raFile.read(file_content);

            // 将byte转成char
            Charset charset = Charset.forName("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.allocate(file_len);
            byteBuffer.put(file_content);
            byteBuffer.flip();
            CharBuffer charBuffer = charset.decode(byteBuffer);

            // 加载内容
            EditText text = view.findViewById(R.id.text_input);
            text.setText(charBuffer.array(), 0, charBuffer.length());

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            EditText text = view.findViewById(R.id.text_input);
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

    public void panic(String log) {
        new AssertionError(log);
    }

    public void shit(boolean b) {
        if (b == false) {
            panic("fuck");
        }
    }
}
