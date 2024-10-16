package VersionStorage;

import java.io.Serial;
import java.io.Serializable;

public class FileVersion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号
    // 对应版本文件路径
    private final String fileVersionSavedPath;
    // 文件哈希值
    private final String hashCode;
    // 文件创建时间
    private final String timestamp;
    // 文件创建解释
    private final String explanation;

    public FileVersion(String hashCode, String pathToFile, String explanation, String timestamp) {
        // 初始化
        this.hashCode = hashCode;
        this.fileVersionSavedPath = pathToFile;
        this.explanation = explanation;
        this.timestamp = timestamp;
    }

    // 所有属性的 getter 方法
    public String getFileVersionSavedPath() {
        return fileVersionSavedPath;
    }

    public String getHashCode() {
        return hashCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getExplanation() {
        return explanation;
    }
}

