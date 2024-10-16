package com.example.demo_1;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import GitSystem.*;
import com.intellij.openapi.project.Project;

import java.nio.file.Paths;

public class gitTest extends AnAction {

    GitSystem gitSystem=new GitSystem();
    @Override
    public void actionPerformed(AnActionEvent e) {
        try{
            // TODO: insert action logic here
            Project project = e.getProject();
            String projectDir = project.getBasePath();
            projectDir = Paths.get(projectDir).normalize().toString();  // 标准化路径

            // 测试通过
            gitSystem.checkAndInitRepository(projectDir);
            System.out.println("生成完成");

            //gitSystem.initialCommit("test_1");
            // 创建新分支
            gitSystem.createBranch("LiTong");
            System.out.println("创建完成");

            // 提交当前版本到指定分支
            gitSystem.commitToBranch("LiTong","test2");

            // 获取上次提交的所有文件
            gitSystem.getLatestCommitFiles();

            System.out.println("提交完成");



        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }



    }
}
