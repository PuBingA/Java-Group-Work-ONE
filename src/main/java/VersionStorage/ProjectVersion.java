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
    // 文件相对路径名称-文件哈希值
    private final Map<String, String> fileNames;
    // 文件相对路径-更改
    private final Map<String,FileChangeType> changedFiles;

    public ProjectVersion(String versionName, String explanation, String savedTimeStamp) {
        // 初始化
        super(savedTimeStamp,explanation);
        this.versionName = versionName;
        this.fileNames = new HashMap<>(); // 初始化文件映射
        this.changedFiles = new HashMap<>();
    }

    public void addFileName(String fileName, String hashCode) throws Exception {
        if (fileName == null || fileName.isEmpty() || hashCode==null||hashCode.isEmpty()) {
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

    // 获取差异
    public void getDiffs(ProjectVersion lastVersion) throws Exception {
        if (lastVersion == null) {

            for (String fileName : fileNames.keySet()) {
                changedFiles.put(fileName, FileChangeType.ADDED); // 所有文件为新增
            }
            return;
        }

        Set<String> allFileNames = new HashSet<>(fileNames.keySet());
        allFileNames.addAll(lastVersion.getFileNames().keySet());
        // 遍历所有文件名，并比较哈希值来确定更改类型
        for (String fileName : allFileNames) {
            String currentHash = fileNames.get(fileName); // 当前版本哈希
            String lastHash = lastVersion.getFileNames().get(fileName); // 上一版本哈希

            if (currentHash == null) {
                // 当前版本没有该文件，文件被删除
                changedFiles.put(fileName, FileChangeType.DELETED);
            } else if (lastHash == null) {
                // 上一版本没有该文件，文件为新增
                changedFiles.put(fileName, FileChangeType.ADDED);
            } else if (!currentHash.equals(lastHash)) {
                // 文件存在，但哈希值不同，文件被修改
                changedFiles.put(fileName, FileChangeType.MODIFIED);
            }
        }
    }


    public Map<String, String> getFileNames() {
        return new HashMap<>(fileNames);
    }

    public Map<String,FileChangeType> getChangedFiles() {
        return new HashMap<>(changedFiles);
    }

    public Map<String,String> getFiles(){
        return fileNames;
    }

    public String getHashCode(String fileName) throws Exception{
        return fileNames.get(fileName);
    }
}
