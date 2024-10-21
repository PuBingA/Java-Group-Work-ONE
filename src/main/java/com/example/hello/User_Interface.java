 package com.example.hello;
import CodeChangeMonitor.FileUtils;
import VersionStorage.FileChangeType;
import VersionStorage.VersionControlSystem;
import VersionStorage.VersionController;
import com.github.weisj.jsvg.M;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import kotlinx.datetime.Clock;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.List;


 public class User_Interface implements Button_Listener
 {
     //3个子部件
    Left_Interface Left;
    Right_Interface Right;
    Top_Interface Top;

    private MessageBusConnection connection;
    String Now_Branch_Name;//表示现在所处的分支
    private Project project;

    String basePath;   //表示项目根目录
    String savingPath; //表示存储json目录
    String FilePath ;
    VersionControlSystem Code_Version_System;  //存储系统类

    ArrayList<String>History_version;  //存储当前分支下的所有版本名字

     FileUtils Whole_File;  // 检测当前项目下所有文件
     Map<String,String> File_Map;  //存放源项目所有文件
     Map<String,String> Target_Map; //存放对比对象的所有文件
     Map<String,FileChangeType> Result_Map; //存放对比结果涉及的文件和类型
    public User_Interface(Project project)
    { // 添加 Project 参数
        JFrame Frame = new JFrame("Code Version Control");
        Frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Frame.setSize(1180, 800);
        Frame.setLayout(new BorderLayout());

        //存储系统读取
        Code_Version_System = new VersionControlSystem();
        this.project =project;
        basePath=project.getBasePath();
        savingPath=basePath;
        FilePath= new String(basePath+"/src");
        try {
            Code_Version_System.loadVersionControlSystem(savingPath);
            System.out.println("存储读取成功!");
        } catch (Exception e) {
            System.out.println("存储读取失败！" + e.getMessage());
        }


       //所有分支读取
        Set <String> branch_init = new HashSet<>();
       System.out.println("初始输出所有分支：");
        try {
            branch_init = new HashSet<>(Code_Version_System.getBranches());
            System.out.println(branch_init);
            System.out.println("分支读取成功!");
        } catch (Exception e) {
            System.out.println("分支读取失败！" + e.getMessage());
        }



        //上部分栏目,放置切换分支按钮等
        Top = new Top_Interface(this,branch_init);
        Now_Branch_Name=Top.currentBranch;
        Frame.add(Top, BorderLayout.NORTH);


        //初始化Main分支下的历史版本
        History_version =new ArrayList<String>();
       try
       {
           History_version = new ArrayList<>(Code_Version_System.getVersions(Now_Branch_Name));  //返回改分支下所有历史版本
       } catch (Exception ex) {
           System.out.println("获取历史版本失败: " + ex.getMessage());
       }
       System.out.println("当前分支下有版本：");
       System.out.println(History_version);

        //源项目文件初始化
        Whole_File = new FileUtils();
        File_Map=Whole_File.getFiles(FilePath);
        Target_Map =new HashMap<>();
        try
        {
           Target_Map =new HashMap<>(Code_Version_System.getLastVersionFiles(basePath,Now_Branch_Name));
        } catch (Exception ex) {
            System.out.println("获取当前分支下最新版本文件失败" + ex.getMessage());
        }
        System.out.println("测试Target_Map读取结果");
        System.out.println(Target_Map);
        System.out.println("测试File_Map读取结果");
        System.out.println(File_Map);
        Result_Map = new HashMap<>();
        Result_Map = new HashMap<>(Changes_get(File_Map,Target_Map));







        //下侧总内容
        JPanel Center_Panel = new JPanel();
        Center_Panel.setLayout(new BorderLayout());
        Center_Panel.setBackground(new Color(255, 255, 255));

        // 左侧选择查看变化以及历史提交版本
        Left = new Left_Interface(this,History_version, Result_Map);
        Center_Panel.add(Left, BorderLayout.WEST); // 添加左侧菜单栏
        // 右侧显示文本对比
        Right = new Right_Interface(this);
        Center_Panel.add(Right, BorderLayout.CENTER);
        Left.Commit_Button.setEnabled(!Result_Map.isEmpty());


        Frame.add(Center_Panel, BorderLayout.CENTER);
        Frame.setVisible(true);
        Frame.setLocationRelativeTo(null);

        //窗口关闭时，进行相关数据结构的保存
        Frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 在窗口关闭时执行存储操作
                System.out.println("窗口关闭，执行存储操作...");
                try {
                    Code_Version_System.saveVersionControlSystem(savingPath);
                    System.out.println("项目保存成功！");
                } catch (Exception ex) {
                    System.out.println("保存项目失败: " + ex.getMessage());
                }
                // 关闭窗口
                Frame.dispose();  // 释放资源并关闭窗口
            }
        });


        // 注册文件变化监听器
        registerFileChangeListener(project);
        System.out.println("Started monitoring file changes...");
    }


    //检测到变化要做的事情在这里
    private void registerFileChangeListener(Project project)
    {
        connection = project.getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events)
                {
                    //进行新的赋值
                    File_Map=Whole_File.getFiles(FilePath);
                    System.out.println("测试Target_Map读取结果");
                    System.out.println(Target_Map);
                    System.out.println("测试File_Map读取结果");
                    System.out.println(File_Map);

                    Result_Map=new HashMap<>(Changes_get(File_Map,Target_Map));
                    Left.updateChangesList(Result_Map);
                    Left.Commit_Button.setEnabled(!Result_Map.isEmpty());
                }
            }
        });
    }

    //按钮点击方法的实现，用于每个前端部分数据的交换对接
    public void Changes_Click()
    {
        System.out.println("Changes tab clicked.");
        Right.showChangesView();
    }

    public void History_Click()
    {
        System.out.println("History tab clicked.");
        Right.showHistoryView();
    }

    private void Changes_Update()
    {
        try
        {
            Target_Map =new HashMap<>(Code_Version_System.getLastVersionFiles(basePath,Now_Branch_Name));
        } catch (Exception ex) {
            System.out.println("获取当前分支下最新版本文件失败" + ex.getMessage());
        }
        Result_Map = new HashMap<>(Changes_get(File_Map,Target_Map));
        Left.updateChangesList(Result_Map);
        Left.Commit_Button.setEnabled(!Result_Map.isEmpty());
    }//更新Changes的功能函数

    //贡献后的回调
    public void Commit_Click(String VerSionName)
    {
        // 保存项目
        System.out.println("Commit button clicked.");
        String projectPath = basePath+"/src"; //获取项目路径
        System.out.println("提取源码路径为："+projectPath);
        String projectName = VerSionName;
        String explanation = "This is a commit."; // 提交说明等
        String initPath = basePath;  //按分支和版本存储;
        try {

            Code_Version_System.saveProject(Now_Branch_Name,initPath, projectPath, projectName, explanation);
            System.out.println("该版代码成功保存!");
            History_version = new ArrayList<>(Code_Version_System.getVersions(Now_Branch_Name));
        } catch (Exception e) {
            System.out.println("Error saving project: " + e.getMessage());
        }

        //更新历史
        System.out.println("下面输出该分支所有版本名字：");
        System.out.println(History_version);
        Left.History_Code=new ArrayList<String>(History_version);
        Left.Content_Value_Change(Left.History_Code,Left.History_Model);

        //更新changes
        Changes_Update();
    }

    //新建分支
    public void Branch_Build(String New_Branch_Name)
    {
        try
        {
            Code_Version_System.createNewBranch(New_Branch_Name,Now_Branch_Name);
        } catch (Exception e)
        {
            System.out.println("新分支初始化失败！");
        }

        System.out.println("在 "+Now_Branch_Name+" 上新建了分支 ： "+New_Branch_Name);
    }

    //分支切换后的回调
    public void Branch_Change(String newBranch)
    {
        //更改分支
        Now_Branch_Name = newBranch;
        System.out.println("Branch changed to: "+Now_Branch_Name );

        //获取该分支的所有历史
        try
        {
            History_version = new ArrayList<>(Code_Version_System.getVersions(Now_Branch_Name)); //返回改分支下所有历史版本
        } catch (Exception ex) {
            System.out.println("获取历史版本失败: " + ex.getMessage());
        }
        Left.History_Code=new ArrayList<String>(History_version);
        Left.Content_Value_Change(Left.History_Code,Left.History_Model);

        //更新changes
        Changes_Update();

        //清空对比页面
        Right.Clean_View();
    }

    //合并分支后的回调
     public void Merge_Click(String Be_Merged_Name,String Merge_Name)
     {
         String input = JOptionPane.showInputDialog(null, "Name your commit", "Commit Your Code", JOptionPane.QUESTION_MESSAGE);
         if (input == null)
             return;   //用户点击退出和取消直接返回
         if (input.trim().isEmpty()) {
             JOptionPane.showMessageDialog(null, "Please input English", "Warning", JOptionPane.WARNING_MESSAGE);
             return;
         }// 用户输入为空，直接返回


         if (input.matches(".*[\\u4e00-\\u9fa5]+.*")) //中文不允许
             JOptionPane.showMessageDialog(null, "Error: Please use English", "Error", JOptionPane.ERROR_MESSAGE);
         else if(Left.IfNameExist(input,Left.History_Code))
             JOptionPane.showMessageDialog(null, "Error: Duplicate names are not allowed", "Error", JOptionPane.ERROR_MESSAGE);
         else
         {
             Map<String,FileChangeType> Get_File_Change_Type = new HashMap<>();
             Map<String,String>Decide_File_Push = new HashMap<>();
             try
             {
                 // 如果没必要合并 直接走掉
                 try{
                     Get_File_Change_Type = new HashMap<>(Code_Version_System.getFileDiffsBetweenBranches(Be_Merged_Name,Merge_Name));
                 }
                 catch(Exception e){
                     JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }


                 for(Map.Entry<String, FileChangeType> entry : Get_File_Change_Type.entrySet())
                 {
                     String File_Name = entry.getKey();
                     FileChangeType Change_Type = entry.getValue();
                     if (Change_Type == FileChangeType.MODIFIED)
                         Decide_File_Push.put(File_Name, "2");
                 }

                 System.out.println("决定合并的文件如下：");
                 System.out.println(Decide_File_Push);
                 JOptionPane.showMessageDialog(null, "Merge successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                 Code_Version_System.mergeBranches(input,Be_Merged_Name,Merge_Name,Decide_File_Push);

                 //更新changes
                 Changes_Update();

                 //更新History
                 History_version = new ArrayList<>(Code_Version_System.getVersions(Now_Branch_Name)); //返回改分支下所有历史版本
                 Left.History_Code=new ArrayList<String>(History_version);
                 Left.Content_Value_Change(Left.History_Code,Left.History_Model);

             } catch (Exception ex) {
                 System.out.println("合并失败！" + ex.getMessage());
             }
         }
     }

     //选中Changes列表项目后的回调
     public void Changes_List_Selected(String selectedValue)
     {
         String CodeFilePosition =new String();
         String StoreFilePosition=new String();

         System.out.println("WUWUWUUUWUWUWU");
         System.out.println(File_Map);

         for(String key: File_Map.keySet())
         {
             if(key.equals(selectedValue))
             {
                 CodeFilePosition =new String(File_Map.get(key));
                 break;
             }
         }

         System.out.println("源码的位置位于："+CodeFilePosition);

         for(String key: Target_Map.keySet())
         {
             if(key.equals(selectedValue))
             {
                 StoreFilePosition =new String(Target_Map.get(key));
                 break;
             }
         }
         System.out.println("仓库的位置位于："+StoreFilePosition);

         Right.Changes_Show_FileComparison("Latest Version :  "+selectedValue,StoreFilePosition,"My code :  "+selectedValue,CodeFilePosition);
     }

     //选中History列表项目后的回调
     public void History_List_Selected(String selectedValue)
     {

         Map<String,FileChangeType>Change_File_Map =new HashMap<>();
         //先获取该版本变化的文件名
         try
         {
             Change_File_Map=new HashMap<>(Code_Version_System.getVersionFileDiffs(Now_Branch_Name,selectedValue));
         } catch (Exception ex) {
             System.out.println("获取当前分支下最新版本文件失败" + ex.getMessage());
         }

         System.out.println("累类类");
         System.out.println(Change_File_Map);
         Right.Picked_History_Name =new String(selectedValue); //选中的History传给Right
         Right.Update_History_List(Change_File_Map);
     }

     //选中历史列表中变化文件后的回调
     public void History_Change_Selected(String Selected_History_Name,String Selected_File_Name)
     {
         System.out.println("回调成功！");
         ArrayList<String> Find_Last_Version = new ArrayList<String>();
         try {
             Find_Last_Version = new ArrayList<String>(Code_Version_System.getVersions(Now_Branch_Name));
         } catch (Exception ex)
         {
             System.out.println("获取分支上一个版本失败" + ex.getMessage());
         }

         String Last_Version_Name = new String();

         if(Find_Last_Version.size()>1)
             for(int i=0;i<Find_Last_Version.size();i++)
                 if(Find_Last_Version.get(i).equals(Selected_History_Name)&&i-1>=0)
                 {
                     Last_Version_Name = new String(Find_Last_Version.get(i-1));
                     break;
                 }
         else
             System.out.println("该版本就是第一个版本，无上一个版本");

         String Now_Version =new String();
         String Last_Version=new String();


         try {
             Last_Version = new String(Code_Version_System.getFileVersion(Now_Branch_Name, Last_Version_Name, Selected_File_Name, basePath));
         } catch (Exception ex) {
             System.out.println("获取 Last_Version 时出错：" + ex.getMessage());
             // 允许空值，继续执行
         }

         try {
             Now_Version = new String(Code_Version_System.getFileVersion(Now_Branch_Name, Selected_History_Name, Selected_File_Name, basePath));
         } catch (Exception ex) {
             System.out.println("获取 Now_Version 时出错：" + ex.getMessage());
             // 允许空值，继续执行
         }



         System.out.println("当前version下的路径在："+Now_Version);
         System.out.println("上一个版本的路径在："+Last_Version);
         Right.History_Show_FileComparison("Last Version :  "+Selected_File_Name,Last_Version, "This Version :  " + Selected_File_Name, Now_Version);

     }

     //切换分支后，比较现有代码和仓库代码,导出修改项
     public Map<String,FileChangeType> Changes_get(Map<String,String> My_Files, Map<String,String> Git_Files)
     {
         Map<String, FileChangeType> changesMap = new HashMap<>();
         // 遍历当前的 file_init 以检测新增和修改的文件
         for (Map.Entry<String, String> entry : My_Files.entrySet())
         {
             String fileName = entry.getKey();
             String currentFilePath = entry.getValue();

             if (!Git_Files.containsKey(fileName))
             {
                 changesMap.put(fileName, FileChangeType.ADDED);
             } else {
                 // 如果文件存在，比较文件内容是否不同
                 String previousFilePath = Git_Files.get(fileName);
                 try {
                     if (VersionController.areFilesDifferent(currentFilePath, previousFilePath)) {
                         // 如果文件内容不同，说明文件被修改
                         changesMap.put(fileName, FileChangeType.MODIFIED);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }

         for (String fileName : Git_Files.keySet()) {
             if (!My_Files.containsKey(fileName)) {
                 // 如果 file_init 不包含此文件，说明文件被删除
                 changesMap.put(fileName, FileChangeType.DELETED);
             }
         }

         System.out.println("接下来公布对比结果：");
         System.out.println(changesMap);
         return changesMap;
     }
}


