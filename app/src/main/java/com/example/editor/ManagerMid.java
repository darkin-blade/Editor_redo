package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

public class ManagerMid extends ManagerLow {

    public ManagerMid (Context context, String appPath, View view) {
        super(context, appPath, view);
    }

    public int loadTempFile(String tempPath) {// 将临时文件加载到tab
        fuck("loadTempFile");
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
            editor.commit();

            // 新建tab
            Button btn = new Button(context);
            btn.setBackgroundResource(R.drawable.tab_notactive);// 置为不活跃
            btn.setLayoutParams(new LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.MATCH_PARENT));// 调整tab大小
            btn.setPadding(0, 0, 0, 0);
            btn.setId(MainActivity.button_id + MainActivity.total_num);// 添加tab标号
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveTemp();// 保存原窗口
                    changeTab(view.getId() - MainActivity.button_id);
                }
            });

            // 添加tab
            LinearLayout tabs = view.findViewById(R.id.file_tab);
            tabs.addView(btn);// 添加到标签栏

            // 加载文件
            readFile(tempPath);// TODO 光标位置

            // 切换至新标签
            changeTab(MainActivity.total_num);
            MainActivity.cur_num = MainActivity.total_num;
            MainActivity.total_num ++;
            loadName();// tab显示文件名

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int unlinkTempFile(String tempPath) {// 从磁盘中删除文件并解绑
        // 解除绑定
        SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pFile.edit();
        editor.putString(tempPath, null);// 解除绑定

        // 删除文件
        File tempFile = new File(tempPath);
        if (tempFile.exists() == false) {
            return 1;
        } else {
            tempFile.delete();
            return 0;
        }
    }

    public int closeTab() {// 关闭当前窗口,调整tab与临时文件的绑定
        if (MainActivity.cur_num == -1) {// TODO 提示
            return 1;
        }

        // 删除当前tab
        Button btn = view.findViewById(MainActivity.cur_num + MainActivity.button_id);
        LinearLayout layout = view.findViewById(R.id.file_tab);// 窗口栏
        layout.removeView(btn);

        // 移动其余的tab
        String tempPath = null;
        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pTab.edit();
        if (MainActivity.cur_num + 2 <= MainActivity.total_num) {// 不是最后一个tab,且当前总tab >= 2
            for (int i = MainActivity.cur_num + 1; i < MainActivity.total_num ; i ++) {
                // 修改tab与临时文件的绑定
                tempPath = pTab.getString(i + "", null);// TODO 必须非空
                editor.putString((i - 1) + "", tempPath);

                // 修改tab的id
                btn = view.findViewById(i + MainActivity.button_id);
                btn.setId(i - 1 + MainActivity.button_id);
            }
        } else {// 是最后一个tab TODO 不管当前打开文件的数目
            editor.putString(MainActivity.cur_num + "", null);// 解除tab的绑定
            MainActivity.cur_num --;// TODO 会现将该窗口置为不活跃
        }
        editor.commit();
        MainActivity.total_num --;
        changeTab(MainActivity.cur_num);// 切换至临近窗口

        return -1;
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

    public int changeTab(int next_num) {// 切换标签页
        fuck(MainActivity.cur_num + "=>" + next_num);

        // 将上一窗口置为不活跃
        Button tabLast = view.findViewById(MainActivity.button_id + MainActivity.cur_num);
        if (tabLast != null) {
            tabLast.setBackgroundResource(R.drawable.tab_notactive);
        }

        // 将下一窗口置为活跃
        Button tabNext = view.findViewById(MainActivity.button_id + next_num);
        if (tabNext == null) {// 关闭最后一个tab时
            EditText text = view.findViewById(R.id.text_input);
            text.setText("");// 清空

            return 1;
        }
        tabNext.setBackgroundResource(R.drawable.tab_active);

        // 加载下一窗口的内容
        try {
            MainActivity.cur_num = next_num;
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            String tempPath = pTab.getString(MainActivity.cur_num + "", null);// TODO 必须非空
            File tempFile = new File(tempPath);
            if (tempFile.exists() == false) {// TODO 不存在则新建
                tempFile.createNewFile();
            }
            readFile(tempPath);

            // TODO 加载光标位置

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int loadName() {// 显示当前窗口的文件名
        // 获取路径
        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        String tempPath = pTab.getString(MainActivity.cur_num + "", null);// TODO 必须非空

        // 获取绑定的对应文件
        SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
        String path = pFile.getString(tempPath, null);

        // 加载文件名
        Button curTab = view.findViewById(MainActivity.cur_num + MainActivity.button_id);
        if (path == null) {// 临时文件
            File tempFile = new File(tempPath);
            curTab.setText(tempFile.getName());
        } else {// 有绑定文件
            File file = new File(path);// TODO 文件不存在
            curTab.setText(file.getName());
        }

        return 0;
    }
}
