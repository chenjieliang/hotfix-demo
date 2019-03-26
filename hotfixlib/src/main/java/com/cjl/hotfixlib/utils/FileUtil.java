package com.cjl.hotfixlib.utils;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author chenjieliang
 */
public class FileUtil {


    /**
     * 复制文件
     *
     * @param fromPathName
     * @param toPathName
     * @return
     */
    public static int copy(String fromPathName, String toPathName) {
        try {
            File newFile = new File(toPathName);
            File parentFile = newFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            return copy(newFile, toPathName);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 复制文件
     *
     * @param fromFile
     * @param toPathName
     * @return
     */
    public static int copy(File fromFile, String toPathName) {
        File newFile = new File(toPathName);
        File parentFile = newFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        delete(toPathName);
        return copy(fromFile,newFile);
    }

    public static int copy(File fromFile,File toFile){
        InputStream from = null;
        try {
            from = new FileInputStream(fromFile);
            return copy(from,toFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        /*InputStream from = null;
        OutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new BufferedOutputStream(new FileOutputStream(toFile));
            byte buf[] = new byte[1024];
            int c;
            while ((c = from.read(buf)) > 0) {
                to.write(buf, 0, c);
            }
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            close(to);
            close(from);
        }*/
    }

    public static int copy(InputStream from,File toFile){
        OutputStream to = null;
        try {
            to = new BufferedOutputStream(new FileOutputStream(toFile));
            byte buf[] = new byte[1024];
            int c;
            while ((c = from.read(buf)) > 0) {
                to.write(buf, 0, c);
            }
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            close(to);
            close(from);
        }
    }

    /**
     * 删除文件
     */
    public static boolean delete(String filePathName) {
        if (TextUtils.isEmpty(filePathName)) return false;
        File file = new File(filePathName);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
