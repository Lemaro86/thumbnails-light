package com.reactnative.thumbnailslight;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
// import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.UUID;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.EnumSet;

public class RNThumbnailsLightManager extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private static final String TAG = "ThumbnailsLight";
    private static final String ERROR_TAG = "E_VIDEO_THUMBNAILS";
    private static String ERR_COULD_NOT_GET_THUMBNAIL = "ERR_COULD_NOT_GET_THUMBNAIL";

    private static final String KEY_QUALITY = "quality";
    private static final String KEY_TIME = "time";
    private static final String KEY_HEADERS = "headers";
//   private enum Permission {
//     READ, WRITE,
//   }
//   private interface FilePermissionModuleInterface {
//     EnumSet<Permission> getPathPermissions(reactContext context, String path);
//   }

    RNThumbnailsLightManager(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return TAG;
    }

    private static class GetThumbnailAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private String mSourceFilename;
        private ReadableMap mVideoOptions;
        Exception mError;

        @ReactMethod
        GetThumbnailAsyncTask(String sourceFilename, ReadableMap videoOptions) {
            mSourceFilename = sourceFilename;
            mVideoOptions = videoOptions;
        }

        @Override
        protected final Bitmap doInBackground(Void... voids) {
            long time = mVideoOptions.hasKey(KEY_TIME) ? mVideoOptions.getInt(KEY_TIME) * 1000 : 0;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                if (URLUtil.isFileUrl(mSourceFilename)) {
                    retriever.setDataSource(Uri.decode(mSourceFilename).replace("file://", ""));
                } else {
//           think about default map setDataSource(String uri, Map<String, String> headers) ?
                    retriever.setDataSource(mSourceFilename);
//           retriever.setDataSource(mSourceFilename, mVideoOptions.hasKey(KEY_HEADERS) ? mVideoOptions.getMap(KEY_HEADERS) : String(""));
                }
            } catch (RuntimeException e) {
                mError = e;
                return null;
            }

            return retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        }
    }

    public static File ensureDirExists(File dir) throws IOException {
        if (!(dir.isDirectory() || dir.mkdirs())) {
            throw new IOException("Couldn't create directory '" + dir + "'");
        }
        return dir;
    }

    public static String generateOutputPath(File internalDirectory, String dirName, String extension) throws IOException {
        File directory = new File(internalDirectory + File.separator + dirName);
        ensureDirExists(directory);
        String filename = UUID.randomUUID().toString();
        return directory + File.separator + filename + (extension.startsWith(".") ? extension : "." + extension);
    }

//   private boolean isAllowedToRead(String url) {
//     if (mModuleRegistry != null) {
//       FilePermissionModuleInterface permissionModuleInterface = mModuleRegistry.getModule(FilePermissionModuleInterface.class);
//       if (permissionModuleInterface != null) {
//         return permissionModuleInterface.getPathPermissions(getReactApplicationContext(), url).contains(Permission.READ);
//       }
//     }
//     return true;
//   }


    //is it need here react method?
    public void getThumbnail(String sourceFilename, final ReadableMap videoOptions, final Promise promise) {
        if (URLUtil.isFileUrl(sourceFilename)
//     && !isAllowedToRead(Uri.decode(sourceFilename).replace("file://", ""))
        ) {
            promise.reject(ERROR_TAG, "Can't read file");
            return;
        }

        GetThumbnailAsyncTask getThumbnailAsyncTask = new GetThumbnailAsyncTask(sourceFilename, videoOptions) {
            @Override
            protected void onPostExecute(Bitmap thumbnail) {
                if (thumbnail == null || mError != null) {
                    String errorMessage = "Could not generate thumbnail.";
                    if (mError != null) {
                        errorMessage = String.format("%s %s", errorMessage, mError.getMessage());
                    }
                    promise.reject(ERR_COULD_NOT_GET_THUMBNAIL, errorMessage, mError);
                    return;
                }
                try {
                    String path = generateOutputPath(getReactApplicationContext().getCacheDir(), "VideoThumbnails", "jpg");
                    OutputStream outputStream = new FileOutputStream(path);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, (int) (videoOptions.getDouble(KEY_QUALITY) * 100), outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Bundle response = new Bundle();
                    response.putString("uri", Uri.fromFile(new File(path)).toString());
                    response.putInt("width", thumbnail.getWidth());
                    response.putInt("height", thumbnail.getHeight());
                    promise.resolve(response);
                } catch (IOException ex) {
                    promise.reject(ERROR_TAG, ex);
                }
            }
        };
        getThumbnailAsyncTask.execute();
    }
}
