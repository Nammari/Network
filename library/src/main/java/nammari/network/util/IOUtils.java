package nammari.network.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import nammari.network.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nammari on 8/10/14.
 */
public class IOUtils {


    public static String convertInputStreamToString(InputStream in) throws IOException {
        return new String(readFully(in), "UTF-8");
    }

    //http://stackoverflow.com/a/2661480
    public static String readFileAsString(String filePath) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
        try {
            long len = new File(filePath).length();
            Logger.logError("len", "" + len);
            if (len > Integer.MAX_VALUE)
                throw new IOException("File " + filePath + " too large, was " + len + " bytes.");
            byte[] bytes = new byte[(int) len];
            dis.readFully(bytes);
            return new String(bytes, "UTF-8");
        } finally {
            dis.close();
        }
    }


    public static long getFileLength(String filePath) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
        return new File(filePath).length();
    }

    public static long getFileLengthNormal(String filePath) throws IOException {
        return new File(filePath).length();
    }

    public static byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    public static byte[] getImageFileReSized(String path, int image_max_size) {


        Bitmap bitmap = loadBitmap(path, 1024, 1024);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] result = stream.toByteArray();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            try {
                //https://developer.android.com/training/displaying-bitmaps/manage-memory.html#recycle
                bitmap.recycle();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Logger.logError("image size after resize", "" + result.length);
        return result;
    }





    public static Bitmap loadBitmap(String path, float maxWidth, float maxHeight) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (path != null) {
            BitmapFactory.decodeFile(path, bmOptions);
        }
        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        Logger.logError("photoW", "" + photoW);
        Logger.logError("photoH", "" + photoH);
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        if (scaleFactor < 1) {
            scaleFactor = 1;
        }
        Logger.logError("scaleFactor", "" + scaleFactor);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;

        String exifPath = null;
        if (path != null) {
            exifPath = path;
        }

        Matrix matrix = null;

        if (exifPath != null) {
            ExifInterface exif;
            try {
                exif = new ExifInterface(exifPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (Exception e) {
                Logger.logError("tmessages", e.toString());
            }
        }

        Bitmap b = null;
        if (path != null) {
            try {
                b = BitmapFactory.decodeFile(path, bmOptions);
                if (b != null) {
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                }
            } catch (Exception e) {
                Logger.logError("tmessages", e.toString());
                if (b == null) {
                    b = BitmapFactory.decodeFile(path, bmOptions);
                }
                if (b != null) {
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                }
            }
        }

        return b;
    }
}
