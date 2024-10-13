package com.example.hello;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.Icon;
import com.intellij.openapi.util.IconLoader;




public class Code_Version_Control extends AnAction {


    //构造函数，用于给插件设置图标，方便找到以及美观
    public Code_Version_Control()
    {
        super("Our Code Version Control",
                "A plugin we designed to track the change of code and store different code version",
                IconLoader.getIcon("/Icons/code_version.png", Code_Version_Control.class));
    }

    @Override

    //点击插件后执行的函数
    public void actionPerformed(AnActionEvent e)
    {
        // TODO: insert action logic here
        System.out.println("芜湖起飞，你好世界");
        return;
    }
}
