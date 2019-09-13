package com.example.editor.getpath;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class Editor {
    private File file;
    private String path;
    private Uri content;
    private Context context;

    public Editor(Context context) {
        this.context = context;
    }

    public final static String CONTENT = "content";

    // readFile
    public String readFile(Uri uri) {
        if (uri == null)
            return null;

        content = null;

        // Attempt to resolve content uri
        if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
            uri = resolveContent(uri);
        }

        if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // Read into default file if unresolved
            path = null;
            Log.i("fuck", "unresolved");
        } else {
            // Read file
            path = uri.getPath();
        }

        Log.i("fuck" + path, "<" + uri.toString() + ">");

        return path;
    }

    // resolveContent
    private Uri resolveContent(Uri uri) {
        String path = FileUtils.getPath(context, uri);

        if (path != null)
        {
            File file = new File(path);
            if (file.canRead())
                uri = Uri.fromFile(file);
        }

        return uri;
    }
}
