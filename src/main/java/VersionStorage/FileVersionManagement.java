package VersionStorage;

import java.util.*;
import java.io.*;

public class FileVersionManagement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号
    // 每个文件名对应的一系列改动
    private final Map<String,List<String>> fileList;

    public FileVersionManagement() {
        fileList = new HashMap<>();
    }

    // 增加文件版本
    public void addFileVersion(String relativePath, String hashCode) throws Exception {
        // 检查相对路径和哈希值是否为空
        if (relativePath == null || relativePath.isEmpty() || hashCode == null || hashCode.isEmpty()) {
            throw new Exception("Relative path and hash code cannot be null or empty.");
        }

        // 检查文件名是否已存在，如果存在，则添加哈希值到现有列表中；如果不存在，则创建新的列表
        if (fileList.containsKey(relativePath)) {

            // 获取已有文件的改动列表
            List<String> hashCodes = fileList.get(relativePath);
            // 检查新添加的哈希值是否与最后一个哈希值相同
            if (hashCodes.isEmpty() || !hashCodes.getLast().equals(hashCode)) {
                hashCodes.add(hashCode); // 添加新的哈希值
            }
        } else {
            // 创建新的列表并添加第一个哈希值
            List<String> hashCodes = new ArrayList<>();
            hashCodes.add(hashCode);
            fileList.put(relativePath, hashCodes); // 将新文件及其哈希值添加到 Map
        }
    }

}
