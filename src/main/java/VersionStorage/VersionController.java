package VersionStorage;

import java.util.*;

public interface VersionController {

    /**
     * 保存整个版本控制系统
     *
     * @param initPath 大版本路径 如untitled
     * @throws Exception 可能抛出异常，如文件路径不存在或无法写入等
     */
    public void saveVersionControlSystem(String initPath) throws Exception;

    /**
     * 加载版本控制系统
     *
     * @param savingPath 项目路径 如untitled
     * @throws Exception 可能抛出异常，如文件未找到或格式不正确等
     */
    public void loadVersionControlSystem(String savingPath) throws Exception;

    /**
     * 根据分支和版本名获取文件版本的绝对路径
     *
     * @param branchName 要查询的分支名称
     * @param versionName 版本名称
     * @param fileName 文件名
     * @param initPath 项目路径 如untitled
     * @return 文件的绝对路径
     * @throws Exception 可能抛出异常，如找不到文件或版本信息不正确等
     * 如果对应版本文件不存在 返回null 版本或者分支不存在返回异常
     */
    public String getFileVersion(String branchName,String versionName,String fileName,String initPath) throws Exception;

    /**
     * 获取所有版本名称
     *
     * @param branchName 要查询的分支名称
     * @return 该分支下所有版本的名称列表
     * @throws Exception 可能抛出异常，如找不到分支等
     */
    public ArrayList<String> getVersions(String branchName) throws Exception;

    /**
     * 获取与上一版本相对比改变的源码
     *
     * @param branchName 要比较的分支名称
     * @param versionName 要比较的版本名称
     * @return 变化文件的相对路径和对应的更改类型
     * @throws Exception 可能抛出异常，如找不到版本或分支等
     */
    public Map<String,FileChangeType> getVersionFileDiffs(String branchName,String versionName)throws Exception;

    /**
     * 保存当前项目
     *
     * @param branchName 要保存的分支名称
     * @param initPath 项目路径 如untitled
     * @param projectPath 项目源码路径
     * @param versionName 版本名称
     * @param explanation 版本说明
     * @throws Exception 可能抛出异常，如无法保存项目等
     */
    public void saveProject(String branchName,String initPath,String projectPath,String versionName,String explanation) throws Exception;

    /**
     * 返回所有分支名称
     *
     * @return 所有分支的名称集合
     * @throws Exception 可能抛出异常，如无法获取分支信息等
     */
    public Set<String> getBranches() throws Exception;

    /**
     * 返回某分支某版本的全部文件
     *
     * @param initPath 整个项目的路径
     * @param branchName 分支名称
     * @param versionName 版本名称
     * @return 文件相对路径和文件绝对路径的对应映射
     * @throws Exception 可能抛出异常，如找不到文件或版本信息不正确等
     * 如果两个包内有一样的文件 可能相对路径和绝对路径对不上 所以返回映射
     */
    public Map<String,String> getFiles(String initPath,String branchName,String versionName) throws Exception;

}
