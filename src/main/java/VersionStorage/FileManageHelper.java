package VersionStorage;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Objects;

public class FileManageHelper {
    public static ArrayList<File> extractFiles(String projectPath, String initPath) throws Exception {
        ArrayList<File> fileList = new ArrayList<>();
        File projectDirectory = new File(projectPath);

        // 检查项目路径是否合法
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            throw new Exception("Project path is invalid or does not exist.");
        }

        // 递归提取文件
        extractFilesRecursively(projectDirectory, initPath, fileList);
        return fileList;
    }

    private static void extractFilesRecursively(File directory, String initPath, ArrayList<File> fileList) {
        // 获取当前目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if(files!=null){
            for (File file : files) {
                // 如果是目录，递归调用
                if (file.isDirectory()) {
                    extractFilesRecursively(file, initPath, fileList);
                } else {
                    // 检查文件路径是否以 initPath 开头
                    if (!file.getAbsolutePath().startsWith(initPath)) {
                        fileList.add(file); // 添加到列表中
                    }
                }
            }

        }

    }

    public static String getFileRelativePath(String pathToFile,String initPath) {

        return pathToFile.replace(initPath + File.separator, "");
    }

    // 计算文件的哈希值
    public static String calculateHashCode(File file) throws Exception {
        // 创建 SHA-256 消息摘要实例
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 读取文件内容并计算哈希值
        try (var inputStream = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int bytesRead;

            // 持续读取文件，直到没有更多字节
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead); // 更新消息摘要
            }
        }

        // 获取计算得到的哈希字节数组
        byte[] hashBytes = digest.digest();

        // 将哈希字节转换为十六进制字符串
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b)); // 转换为十六进制字符串
        }

        return hashString.toString(); // 返回哈希字符串
    }

    public static void clearProjectFolder(String initPath, String projectName) throws Exception {
        try{
            System.out.println("clear directory");
            File projectFolder = new File(initPath + File.separator + projectName);
            if (projectFolder.exists()) {
                for (File file : Objects.requireNonNull(projectFolder.listFiles())) {
                    if(!file.delete()){
                        throw new Exception("Failed to delete file " + file.getAbsolutePath());
                    }; // 删除文件
                }

                if(!projectFolder.delete()){
                    throw new Exception("Failed to delete project folder " + projectFolder.getAbsolutePath());
                } // 删除文件夹
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw e;
        }

    }


}
