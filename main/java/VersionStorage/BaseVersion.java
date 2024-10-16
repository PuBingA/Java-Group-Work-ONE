package VersionStorage;

import java.io.Serial;
import java.io.Serializable;

// 版本基类
public class BaseVersion implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号

    private final String timestamp;
    // 文件创建解释
    private final String explanation;

    BaseVersion(String timestamp,String explanation) {
        this.timestamp = timestamp;
        this.explanation = explanation;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getExplanation() {
        return explanation;
    }
}
