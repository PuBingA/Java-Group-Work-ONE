package CodeChangeMonitor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileUtils
{
    //返回文件目录下的所有.java文件
    public Map<String, String> getFiles(String directoryPath)
    {
        Map<String, String> fileMap = new HashMap<>();

        Path path = Paths.get(directoryPath);

        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".java")) // 保留 .java 文件
                    .forEach(file -> {
                        String relativePath = path.relativize(file).toString();
                        String absolutePath = file.toString();
                        fileMap.put(relativePath, absolutePath);
                    });
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }

        return fileMap;
    }
}

