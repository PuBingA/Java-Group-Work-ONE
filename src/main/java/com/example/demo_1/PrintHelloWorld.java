package com.example.demo_1;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import VersionStorage.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class PrintHelloWorld extends AnAction {

    private final VersionController versionController= new VersionSystem();

    @Override
    public void actionPerformed(AnActionEvent e){
        try{
            Project project = e.getProject();
            if (project != null) {
                // 获取项目的根目录
                String projectDir = project.getBasePath();


                if (projectDir != null) {

                    projectDir = Paths.get(projectDir).normalize().toString();  // 标准化路径
                    //String projectDir_1=Paths.get(projectDir,"src").toString();
                    // 打印当前工作文件夹路径 完整路径
                    System.out.println("Current working directory: " + projectDir);

                    // 首先 load
                    String savingPath=Paths.get(projectDir,".plugin").toString();
                    System.out.println("Saving path: " + savingPath);


                    versionController.loadVersionControlSystem(savingPath);
                    System.out.println("load success");

                    // 然后save
                    versionController.saveProject(savingPath,projectDir,"version_1","test_1");
                    System.out.println("保存文件版本成功");

                    // 然后save
                    versionController.saveVersionControlSystem(savingPath);
                }
            } else {
                System.out.println("No active project found.");
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }
}
