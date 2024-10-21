package com.example.hello;

import org.assertj.core.util.diff.Delta;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDiff {

    private final Patch<String> patch;

    // 构造函数传入两个文件路径并进行初始化对比
    public FileDiff(String filePath1, String filePath2) throws IOException {
        List<String> file1Lines = FileReader.ReadFile(filePath1);
        List<String> file2Lines = FileReader.ReadFile(filePath2);
        patch = DiffUtils.diff(file1Lines, file2Lines);
    }

    // 判断两个文件是否有不同
    public boolean hasDifferences() {
        return !patch.getDeltas().isEmpty();
    }

    // 获取不同之处的数量
    public int getDifferenceCount() {
        return patch.getDeltas().size();
    }

    // 如果没有区别，返回null；如果有区别，返回区别列表
    public List<Map<String, Object>> getDifferences() {
        return parseDifferences();
    }

    private List<Map<String, Object>> parseDifferences() {
        if (!hasDifferences()) {
            return null;
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Delta<String> delta : patch.getDeltas()) {

            Map<String, Object> diffEntry = new HashMap<>();
            List<String> original = delta.getOriginal().getLines();
            List<String> revised = delta.getRevised().getLines();
            int originalPos = delta.getOriginal().getPosition();
            int revisedPos = delta.getRevised().getPosition();

            // 获取行号列表
            List<Integer> originalLineNumbers = new ArrayList<>();
            for (int i = 0; i < original.size(); i++) {
                originalLineNumbers.add(originalPos + i);
            }

            List<Integer> revisedLineNumbers = new ArrayList<>();
            for (int i = 0; i < revised.size(); i++) {
                revisedLineNumbers.add(revisedPos + i);
            }

            switch(delta.getType()) {
                case INSERT:
                    diffEntry.put("type", "add");
                    diffEntry.put("position", revisedPos);
                    diffEntry.put("lines", revised);
                    diffEntry.put("line_numbers", revisedLineNumbers);  // 添加行号信息
                    result.add(diffEntry);
                    break;
                case DELETE:
                    diffEntry.put("type", "delete");
                    diffEntry.put("position", originalPos);
                    diffEntry.put("lines", original);
                    diffEntry.put("line_numbers", originalLineNumbers);  // 添加行号信息
                    result.add(diffEntry);
                    break;
                case CHANGE:
                    // 为了方便前端标记修改，额外标识出修改的块
                    Map<String, Object> changeEntry = new HashMap<>();
                    changeEntry.put("type", "change");
                    changeEntry.put("original_position", originalPos);
                    changeEntry.put("revised_position", revisedPos);
                    changeEntry.put("original_lines", original);
                    changeEntry.put("revised_lines", revised);
                    changeEntry.put("original_line_numbers", originalLineNumbers);  // 添加原始行号信息
                    changeEntry.put("revised_line_numbers", revisedLineNumbers);    // 添加修改后行号信息
                    result.add(changeEntry);
                    break;
            }
        }

        System.out.println(result);

        return result;
    }


    public static void main(String[] args) throws IOException {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        FileDiff res = new FileDiff("D:\\JavaProject\\demo\\src\\main\\java\\VersionStorage\\VersionSystem.java",
                "D:\\JavaProject\\demo\\src\\main\\java\\VersionStorage\\ProjectVersionManagement.java");

        int count = res.getDifferenceCount();
        System.out.println(count);
        System.out.println(res.hasDifferences());

        System.out.println(res.getDifferences());
    }
}
