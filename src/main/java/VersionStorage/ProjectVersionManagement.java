package VersionStorage;

import java.io.*;
import java.util.*;

public class ProjectVersionManagement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号

    // versionName to projectversion
    private final Map<String,ProjectVersion> versions;
    private final ArrayList<String> versionNames;
    private final HashSet<String> deletedVersions;


    // 构造函数
    public ProjectVersionManagement() {
        versions = new HashMap<>();
        versionNames = new ArrayList<>();
        deletedVersions = new HashSet<>();
    }

    public ProjectVersionManagement(ProjectVersionManagement original) throws Exception {
        // 深拷贝 versions
        this.versions = new HashMap<>();
        for (Map.Entry<String, ProjectVersion> entry : original.versions.entrySet()) {
            // 假设 ProjectVersion 有一个深拷贝的构造函数
            this.versions.put(entry.getKey(), new ProjectVersion(entry.getValue()));
        }

        // 深拷贝 versionNames
        this.versionNames = new ArrayList<>(original.versionNames);

        // 深拷贝 deletedVersions
        this.deletedVersions = new HashSet<>(original.deletedVersions);
    }


    public HashSet<String> getDeletedVersions(){
        return deletedVersions;
    }


    // 增加版本
    public void addProjectVersion(ProjectVersion pv) throws Exception {
        ProjectVersion pVersion=null;
        if(!versionNames.isEmpty()) {
            pVersion = versions.get(versionNames.getLast());
        }
        // 获取差异
        Map<String,FileChangeType> temp=pv.getDiffs(pVersion);

        // 遍历差异，找到被删除的文件并添加到 deletedVersions
        for (Map.Entry<String, FileChangeType> entry : temp.entrySet()) {
            if (entry.getValue() == FileChangeType.DELETED) {
                deletedVersions.add(entry.getKey()); // 添加被删除的文件名
            }
        }
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

    public ProjectVersion getLastProjectVersion() throws Exception{
        if(versionNames.isEmpty()){
            return null;
        }
        System.out.println(versionNames.getLast());
        return versions.get(versionNames.getLast());
    }

    public void mergeBranches(String mergeName,ProjectVersionManagement pv,Map<String,String> saveConfiguration) throws Exception{
        if(pv==null){
            throw new Exception("not found");
        }
        else{
            var v_1=getLastProjectVersion();
            var v_2=pv.getLastProjectVersion();

            String timestampString = Long.toString(System.currentTimeMillis());

            ProjectVersion finalversion;
            if(v_2==null){
                // 如果两边都为null 则新建一个并插入
                throw new Exception("No changes to merge");
            }
            else if(v_1==null){
                finalversion=new ProjectVersion(mergeName,"merge",timestampString,v_2.getFileNames());
            }
            else{
                // 合并文件名和哈希值，处理合并冲突
                Map<String, String> mergedFileNames = new HashMap<>();

                // 获取两个版本的文件名和更改类型
                Map<String, String> fileNames1 = v_1.getFileNames();
                Map<String, String> fileNames2 = v_2.getFileNames();

                // 合并逻辑
                Set<String> allFileNames = new HashSet<>(fileNames1.keySet());
                allFileNames.addAll(fileNames2.keySet());

                for (String fileName : allFileNames) {

                    if(pv.getDeletedVersions().contains(fileName)){
                        //allFileNames.remove(fileName);
                        continue;
                    }

                    String hash1 = fileNames1.get(fileName);
                    String hash2 = fileNames2.get(fileName);

                    if (hash1 == null && hash2 != null) {
                        // 文件只在 v_2 中，添加到合并结果
                        mergedFileNames.put(fileName, hash2);
                    } else if (hash1 != null && hash2 == null) {
                        // 文件只在 v_1 中，添加到合并结果
                        mergedFileNames.put(fileName, hash1);
                    } else if(!Objects.equals(hash1, hash2))  {
                        // 文件在两个版本中都有，处理合并冲突
                        String configValue = saveConfiguration.get(fileName);
                        if ("1".equals(configValue)) {
                            // 取 v_1 的值
                            mergedFileNames.put(fileName, hash1);

                        } else if ("2".equals(configValue)) {
                            // 取 v_2 的值
                            mergedFileNames.put(fileName, hash2);

                        } else {
                            throw new Exception("Merge conflict for file: " + fileName + ". Configuration value is invalid.");
                        }
                    }
                    else{
                        System.out.println("mergedFileNames"+mergedFileNames);
                        mergedFileNames.put(fileName, hash1);
                        System.out.println("mergedFileNames"+mergedFileNames);
                    }
                    // projectversion内含private final Map<String, String> fileNames; 将两个这个合并 遇到合并冲突的情况 查看saveconfiguration中对应的值
                    // 如果对应的值为"1"则取v_1中的值 如果对应的值为2 则取v_2中的值
                    // 合并完成得到map<String,String>
                }
                finalversion = new ProjectVersion(mergeName, "merge", timestampString, mergedFileNames);

            }
            addProjectVersion(finalversion);
        }




        // 将两个arrayList<Projectversion>按时间排序 存进新projectmanagement中

        // 和上一版对比的差异改为和最后arrayList<>最后一个比较的差异

        // 将新的projectversion存入
    }


}
