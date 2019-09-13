package com.example.editor.getpath;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Get a file path from a Uri. This will get the the path for
     * Storage Access Framework Documents, as well as the _data field
     * for the MediaStore and other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming
     * it represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri)
    {

//        if (BuildConfig.DEBUG)
//            Log.d(TAG + " File",
//                    "Authority: " + uri.getAuthority() +
//                            ", Fragment: " + uri.getFragment() +
//                            ", Port: " + uri.getPort() +
//                            ", Query: " + uri.getQuery() +
//                            ", Scheme: " + uri.getScheme() +
//                            ", Host: " + uri.getHost() +
//                            ", Segments: " + uri.getPathSegments().toString()
//            );

        final boolean isKitKat =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
            {
                final List<String> segments = uri.getPathSegments();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment
                            .getExternalStorageDirectory() + "/" + split[1];
                }
                else if ("home".equalsIgnoreCase(type))
                {
                    return Environment
                            .getExternalStorageDirectory() + "/Documents/" +
                            split[1];
                }
                else if ("document".equalsIgnoreCase(segments.get(0)))
                {
                    return Environment
                            .getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {
                // Check for non-numeric id
                try
                {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri =
                            ContentUris
                                    .withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                            Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }


                // Id not a number
                catch (Exception e)
                {
                    return getDataColumn(context, uri, null, null);
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri =
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri =
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type))
                {
                    contentUri =
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]
                        {
                                split[1]
                        };

                return getDataColumn(context, contentUri,
                        selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            // Return ContentProvider path
            String path = getDataColumn(context, uri, null, null);
            if (path != null)
                return path;

            // Return FileProvider path
            return fileProviderPath(uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content"
                .equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the
     *                      query.
     * @return The value of the _data column, which is typically a
     * file path.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getDataColumn(Context context, Uri uri,
                                       String selection,
                                       String[] selectionArgs)
    {

        final String column = "_data";
        final String[] projection =
                {
                        column
                };

        try (Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null))
        {
            if (cursor != null && cursor.moveToFirst())
            {
//                if (BuildConfig.DEBUG)
//                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndex(column);
                if (column_index >= 0)
                    return cursor.getString(column_index);
            }
        }
        catch (Exception e)
        {
        }

        return null;
    }

    /**
     * @param uri The Uri to match.
     * @return The file path from the FileProvider Uri.
     * @author billthefarmer
     */
    public static String fileProviderPath(Uri uri)
    {
//        if (BuildConfig.DEBUG)
//            Log.d(TAG, "Path " + uri.getPath());

        StringBuilder path = new StringBuilder();
        List<String> list = uri.getPathSegments();
        if (list.contains("storage") &&
                list.contains("emulated") &&
                list.contains("0"))
        {
            List<String> segments =
                    list.subList(list.indexOf("storage"), list.size());

            for (String segment : segments)
            {
                path.append(File.separator);
                path.append(segment);
            }

//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Path " + path.toString());

            File file = new File(path.toString());
            if (file.isFile())
                return path.toString();
        }

        if (list.size() > 1)
        {
            List<String> segments =
                    list.subList(1, list.size());

            path.append(Environment.getExternalStorageDirectory());
            for (String segment : segments)
            {
                path.append(File.separator);
                path.append(segment);
            }

//            if (BuildConfig.DEBUG)
//                Log.d(TAG, "Path " + path.toString());

            File file = new File(path.toString());
            if (file.isFile())
                return path.toString();
        }

        return null;
    }

}
