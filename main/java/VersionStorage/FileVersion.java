package VersionStorage;

import org.kohsuke.rngom.parse.host.Base;

import java.io.Serial;
import java.io.Serializable;

public class FileVersion extends BaseVersion {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号
    // 对应版本文件路径
    private final String fileVersionSavedPath;
    // 文件哈希值
    private final String hashCode;


    public FileVersion(String hashCode, String pathToFile, String explanation, String timestamp) {
        // 初始化
        super(timestamp, explanation);
        this.hashCode = hashCode;
        this.fileVersionSavedPath = pathToFile;

    }

    // 所有属性的 getter 方法
    public String getFileVersionSavedPath() {
        return fileVersionSavedPath;
    }

    public String getHashCode() {
        return hashCode;
    }

}

