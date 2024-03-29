package com.example.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    public int loadTempFile(String tempPath) {// 将临时文件加载到tab,会切换标签
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
            editor.putString(Editor.total_num + "", tempPath);// 绑定临时文件到窗口
            editor.commit();

            // 新建tab
            Button btn = new Button(context);
            btn.setTextColor(Color.argb(0xff, 0x90, 0x90, 0x90));
            btn.setBackgroundResource(R.drawable.tab_notactive);// 置为不活跃
            btn.setId(Editor.button_id + Editor.total_num);// 添加tab标号
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveTemp();// 保存原窗口
                    saveCursor();// 保存光标
                    changeTab(view.getId() - Editor.button_id);
                }
            });

            // TODO 修改tab的margin和大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(-20, 0, 0, 0);
            btn.setLayoutParams(layoutParams);// 调整tab大小
            btn.setPadding(0, 0, 0, 0);

            // 添加tab
            LinearLayout tabs = view.findViewById(R.id.file_tab);
            tabs.addView(btn);// 添加到标签栏

            // 加载文件
            readFile(tempPath);// TODO 光标位置

            // 切换至新标签
            changeTab(Editor.total_num);
            Editor.cur_num = Editor.total_num;
            Editor.total_num ++;
            loadName();// tab显示文件名

            // TODO 如果是加载的第一个文件,那么将输入框置为可编辑
            if (Editor.total_num == 1) {
                EditText text = view.findViewById(R.id.text_input);
                text.setEnabled(true);
            }

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int closeTab() {// 关闭当前窗口,调整tab与临时文件的绑定
        if (Editor.cur_num == -1) {// TODO 提示
            return 1;
        }

        // 删除当前tab
        Button btn = view.findViewById(Editor.cur_num + Editor.button_id);
        LinearLayout layout = view.findViewById(R.id.file_tab);// 窗口栏
        layout.removeView(btn);

        // 移动其余的tab
        String tempPath = null;
        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        SharedPreferences.Editor eTab = pTab.edit();// 修改临时文件和窗口的绑定
        SharedPreferences pCursor = context.getSharedPreferences("cursor", Context.MODE_PRIVATE);
        SharedPreferences.Editor eCursor = pCursor.edit();// 修改窗口的光标位置的记录
        int position = 0;
        if (Editor.cur_num + 2 <= Editor.total_num) {// 不是最后一个tab,且当前总tab >= 2
            for (int i = Editor.cur_num + 1; i < Editor.total_num ; i ++) {
                // 修改tab与临时文件的绑定
                tempPath = pTab.getString(i + "", null);// TODO 必须非空
                eTab.putString((i - 1) + "", tempPath);

                // 修改tab的光标位置
                position = pCursor.getInt(i + "", 0);
                eCursor.putInt((i - 1) + "", position);

                // 修改tab的id
                btn = view.findViewById(i + Editor.button_id);
                btn.setId(i - 1 + Editor.button_id);
            }

            // 最后一个tab清空
            eTab.putString((Editor.total_num - 1) + "", null);
            eCursor.putInt((Editor.total_num - 1) + "", 0);
        } else {// 是最后一个tab TODO 不管当前打开文件的数目
            eTab.putString(Editor.cur_num + "", null);// 解除tab的绑定
            eCursor.putInt(Editor.cur_num + "", 0);// 光标置为初始处
            Editor.cur_num --;// TODO 会现将该窗口置为不活跃
        }

        // 保存更改
        eTab.commit();
        eCursor.commit();
        Editor.total_num --;
        changeTab(Editor.cur_num);// 切换至临近窗口

        // 当关闭最后一个窗口后将输入框设置为不可编辑
        if (Editor.total_num == 0) {
            EditText text = view.findViewById(R.id.text_input);
            text.setEnabled(false);// 不可输入
        }

        return 0;
    }

    public int changeTab(int next_num) {// 切换标签页,会加载光标
        // 将上一窗口置为不活跃
        Button tabLast = view.findViewById(Editor.button_id + Editor.cur_num);
        if (tabLast != null) {
            tabLast.setBackgroundResource(R.drawable.tab_notactive);
        }

        // 将下一窗口置为活跃
        Button tabNext = view.findViewById(Editor.button_id + next_num);
        if (tabNext == null) {// 关闭最后一个tab时
            EditText text = view.findViewById(R.id.text_input);
            text.setText("");// 清空

            return 1;
        }
        tabNext.setBackgroundResource(R.drawable.tab_active);

        // 加载下一窗口的内容
        try {
            Editor.cur_num = next_num;
            SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
            String tempPath = pTab.getString(Editor.cur_num + "", null);// TODO 必须非空
            File tempFile = new File(tempPath);
            if (tempFile.exists() == false) {// TODO 不存在则新建
                tempFile.createNewFile();
            }
            readFile(tempPath);

            loadCursor();// TODO 加载光标位置

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int loadName() {// 显示当前窗口的文件名
        // 获取路径
        SharedPreferences pTab = context.getSharedPreferences("tab", Context.MODE_PRIVATE);
        String tempPath = pTab.getString(Editor.cur_num + "", null);// TODO 必须非空

        // 获取绑定的对应文件
        SharedPreferences pFile = context.getSharedPreferences("file", Context.MODE_PRIVATE);
        String path = pFile.getString(tempPath, null);

        // 加载文件名
        Button curTab = view.findViewById(Editor.cur_num + Editor.button_id);
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
