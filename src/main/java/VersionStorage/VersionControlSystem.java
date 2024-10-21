package VersionStorage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static VersionStorage.FileManageHelper.*;



public class VersionControlSystem implements VersionController,Serializable {
    // 哈希值和fileVersion的映射
    @Serial
    private static final long serialVersionUID = 1L; // 建议指定序列化版本号
    private final Map<String,FileVersion> hashCodeToFile;
    private final Map<String,ProjectVersionManagement> branchNameToProjectVersion;

    private final String filePath="VersionControlSystem";
    private final String folderPath=".plugin";

    public VersionControlSystem(){
        // 初始化
        hashCodeToFile = new HashMap<>();
        branchNameToProjectVersion = new HashMap<>();
    }

    public Map<String,FileVersion> getHashCodeToFile(){
        return hashCodeToFile;
    }
    public Map<String,ProjectVersionManagement> getBranchNameToProjectVersion(){
        return branchNameToProjectVersion;
    }



    // 保存项目 initPath为整个项目名 projectPath为存储路径名 versionName为版本名称 explanation为解释
    public void saveProject(String branchName,String initPath,String projectPath,String versionName,String explanation) throws Exception{
        // 查看版本名是否有对应

        String timestamp = String.valueOf(System.currentTimeMillis());

        ProjectVersionManagement pvManager=branchNameToProjectVersion.get(branchName);
        if(pvManager==null){
            pvManager=new ProjectVersionManagement();
            branchNameToProjectVersion.put(branchName,pvManager);
        }
        else{
            pvManager.checkVersionName(versionName);
        }

        initPath=Paths.get(initPath,folderPath).toString();

        // 将initPath和projectName组合为一个路径 文件夹/branch/projectName
        Path projectVersionPath=Paths.get(initPath,branchName,versionName);
        Files.createDirectories(projectVersionPath);
        ArrayList<File> fileList=extractFiles(projectPath);

        ProjectVersion tempProjectVersion=new ProjectVersion(versionName,explanation,timestamp);

        // 处理文件
        for (File file : fileList) {

            Path absoluteFilePath = file.toPath();
            Path relativePath = getFileRelativePath(absoluteFilePath,Paths.get(projectPath));
            // 计算hashcode
            String hashCode = calculateHashCode(file);
            // 获取hashCode对应的fileversion
            FileVersion newFileVersion = hashCodeToFile.get(hashCode);

            // 如果没有保存的版本
            if (newFileVersion == null) {
                Path newPath=Paths.get(projectVersionPath.toString(),relativePath.toString());
                Path newRelativePath=getFileRelativePath(newPath,Paths.get(initPath));
                Files.createDirectories(newPath.getParent());
                Files.copy(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println(newPath.toString()+"\n");

                newFileVersion = new FileVersion(hashCode, newRelativePath.toString(), explanation, timestamp);
                // 更新 hashCodeToFile 的映射
                hashCodeToFile.put(hashCode, newFileVersion);

            }

            // 在版本控制中添加对应映射
            tempProjectVersion.addFileName(relativePath.toString(), hashCode);
        }

        // 将项目版本保存到 pvManager
        pvManager.addProjectVersion(tempProjectVersion);

    }

    // 在路径下保存版本控制系统
    public void saveVersionControlSystem(String savingPath) throws Exception {
        // 创建完整的保存路径
        String combinedPath = Paths.get(savingPath, folderPath, filePath).toString();

        try {
            // 创建文件及其父目录（如果不存在）
            Path path = Paths.get(combinedPath);
            Files.createDirectories(path.getParent()); // 创建父目录
            if (!Files.exists(path)) {
                Files.createFile(path); // 创建文件
                System.out.println("创建文件成功");
            } else {
                System.out.println("文件已存在，直接覆盖写入");
            }

            // 创建并保存整个版本控制系统
            VersionControlSystem vcs = new VersionControlSystem();
            vcs.getHashCodeToFile().putAll(hashCodeToFile);
            vcs.getBranchNameToProjectVersion().putAll(branchNameToProjectVersion);

            // 使用 ObjectOutputStream 进行对象序列化
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(vcs); // 保存整个版本控制系统
            }

        } catch (IOException e) {
            throw new Exception("保存版本控制系统时出错: " + e.getMessage(), e);
        }
    }

    // 在路径下获取版本索引
    public void loadVersionControlSystem(String savingPath) throws Exception {
        String combinedPath = Paths.get(savingPath, folderPath, filePath).toString();

        // 检查路径是否存在
        File file = new File(combinedPath);
        if (!file.exists()) {
            System.out.println("路径不存在，已初始化为对应版本");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 加载整个版本控制系统
            VersionControlSystem vcs = (VersionControlSystem) ois.readObject();
            // 清空现有映射
            hashCodeToFile.clear();
            branchNameToProjectVersion.clear();
            // 恢复状态
            hashCodeToFile.putAll(vcs.getHashCodeToFile());
            branchNameToProjectVersion.putAll(vcs.getBranchNameToProjectVersion());

        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("加载版本控制系统时出错: " + e.getMessage(), e);
        }
    }

    // 获取所有历史
    public ArrayList<String> getVersions(String branchName) throws Exception{
        var temp=branchNameToProjectVersion.get(branchName);
        if(temp==null){
            return new ArrayList<>();
        }
        else{
            return temp.getVersionNames();
        }

    }


    // 获取某版本下的某文件的存储路径
    public String getFileVersion(String branchName,String versionName,String fileName,String initPath) throws Exception{
        // 获取对应的pvManager
        ProjectVersionManagement pvManager=branchNameToProjectVersion.get(branchName);
        String hashCode= pvManager.getFile(versionName,fileName);
        if(hashCode==null){
            return null;
        }
        else{
            FileVersion fv=hashCodeToFile.get(hashCode);
            return Paths.get(initPath,folderPath,fv.getFileVersionSavedPath()).toString();// 获取文件存储路径
        }

    }

    // 获取与上一版本相对比改变的源码 相对路径返回
    public Map<String,FileChangeType> getVersionFileDiffs(String branchName,String versionName)throws Exception{
        var pvManager=branchNameToProjectVersion.get(branchName);
        if(pvManager==null){
            throw new Exception("Branch not found: " + branchName);
        }
        else{
            return pvManager.getVersionFileDiffs(versionName);
        }
    }

    public Set<String> getBranches() throws Exception{
        return new HashSet<>(branchNameToProjectVersion.keySet());
    }

    // 获取某一版本的所有文件
    public Map<String,String> getFiles(String initPath,String branchName,String versionName) throws Exception{
        ProjectVersionManagement pvManager=branchNameToProjectVersion.get(branchName);

        Map<String,String> fileNameToHashCode=pvManager.getFiles(versionName);
        if (fileNameToHashCode == null||fileNameToHashCode.isEmpty()) {
            return new HashMap<>();
        }

        // 存储文件名和对应路径的映射
        Map<String, String> result = new HashMap<>();

        // 遍历 fileNameToHashCode，将哈希码映射到对应的 FileVersion 对象
        for (Map.Entry<String, String> entry : fileNameToHashCode.entrySet()) {
            String fileName = entry.getKey();       // 文件名
            String hashCode = entry.getValue();     // 文件对应的哈希码

            // 从 hashCodeToFile 中获取 FileVersion 对象
            FileVersion fileVersion = hashCodeToFile.get(hashCode);

            String filePath = Paths.get(initPath,folderPath,fileVersion.getFileVersionSavedPath()).toString();  // 假设 FileVersion 类有 getFilePath 方法

            result.put(fileName, filePath);

        }

        // 返回最终的结果映射
        return result;


    }

    // 获取两个分支上的不同文件 1是合并到的分支
    public Map<String,FileChangeType> getFileDiffsBetweenBranches(String branchName_1,String branchName_2) throws Exception{
        ProjectVersionManagement pvManager_1=branchNameToProjectVersion.get(branchName_1);
        ProjectVersionManagement pvManager_2=branchNameToProjectVersion.get(branchName_2);
        if(pvManager_1==null||pvManager_2==null){
            throw new Exception("Branch not found: " + branchName_1+"/"+branchName_2);
        }
        else{
            var pv_1=pvManager_1.getLastProjectVersion();
            var pv_2=pvManager_2.getLastProjectVersion();

            if(pv_2==null){
                throw new Exception("No changes to merge");
            }

            // pv_1没东西
            if (pv_1 == null) {
                return new HashMap<>();
            }
            else{
                var versionDiffs=pv_1.getVersionDiffs(pv_2);

                if(versionDiffs.isEmpty()||versionDiffs.values().stream().allMatch(changeType -> changeType.equals(FileChangeType.ADDED))){
                    if (pvManager_2.getDeletedVersions().isEmpty()) {
                        throw new Exception("No changes to merge");
                    } else {

                        return versionDiffs;  // 如果没有问题，返回 versionDiffs
                    }
                }
                else{
                    return versionDiffs;
                }


            }

        }

    }

    // 对应文件保存哪个版本
    public void mergeBranches(String mergeName,String branchName_1,String branchName_2,Map<String,String> saveFiles) throws Exception{
        var pvManager_1=branchNameToProjectVersion.get(branchName_1);
        var pvManager_2=branchNameToProjectVersion.get(branchName_2);
        if(pvManager_1==null||pvManager_2==null){
            throw new Exception("Branch not found: " + branchName_1+"/"+branchName_2);
        }

        pvManager_1.mergeBranches(mergeName,pvManager_2,saveFiles);


    }

    // 获取上次
    public Map<String,String> getLastVersionFiles(String initPath,String branchName) throws Exception{
        var temp=branchNameToProjectVersion.get(branchName);
        var arrayList=temp.getVersionNames();
        if(arrayList.isEmpty()){
            return new HashMap<>();
        }
        else{
            return getFiles(initPath,branchName,temp.getVersionNames().getLast());
        }

    }

    public void createNewBranch(String newBranchName,String oldBranchName) throws Exception{
        var temp=branchNameToProjectVersion.computeIfAbsent(oldBranchName,k->new ProjectVersionManagement());
        var newPvManager=new ProjectVersionManagement(temp);
        branchNameToProjectVersion.put(newBranchName,newPvManager);
    }





}


