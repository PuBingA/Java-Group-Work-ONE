package VersionStorage;

import java.io.*;
import java.util.*;

public class ProjectVersionManagement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号

    // 版本列表

    private final Map<String,ProjectVersion> versions;
    private final ArrayList<String> versionNames;

    // 构造函数
    public ProjectVersionManagement() {
        versions = new HashMap<>();
        versionNames = new ArrayList<>();
    }


    // 重名监测
    public boolean checkName(String versionName) throws Exception {
        // 检查版本名称是否已存在
        return !versions.containsKey(versionName);
    }


    // 增加版本
    public void addProjectVersion(ProjectVersion pv) throws Exception {
        // 检查版本名称是否重复
        //checkName(pv.getVersionName());

        // 将新的 ProjectVersion 添加到版本列表中
        versions.put(pv.getVersionName(), pv);
        // 将新版本的名称添加到版本名称列表
        versionNames.add(pv.getVersionName());
    }

    // 根据版本名称获取 版本
    public ProjectVersion getVersion(String versionName){
        return versions.get(versionName);
    }

    public ArrayList<String> getVersionNames(){
        return versionNames;
    }

    public Map<String,ProjectVersion> getVersions(){
        return versions;
    }


}
