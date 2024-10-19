package VersionStorage;

import java.io.*;
import java.util.*;

public class ProjectVersionManagement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号

    // versionName to projectversion
    private final Map<String,ProjectVersion> versions;
    private final ArrayList<String> versionNames;


    // 构造函数
    public ProjectVersionManagement() {
        versions = new HashMap<>();
        versionNames = new ArrayList<>();
    }


    // 增加版本
    public void addProjectVersion(ProjectVersion pv) throws Exception {
        ProjectVersion pVersion=null;
        if(!versionNames.isEmpty()) {
            pVersion = versions.get(versionNames.getLast());
        }

        pv.getDiffs(pVersion);

        // 将新的 ProjectVersion 添加到版本列表中
        versions.put(pv.getVersionName(), pv);
        // 将新版本的名称添加到版本名称列表
        versionNames.add(pv.getVersionName());
    }

    public String getFile(String versionName,String fileName) throws Exception{
        var pv=versions.get(versionName);
        if(pv!=null){
            return pv.getHashCode(fileName);
        }
        else{
            throw new Exception("Version Name "+versionName+" not found");
        }
    }


    public ArrayList<String> getVersionNames(){
        return versionNames;
    }


    public  Map<String,FileChangeType> getVersionFileDiffs(String versionName){
        return versions.get(versionName).getChangedFiles();
    }

    public Map<String,String> getFiles(String versionName){
        ProjectVersion pv=versions.get(versionName);
        return pv.getFiles();
    }

    public void checkVersionName(String versionName) throws Exception{
        if(versionNames.contains(versionName)) {
           throw new Exception("Version Name "+versionName+" already exists");
        }
    }


}
