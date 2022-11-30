package utils;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetFoldFileNames {
    /**
     * 读取某个文件夹下的所有文件
     */
    public static List<String> readfile(String filepath) throws FileNotFoundException, IOException {
        return readfileWithType(filepath, "java");
    }

    public static List<String> readfileWithType(String filepath, String type) throws FileNotFoundException, IOException {
        List<String> fileNames = new ArrayList<String>();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println(filepath + " no files");
                return Lists.newArrayList();
            }
            if (!file.isDirectory()) {
                System.out.println("Not a dictionary");
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "/" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        String fileName = readfile.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if(suffix.equals(type)){
                            fileNames.add(filepath + "/" + filelist[i]);
                        }
                    } else if (readfile.isDirectory()) {
                        fileNames.addAll(readfileWithType(filepath + "/" + filelist[i], type));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("readfile()  Exception:" + e.getMessage());
        }
        return fileNames;
    }
}