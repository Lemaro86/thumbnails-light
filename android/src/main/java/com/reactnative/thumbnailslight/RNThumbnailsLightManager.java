
package com.reactnative.thumbnailslight;

package expo.modules.videothumbnails;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.os.Bundle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import java.util.EnumSet;

public interface ReadableArguments {
  Collection<String> keys();

  boolean containsKey(String key);

  Object get(String key);

  boolean getBoolean(String key);

  boolean getBoolean(String key, boolean defaultValue);

  double getDouble(String key);

  double getDouble(String key, double defaultValue);

  int getInt(String key);

  int getInt(String key, int defaultValue);

  String getString(String key);

  String getString(String key, String defaultValue);

  List getList(String key);

  List getList(String key, List defaultValue);

  Map getMap(String key);

  Map getMap(String key, Map defaultValue);

  ReadableArguments getArguments(String key);

  boolean isEmpty();

  int size();

  Bundle toBundle();
}

public enum Permission {
  READ, WRITE,
}

public interface FilePermissionModuleInterface {
  EnumSet<Permission> getPathPermissions(Context context, String path);
}

public class ThumbnailsLightManager extends ReactContextBaseJavaModule {
  private static final String TAG = "ThumbnailsLight";
  private static final String ERROR_TAG = "E_VIDEO_THUMBNAILS";
  private static String ERR_COULD_NOT_GET_THUMBNAIL = "ERR_COULD_NOT_GET_THUMBNAIL";

  private static final String KEY_QUALITY = "quality";
  private static final String KEY_TIME = "time";
  private static final String KEY_HEADERS = "headers";

  public ThumbnailsLightManager(Context context) {
    super(context);
  }

  @Override
  public String getName() {
    return TAG;
  }

  private static class GetThumbnailAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    private String mSourceFilename;
    private ReadableArguments mVideoOptions;
    Exception mError;

    GetThumbnailAsyncTask(String sourceFilename, ReadableArguments videoOptions) {
      mSourceFilename = sourceFilename;
      mVideoOptions = videoOptions;
    }

    @Override
    protected final Bitmap doInBackground(Void... voids) {
      long time = mVideoOptions.getInt(KEY_TIME, 0) * 1000;
      MediaMetadataRetriever retriever = new MediaMetadataRetriever();
      try {
        if (URLUtil.isFileUrl(mSourceFilename)) {
          retriever.setDataSource(Uri.decode(mSourceFilename).replace("file://", ""));
        } else {
          retriever.setDataSource(mSourceFilename, mVideoOptions.getMap(KEY_HEADERS, new HashMap<String, String>()));
        }
      } catch (RuntimeException e) {
        mError = e;
        return null;
      }

      return retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }
  }

  public static String generateOutputPath(File internalDirectory, String dirName, String extension) throws IOException {
    File directory = new File(internalDirectory + File.separator + dirName);
    ensureDirExists(directory);
    String filename = UUID.randomUUID().toString();
    return directory + File.separator + filename + (extension.startsWith(".") ? extension : "." + extension);
  }

  private boolean isAllowedToRead(String url) {
    if (mModuleRegistry != null) {
      FilePermissionModuleInterface permissionModuleInterface = mModuleRegistry.getModule(FilePermissionModuleInterface.class);
      if (permissionModuleInterface != null) {
        return permissionModuleInterface.getPathPermissions(getContext(), url).contains(Permission.READ);
      }
    }
    return true;
  }

  public void getThumbnail(String sourceFilename, final Object videoOptions, final Promise promise) {
    if (URLUtil.isFileUrl(sourceFilename) && !isAllowedToRead(Uri.decode(sourceFilename).replace("file://", ""))) {
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
          String path = generateOutputPath(getContext().getCacheDir(), "VideoThumbnails", "jpg");
          OutputStream outputStream = new FileOutputStream(path);
          thumbnail.compress(Bitmap.CompressFormat.JPEG, (int) (videoOptions.getDouble(KEY_QUALITY, 1) * 100), outputStream);
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
