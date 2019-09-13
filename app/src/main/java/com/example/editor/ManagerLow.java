package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ManagerLow extends GetPath {

    Context context;
    String appPath;
    View view;

    public ManagerLow (Context context, String appPath, View view) {// 构造方法
        this.context = context;
        this.appPath = appPath;// 软件目录
        this.view = view;// 标签栏
    }

    public int unlinkTempFile(String tempPath) {// 从磁盘中删除文件并解绑
        // 解除绑定
        SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pFile.edit();
        editor.putString(tempPath, null);// 不管原来是什么,直接覆盖
        editor.commit();

        // 删除文件
        File tempFile = new File(tempPath);
        if (tempFile.exists() == false) {
            return 1;
        } else {
            tempFile.delete();
            return 0;
        }
    }

    public int readFile(String path) {// 读取文件到输入框
        if (path == null) {// TODO
            return -1;
        }

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

    public int saveNum() {// 保存当前窗口号,总窗口数
        SharedPreferences pNum = context.getSharedPreferences("num", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pNum.edit();
        editor.putInt("cur", MainActivity.cur_num);
        editor.putInt("total", MainActivity.total_num);
        editor.commit();

        fuck("save num: " + MainActivity.cur_num + "/" + MainActivity.total_num);

        return 0;
    }

    public int saveCursor() {// 保存光标位置
        if (MainActivity.cur_num == -1) {// 乱点按钮的情况
            return 1;
        }

        // 获取位置
        EditText text = view.findViewById(R.id.text_input);
        int position = text.getSelectionStart();// TODO end?

        // 保存位置
        SharedPreferences pCursor = context.getSharedPreferences("cursor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pCursor.edit();
        editor.putInt(MainActivity.cur_num + "", position);// 保存当前窗口的光标位置
        editor.commit();

        return 0;
    }

    public int loadCursor() {// 加载当前窗口的光标位置
        // 获取位置
        SharedPreferences pCursor = context.getSharedPreferences("cursor", Context.MODE_PRIVATE);
        int position = pCursor.getInt(MainActivity.cur_num + "", 0);// 如果没有的话返回0

        // 移动光标
        EditText text = view.findViewById(R.id.text_input);
        int text_len = text.getText().length();
        if (position > text_len) {// 文件遭到修改
            position = 0;
        }

        text.setSelection(position);

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

    public boolean checkReopen(String path) {// 检查是否重复打开文件
        if (path == null) {
            return false;
        }

        // 遍历所有已打开的文件名,查看是否有重复的
        String tempPath = null;
        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
        for (int i = 0; i < MainActivity.total_num ; i ++) {
            tempPath = pTab.getString(i + "", null);// TODO 必须非空
            String bindPath = pFile.getString(tempPath, null);
            if (bindPath == null) {// 新文件
                continue;
            } else if (bindPath.equals(path)) {// 有重复
                return true;
            }
        }

        return false;
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

    public boolean diffFile(String path_1, String path_2) {
        // 检查文件存在
        File file_1 = new File(path_1);
        File file_2 = new File(path_2);
        if ((!file_1.exists()) || (!file_2.exists())) {// 有某一个不存在则返回`文件不相同`
            fuck("not exists");
            return true;
        }

        // 比较文件大小
        int len_1 = (int) file_1.length();
        int len_2 = (int) file_2.length();
        if (len_1 != len_2) {
            fuck("length: " + len_1 + " != " + len_2);
            return true;
        }

        // 比较文件内容
        try {
            // 逐byte比较
            RandomAccessFile ra_1 = new RandomAccessFile(file_1, "r");
            RandomAccessFile ra_2 = new RandomAccessFile(file_2, "r");
            byte[] content_1 = new byte[1];
            byte[] content_2 = new byte[1];
            for (int i = 0; i < len_1 ; i ++) {
                ra_1.read(content_1, 0, 1);
                ra_2.read(content_2, 0, 1);
                if (content_1[0] != content_2[0]) {
                    fuck("content: " + content_1[0] + " != " + content_2[0]);
                    return true;
                }
            }

            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String newTempFile() {// 在app目录新建临时文件
        try {
            String tempPath = null;
            File tempFile = null;
            for (int i = 0; i < 1000; i++) {
                shit(i < 100);
                tempPath = appPath + "/temp" + i;// 临时文件名
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
}
