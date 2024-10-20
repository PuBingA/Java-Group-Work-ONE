package VersionStorage;



public enum FileChangeType {


    ADDED("Added"),      // 新增文件
    MODIFIED("Modified"), // 修改文件
    DELETED("Deleted");   // 删除文件

    private final String value;

    // 构造函数
    FileChangeType(String value) {
        this.value = value;
    }

    // 获取对应的字符串值
    public String getValue() {
        return value;
    }

    // 根据字符串获取对应的枚举
    public static FileChangeType fromString(String text) {
        for (FileChangeType type : FileChangeType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

