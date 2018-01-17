package com.bmw.peek2slite.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by admin on 2018/1/8.
 */

public class Manufacturer_FileUtil {


    public static String readFileMessage(String path, String fileName) {
        if (path == null || fileName == null)
            return null;
        File fileBase = new File(path);
        File file = new File(fileBase, fileName);
        if (!fileBase.exists() || !file.exists())
            return null;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String msg = null;
            if ((msg = bufferedReader.readLine()) != null) {
                bufferedReader.close();
                fileReader.close();
                return msg;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeFile(String basePath, String fileName, String message) {
        File fileBase = new File(basePath);
        if (!fileBase.exists()) {
            fileBase.mkdirs();
        }
        File file = new File(fileBase, fileName);
        if (!file.exists())
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.append(message);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
