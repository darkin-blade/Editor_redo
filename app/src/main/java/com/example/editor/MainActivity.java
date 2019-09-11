package com.example.editor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    CloseDialog closeDialog;// 0
    EditDialog editDialog;// 1
    NewManager newManager;// 2
    OpenManager openManager;// 3
    SaveManager saveManager;// 4
    static int window_num;// 调用哪个窗口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        closeDialog = new CloseDialog();
        editDialog = new EditDialog();
        newManager = new NewManager();
        openManager = new OpenManager();
        saveManager = new SaveManager();
        window_num = -1;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        switch (window_num) {
            case 0:
                resClose();
                break;
            case 1:
                resEdit();
                break;
            case 2:
                resNew();
                break;
            case 3:
                resOpen();
                break;
            case 4:
                resSave();
        }
    }

    public int resClose() {
        return -1;
    }

    public int resEdit() {
        return -1;
    }

    public int resNew() {
        return -1;
    }

    public int resOpen() {
        return -1;
    }

    public int resSave() {
        return -1;
    }
}
