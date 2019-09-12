package com.example.editor;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;

public class OpenManager extends DialogFragment implements FileBroswer {// 打开文件
    public Button yes;
    public Button cancel;
    public int result;
    public String path;// 文件路径
    public File file;// 文件

    int item_height = 130;
    int type_padding = 20;
    int name_padding = 40;

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        super.show(fragmentManager, tag);
        MainActivity.window_num = 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_open, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        // 绑定按钮事件
        initButton(view);

        // TODO 文件管理器
        readPath("", view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme);// 关闭背景(点击外部不能取消)
    }

    private void initButton(View view) {
        cancel = view.findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = 0;
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void readPath(final String dirPath, View view) {
        LinearLayout item = null;
        item = createItem(0, "file", view);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        item = createItem(1, "dir", view);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private LinearLayout createItem(int itemType, String itemName, View view) {// 创建图标
        LinearLayout layout = view.findViewById(R.id.item_list);
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, item_height);
        LinearLayout.LayoutParams typeParam = new LinearLayout.LayoutParams(item_height, item_height);
        LinearLayout.LayoutParams iconParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams nameParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout item = new LinearLayout(getContext());// TODO 参数
        item.setLayoutParams(itemParam);
        item.setBackgroundResource(R.color.grey);
        item.setPadding(name_padding, 0, 0, 0);

        LinearLayout type = new LinearLayout(getContext());// 图标的外圈
        type.setLayoutParams(typeParam);
        type.setPadding(type_padding, type_padding, type_padding, type_padding);

        View icon = new View(getContext());// 图标
        icon.setLayoutParams(iconParam);
        if (itemType == 0) {// 文件
            icon.setBackgroundResource(R.drawable.item_file);
        } else {// 文件夹
            icon.setBackgroundResource(R.drawable.item_dir);
        }

        TextView name = new TextView(getContext());// 文件名
        name.setLayoutParams(nameParam);
        name.setBackgroundResource(R.color.grey);
        name.setText(itemName);
        name.setPadding(name_padding, name_padding, name_padding, name_padding);

        type.addView(icon);
        item.addView(type);
        item.addView(name);
        layout.addView(item);

        return item;
    }
}