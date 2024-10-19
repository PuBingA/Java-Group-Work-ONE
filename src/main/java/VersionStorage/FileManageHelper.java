package VersionStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FileManageHelper {
    // 提取所有文件
    public static ArrayList<File> extractFiles(String projectPath) throws Exception {
        ArrayList<File> filesList = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(projectPath))) {
            paths.filter(Files::isRegularFile) // 仅获取文件
                    .forEach(path -> filesList.add(path.toFile())); // 将文件添加到列表中
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }

        return filesList;
    }


    public static Path getFileRelativePath(Path pathToFile,Path initPath) {
        return initPath.relativize(pathToFile);
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
