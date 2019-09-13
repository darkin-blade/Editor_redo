package com.example.editor.getpath;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SearchView;

import java.io.File;

public class Editor extends Activity {
    private File file;
    private String path;
    private Uri content;
    private Uri readUri;
    private String toAppend;
    private EditText textView;
    private MenuItem searchItem;
    private SearchView searchView;
    private ScrollView scrollView;
    private Runnable updateHighlight;

    private final static int REQUEST_READ = 1;
    private final static int REQUEST_SAVE = 2;
    private final static int REQUEST_OPEN = 3;

    public final static String PATH = "path";
    public final static String EDIT = "edit";
    public final static String CHANGED = "changed";
    public final static String CONTENT = "content";
    public final static String MODIFIED = "modified";

    // readFile
    private void readFile(Uri uri)
    {
        if (uri == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
                readUri = uri;
                return;
            }
        }

        content = null;

        // Attempt to resolve content uri
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
            uri = resolveContent(uri);

        // Read into default file if unresolved
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
        {
            content = uri;
            Uri defaultUri = Uri.fromFile(file);
            path = defaultUri.getPath();

            String title = uri.getLastPathSegment();
            setTitle(title);
        }

        // Read file
        else
        {
            path = uri.getPath();

            String title = uri.getLastPathSegment();
            setTitle(title);
        }

//        ReadTask read = new ReadTask(this);
//        read.execute(uri);
//
//        changed = false;
//        modified = file.lastModified();
//        savePath(path);
//        invalidateOptionsMenu();

        Log.i("fuck" + path, "<" + uri.toString() + ">");
    }

    // resolveContent
    private Uri resolveContent(Uri uri)
    {
        String path = FileUtils.getPath(this, uri);

        if (path != null)
        {
            File file = new File(path);
            if (file.canRead())
                uri = Uri.fromFile(file);
        }

        return uri;
    }
}
