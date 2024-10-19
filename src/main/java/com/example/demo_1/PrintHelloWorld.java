package com.example.demo_1;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

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

    private final VersionController versionController= new VersionControlSystem();

    @Override
    public void actionPerformed(AnActionEvent e){
        try{
            Project project = e.getProject();
            if (project != null) {
                // 获取项目的根目录
                String projectDir = Paths.get(project.getBasePath(),"src").toString();


                if (projectDir != null) {

                    String path = project.getBasePath();

                    // 获取
                    versionController.loadVersionControlSystem(path);
                    System.out.println("load success");


                    // 存储版本
                    versionController.saveProject("111", path,projectDir,"115","115");
                    //System.out.println("save file success");

                    // 开始
                    versionController.saveVersionControlSystem(path);
                    //System.out.println("success");

                    // 获取所有分支名称
                    var v1=versionController.getBranches();
                    var v2=versionController.getFileVersion("111","114","Main.java",path);
                    var v3=versionController.getVersions("111");
                    var v4=versionController.getVersionFileDiffs("111","114");
                    var v5=versionController.getFiles(path,"111","113");
                    System.out.println("c");
                    //var temp=versionController.getVersions("111");

                    //var temp_1=versionController.getFileVersions("111","src\\Hello.java");

                    //System.out.println(temp_1);



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
