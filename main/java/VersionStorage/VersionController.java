package VersionStorage;

import java.util.*;

public interface VersionController {

    // 根据分支监测文件夹
    public void saveVersionControlSystem(String initPath) throws Exception;

    // load
    public void loadVersionControlSystem(String savingPath) throws Exception;

    // 获取所有历史
    public ArrayList<String> getVersions();

    // 获取目录差别
    // public String getDirectoryDiff(String versionName,String directory);

    // 获取某一文件 返回文件路径
    public String getFileVersion(String versionName,String fileName);

    // 保存当前项目
    public void saveProject(String initPath,String projectPath,String projectName,String explanation) throws Exception;


}
