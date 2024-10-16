package com.example.demo_1;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import VersionStorage.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import VersionStorage.FileManageHelper.*;


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
                    // String projectDir_1=Paths.get(projectDir,"src").toString();
                    // 打印当前工作文件夹路径 完整路径
                    System.out.println("Current working directory: " + projectDir);


                    String savingPath=Paths.get(projectDir,".plugin").toString();
                    System.out.println("Saving path: " + savingPath);

                    // 首先 load
                    versionController.loadVersionControlSystem(savingPath);
                    System.out.println("load success");

                    // 然后save
                    //versionController.saveProject(savingPath,projectDir,"version_5","test_1");
                    System.out.println("保存文件版本成功");

                    // 然后save版本控制索引
                    //versionController.saveVersionControlSystem(savingPath);
                    System.out.println("saveSuccess");

                    //File file_1=new File("F:\\ThirdGrade\\java\\untitled\\.plugin\\version_4\\src\\newName.java");
                    //String hashCode_1=FileManageHelper.calculateHashCode(file_1);
                    //System.out.println("hashCode 1: " + hashCode_1);
                    //File file_2=new File("F:\\ThirdGrade\\java\\untitled\\.plugin\\version_3\\src\\Main.java");
                    //String hashCode_2=FileManageHelper.calculateHashCode(file_2);
                    //System.out.println("hashCode 2: " + hashCode_2);

                    // 然后输出某一版本的对应文件存储位置
                    //String path= versionController.getFileVersion("version_1",".gitignore");
                    //System.out.println(path);
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
