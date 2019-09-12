package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

public class ManagerMid extends ManagerLow {

    public ManagerMid (Context context, EditText text, String appPath, LinearLayout tabs) {
        super(context, text, appPath, tabs);
    }

    public int loadTempFile(String tempPath) {// 将临时文件加载到tab
        try {
            // 检查文件存在
            File tempFile = new File(tempPath);
            if (tempFile.exists() == false) {// TODO 文件不存在则创建
                tempFile.createNewFile();
            }

            // 绑定窗口
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pTab.edit();
            editor.putString(MainActivity.total_num + "", tempPath);// 绑定临时文件到窗口
            MainActivity.cur_num = MainActivity.total_num;
            MainActivity.total_num ++;

            // 添加tab
            Button btn = new Button(context);
            btn.setBackgroundResource(R.drawable.tab_notactive);// 置为不活跃
            btn.setLayoutParams(new LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.MATCH_PARENT));// 调整tab大小
            btn.setPadding(0, 0, 0, 0);
            btn.setId(MainActivity.button_id + MainActivity.total_num);// 添加tab标号
        } catch (IOException e) {
            e.printStackTrace();
        }
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
