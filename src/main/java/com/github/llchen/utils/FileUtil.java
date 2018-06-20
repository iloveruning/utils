package com.github.llchen.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author llchen12
 * @date 2018/6/20
 */
public class FileUtil {



    public static List<File> findFiles(String filePath) {
        return findFiles(new File(filePath));
    }


    public static List<File> findFiles(File filePath) {
        List<File> fileList = new LinkedList<>();
        if (filePath.exists()) {
            if (filePath.isFile()) {
                fileList.add(filePath);
            } else {
                File[] files = filePath.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        fileList.addAll(findFiles(file));
                    }
                }
            }
        }
        return fileList;
    }


    public static boolean delete(String fileOrPath) {
        File file = new File(fileOrPath);
        if (!file.exists()) {
            return true;
        }
        return delete(file);
    }


    public static boolean delete(String... fileOrPath) {
        if (fileOrPath == null || fileOrPath.length == 0) {
            return true;
        }
        for (String f : fileOrPath) {
            if (!delete(f)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除文件或文件夹
     */
    public static boolean delete(File fileOrDirectory) {
        if (!fileOrDirectory.exists()) {
            return true;
        }

        if (fileOrDirectory.isFile()) {
            return fileOrDirectory.delete();
        }

        File[] files = fileOrDirectory.listFiles();
        if (files == null || files.length == 0) {
            return fileOrDirectory.delete();
        }

        for (File f : files) {
            if (f.isFile()) {
                if (!f.delete()) {
                    return false;
                }
            } else {
                delete(f);
            }
        }

        return fileOrDirectory.delete();
    }


}
