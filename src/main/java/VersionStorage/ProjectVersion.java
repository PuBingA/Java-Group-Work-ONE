package VersionStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ProjectVersion extends BaseVersion {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号
    private final String versionName;


    // 文件夹相对路径名称
   // private final ArrayList<String> folderNames;
    // 文件相对路径名称-文件哈希值
    private final Map<String, String> fileNames;

    public ProjectVersion(String versionName, String explanation, String savedTimeStamp) {
        // 初始化
        super(savedTimeStamp,explanation);
        this.versionName = versionName;
       // this.folderNames = new ArrayList<>(); // 初始化文件夹名称列表
        this.fileNames = new HashMap<>(); // 初始化文件映射
    }

    // 增加文件夹名称
   /* public void addFolderName(String folderName) throws Exception {
        if (folderName == null || folderName.isEmpty()) {
            throw new Exception("Folder name cannot be null or empty.");
        }
        // 检查文件夹名称是否已经存在
        if (!folderNames.contains(folderName)) {
            folderNames.add(folderName); // 添加文件夹名称
        } else {
            throw new Exception("Folder name '" + folderName + "' already exists.");
        }
    }*/

    // 增加文件映射
    public void addFileName(String fileName, String hashCode) throws Exception {
        if (fileName == null || fileName.isEmpty() || hashCode == null || hashCode.isEmpty()) {
            throw new Exception("File name and hash code cannot be null or empty.");
        }
        // 检查文件名是否已经存在
        if (!fileNames.containsKey(fileName)) {
            fileNames.put(fileName, hashCode); // 添加文件映射
        } else {
            throw new Exception("File name '" + fileName + "' already exists.");
        }
    }

    // 所有属性的 getter 方法
    public String getVersionName() {
        return versionName;
    }


    public Map<String, String> getFileNames() {
        return new HashMap<>(fileNames); // 返回文件映射的副本
    }
}
