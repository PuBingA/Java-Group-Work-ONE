package VersionStorage;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

import static VersionStorage.FileManageHelper.*;



public class VersionSystem implements VersionController {
    // 哈希值和fileVersion的映射
    private final Map<String,FileVersion> hashCodeToFile;
    private ProjectVersionManagement pvManager;
    private FileVersionManagement fvManager;

    private final String filePath="VersionControlSystem.json";


    public VersionSystem(){
        // 初始化
        hashCodeToFile = new HashMap<>();
    }



    // 保存项目
    public void saveProject(String initPath,String projectPath,String projectName,String explanation) throws Exception{
        if(pvManager.checkName(projectName)){
            try{
                // 创建时间戳
                String timestamp = String.valueOf(System.currentTimeMillis());
                // 创建项目版本
                ProjectVersion tempProjectVersion=new ProjectVersion(projectName,explanation,timestamp);
                // 将initPath和projectName组合为一个路径 文件夹/branch/projectName
                Path projectVersionPath=Paths.get(initPath,projectName);
                // 创建该文件夹及其所有父目录（如果不存在）
                Files.createDirectories(projectVersionPath);

                // 提取projectPath中的所有文件
                ArrayList<File> fileList=extractFiles(projectPath,initPath);
                // 调试
                for (File file : fileList) {
                    System.out.println(file.getAbsolutePath()+"\n");
                }
                System.out.println("\n\n\n\n\n");

                // 处理文件和文件夹
                for (File file : Objects.requireNonNull(fileList)) {
                    // 文件绝对路径
                    String absoluteFilePath = file.getAbsolutePath();

                    /*if (file.isDirectory()) {
                        // 获取相对路径
                        String folderRelativePath = getFileRelativePath(absoluteFilePath, projectPath);
                        // 调试
                        System.out.println("folderRelativePath="+folderRelativePath);

                        // 对应文件路径 文件夹/branch/versionName/文件
                        Path targetFolderPath = Paths.get(projectVersionPath.toString(), folderRelativePath);
                        // 调试
                        System.out.println("targetFolderPath="+targetFolderPath);

                        File targetFolder = new File(targetFolderPath.toString());
                        if (!targetFolder.exists()) {
                            if(!targetFolder.mkdirs()){
                                throw new Exception("wrong");// 如果不存在，创建该文件夹
                            }
                        }
                        // 将文件夹名称保存到 projectVersion
                        tempProjectVersion.addFolderName(folderRelativePath);

                    } else if (file.isFile()) {*/
                        // 计算文件的相对路径
                        String relativePath = getFileRelativePath(absoluteFilePath, projectPath);
                        // 计算hashcode
                        String hashCode = calculateHashCode(file);
                        System.out.println(hashCode);
                        // 获取hashCode对应的fileversion
                        FileVersion newFileVersion = hashCodeToFile.get(hashCode);

                        // 如果没有保存的版本
                        if (newFileVersion == null) {
                            // 创建新的 FileVersion
                            Path newPath=Paths.get(projectVersionPath.toString(),relativePath);
                            // 将文件复制到新路径

                            Files.createDirectories(newPath.getParent());
                            Files.copy(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);


                            newFileVersion = new FileVersion(hashCode, newPath.toString(), explanation, timestamp);
                            // 更新 hashCodeToFile 的映射
                            hashCodeToFile.put(hashCode, newFileVersion);

                        }

                        // 在 fvManager 中增加对应的映射
                        fvManager.addFileVersion(relativePath, hashCode);

                        // 在版本控制中添加对应映射
                        tempProjectVersion.addFileName(relativePath, hashCode);
                    //}
                }

                // 将项目版本保存到 pvManager
                pvManager.addProjectVersion(tempProjectVersion);

            }
            catch (Exception e) {
                // 出现错误时要检查并清空 initPath/projectName 的整个文件夹
                System.out.println(e.getMessage());
                clearProjectFolder(initPath, projectName);
                throw e; // 重新抛出异常
            }
        }
        else{
            throw new Exception("版本名称已存在");
        }

    }


    // 在路径下保存版本索引
    public void saveVersionControlSystem(String savingPath) throws Exception {
        // 创建完整的保存路径
        String combinedPath = Paths.get(savingPath, filePath).toString();

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



            // 使用 ObjectOutputStream 进行对象序列化
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                // 保存 hashCodeToFile 的映射
                oos.writeObject(hashCodeToFile);

                // 保存 pvManager
                oos.writeObject(pvManager);

                // 保存 fvManager
                oos.writeObject(fvManager);
            }

        } catch (IOException e) {
            throw new Exception("保存版本控制系统时出错: " + e.getMessage(), e);
        }
    }

    // 在路径下获取版本索引 此处输入文件夹/branch/filePath
    public void loadVersionControlSystem(String savingPath) throws Exception {
        String combinedPath = Paths.get(savingPath, filePath).toString();

        // 检查路径是否存在
        File file = new File(combinedPath);
        if (!file.exists()) {
            // 如果路径不存在，初始化为空的管理对象
            pvManager = new ProjectVersionManagement();
            fvManager = new FileVersionManagement();
            System.out.println("路径不存在，已初始化为空的版本管理对象.");
            return; // 直接返回
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 加载 hashCodeToFile 的映射
            hashCodeToFile.clear(); // 清空现有映射
            Object obj = ois.readObject();
            @SuppressWarnings("unchecked")
            Map<String, FileVersion> versionMap = (Map<String, FileVersion>) obj;
            hashCodeToFile.putAll(versionMap);


            // 加载 pvManager
            pvManager = (ProjectVersionManagement) ois.readObject();

            // 加载 fvManager
            fvManager = (FileVersionManagement) ois.readObject();

            // 调试
            Map<String, ProjectVersion> versions = pvManager.getVersions();

            for (Map.Entry<String, ProjectVersion> entry : versions.entrySet()) {
                String key = entry.getKey();
                ProjectVersion value = entry.getValue();

                // 输出键和值
                System.out.println("Key: " + key + ", Value: " + value);

                // 获取并输出 projectVersion 中的 fileNames
                Map<String, String> fileNames = value.getFileNames();  // 假设有 getFileNames() 方法

                // 遍历并输出 fileNames 的键值对
                for (Map.Entry<String, String> fileEntry : fileNames.entrySet()) {
                    String fileKey = fileEntry.getKey();
                    String fileName = fileEntry.getValue();

                    // 输出 fileNames 中的键和值
                    System.out.println("  File Key: " + fileKey + ", File Name: " + fileName);
                }
            }

            System.out.println(pvManager);
            System.out.println(fvManager);
            System.out.println("调试结束\n\n\n\n");
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("加载版本控制系统时出错: " + e.getMessage(), e);
        }


    }


    // 获取所有版本名称
    public ArrayList<String> getVersions(){
        return pvManager.getVersionNames();
    }

    // 获取某版本下的某文件的存储路径
    public String getFileVersion(String versionName,String fileName){
        try{
            ProjectVersion pv= pvManager.getVersion(versionName);
            String hashCode=pv.getFileNames().get(fileName);
            FileVersion fv=hashCodeToFile.get(hashCode);
            return fv.getFileVersionSavedPath();// 获取文件存储路径
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }


    }



}


