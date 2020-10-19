package com.lemaro.react;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.UUID;

@ReactModule(name = RNThumbnailsLightModule.MODULE_NAME)
public class RNThumbnailsLightModule extends ReactContextBaseJavaModule {

    public static final String MODULE_NAME = "RNThumbnailsLight";

    private static final String TAG = "ThumbnailsLight";
    private static final String ERROR_TAG = "E_VIDEO_THUMBNAILS";
    private static String ERR_COULD_NOT_GET_THUMBNAIL = "ERR_COULD_NOT_GET_THUMBNAIL";

    private static final String KEY_QUALITY = "quality";
    private static final String KEY_TIME = "time";
    private static final String KEY_HEADERS = "headers";

    RNThumbnailsLightModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
      return MODULE_NAME;
    }

    private static class GetThumbnailAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private String mSourceFilename;
        Exception mError;

        GetThumbnailAsyncTask(String sourceFilename) {
            mSourceFilename = sourceFilename;
        }

        @Override
        protected final Bitmap doInBackground(Void... voids) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            long time = 1000000;
            try {
                retriever.setDataSource(mSourceFilename, new HashMap<String, String>());
            } catch (Exception e) {
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

    @ReactMethod
    public void getThumbnail(String sourceFilename, final ReadableMap videoOptions, final Promise promise) {

        GetThumbnailAsyncTask getThumbnailAsyncTask = new GetThumbnailAsyncTask(sourceFilename) {
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
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    String imageUrl = Uri.fromFile(new File(path)).toString();

//                     Bundle response = new Bundle();
//                     response.putString("uri", imageUrl);
//                     response.putInt("width", thumbnail.getWidth());
//                     response.putInt("height", thumbnail.getHeight());

                    WritableMap map = Arguments.createMap();
                    map.putString("uri", imageUrl);

                    promise.resolve(map);
                } catch (Exception ex) {
                    Log.e("E_RNThumnail_ERROR", ex.getMessage());
                    promise.reject(ERROR_TAG, ex);
                }
            }
        };
        getThumbnailAsyncTask.execute();
    }
}
