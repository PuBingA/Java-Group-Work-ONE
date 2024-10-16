package VersionStorage;

public interface DiffModel {
    /**
     * 应用差异到文件上并生成新内容，同时显示差异。
     *
     * @param filePath 原文件的路径
     * @param diffPath 差异文件的路径
     * @return 返回应用差异后的文件内容
     */
    String applyDiffAndShow(String filePath, String diffPath);

    /**
     * 获取两个文件之间的差别并显示。
     *
     * @param filePath1 第一个文件的路径
     * @param filePath2 第二个文件的路径
     * @return 返回文件之间的差异内容，采用@@ 样式格式
     */
    String getDiffContent(String filePath1, String filePath2);

}
