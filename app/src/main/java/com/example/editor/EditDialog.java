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
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class EditDialog extends DialogFragment {// 文件被修改后提示是否保存更改
    public Button yes;
    public Button cancel;
    public Button no;
    public TextView attention;// 提示框

    public int result;

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        super.show(fragmentManager, tag);
        Editor.window_num = 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme);// 关闭背景(点击外部不能取消)
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("fuck", "on create view");
        View view = inflater.inflate(R.layout.layout_dialog, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));// 背景透明

        // 绑定按钮事件
        initButton(view);
        return view;
    }

    private void initButton(View view) {
        yes = view.findViewById(R.id.yes_button);
        cancel = view.findViewById(R.id.cancel_button);
        no = view.findViewById(R.id.no_button);
        attention = view.findViewById(R.id.attention);

        yes.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View view) {
                result = 1;
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = 0;
                dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = -1;
                dismiss();
            }
        });

        attention.setText("The file has been changed:");
    }
}