package com.example.hello;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class User_Interface implements Button_Listener {
    Left_Interface Left;
    private MessageBusConnection connection;

    public User_Interface(Project project) { // 添加 Project 参数
        JFrame Frame = new JFrame("Code Version Control");
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setSize(1180, 800);
        Frame.setLayout(new BorderLayout());

        //上部分栏目,放置切换分支按钮等
        Top_Interface Top = new Top_Interface();
        Frame.add(Top, BorderLayout.NORTH);

        //下侧总内容
        JPanel Center_Panel = new JPanel();
        Center_Panel.setLayout(new BorderLayout());
        Center_Panel.setBackground(new Color(255, 255, 255));

        // 左侧选择查看变化以及历史提交版本
        Left = new Left_Interface(this);
        Center_Panel.add(Left, BorderLayout.WEST); // 添加左侧菜单栏

        // 右侧显示文本对比
        Right_Interface Right = new Right_Interface();
        Center_Panel.add(Right, BorderLayout.CENTER);

        //右侧文本内容
        Frame.add(Center_Panel, BorderLayout.CENTER);

        Frame.setVisible(true);
        Frame.setLocationRelativeTo(null);

        // 注册文件变化监听器
        registerFileChangeListener(project);
        System.out.println("Started monitoring file changes...");
    }


    //注册文件变化监听器的函数
    private void registerFileChangeListener(Project project) {
        connection = project.getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    // 处理文件变化事件
                    System.out.println("File change detected: " + event);
                }
            }
        });
    }

    //按钮点击方法的实现，用于每个前端部分数据的交换对接
    public void Changes_Click() {
        System.out.println("Changes tab clicked.");
    }

    public void History_Click() {
        System.out.println("History tab clicked.");
    }

    public void Commit_Click() {
        System.out.println("Commit button clicked.");
    }
}


