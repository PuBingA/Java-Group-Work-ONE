package com.example.hello;
import CodeChangeMonitor.FileUtils;
import VersionStorage.FileChangeType;
import VersionStorage.FileManageHelper;
import VersionStorage.VersionController;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.diff.Diff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Left_Interface extends JPanel
{
    Color Left_Color = new Color(193, 167, 221);  //公用颜色配置

    //存放更改的条目
    ArrayList<String> History_Code;   //存放历史版本的条目

    //提交按钮向外提供
    JButton Commit_Button;

    //变化和历史的列表
    JList<String> Changes_List;
    JList<String> History_List;
    DefaultListModel<String> Changes_Model;
    DefaultListModel<String> History_Model;


    private Button_Listener listener;  //回调接口

    public Left_Interface(Button_Listener listener, ArrayList<String> history_init, Map<String, FileChangeType> changes_init) {
        this.listener = listener; //用于回调

        History_Code = new ArrayList<String>(history_init); //历史版本初始化

        setPreferredSize(new Dimension(200, 0));
        setBackground(Left_Color);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 垂直布局
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));


        // 选中变化或者历史按钮
        JPanel Tab_Panel = new JPanel();
        Tab_Panel.setLayout(new GridLayout(1, 2));
        JButton Changes_Tab = new JButton("Changes");
        JButton History_Tab = new JButton("History");
        customizeButton(Changes_Tab, 0, 0, 1, 1, Left_Color, 100, 40);
        customizeButton(History_Tab, 0, 0, 1, 0, Left_Color, 100, 40);
        Changes_Tab.setBackground(new Color(111, 59, 227));


        //中间显示内容
        CardLayout Card_Layout = new CardLayout();
        JPanel Content_Panel = new JPanel(Card_Layout);
        Changes_Model = new DefaultListModel<>();
        History_Model = new DefaultListModel<>();
        Changes_List = new JBList<>();
        History_List = new JBList<>();
        JScrollPane Changes_Scroll = new JBScrollPane(Changes_List);
        JScrollPane History_Scroll = new JBScrollPane(History_List);
        History_List.setModel(History_Model);
        Changes_List.setModel(Changes_Model);

        //Changes初始化
        updateChangesList(changes_init);


        Content_Value_Change(History_Code, History_Model);
        Content_Panel.add(Changes_Scroll, "Changes");
        Content_Panel.add(History_Scroll, "History");




        //统一格式设计
        History_List.setBackground(new Color(255, 255, 255));
        Font List_Font = new Font("Times New Roman", Font.PLAIN, 15);
        Changes_List.setBackground(Color.WHITE);
        Changes_List.setSelectionBackground(Color.LIGHT_GRAY);
        Changes_List.setFont(List_Font);
        History_List.setBackground(Color.WHITE);
        History_List.setSelectionBackground(Color.LIGHT_GRAY);
        History_List.setForeground(Color.GREEN);
        History_List.setSelectionForeground(Color.GREEN);
        History_List.setFont(List_Font);


        // ------选中条款触发函数-------
        Changes_List.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting())
            {
                String selectedValue = Changes_List.getSelectedValue();
                if(selectedValue!=null)
                {
                    System.out.println("巧妙的选中了：" + selectedValue);
                    listener.Changes_List_Selected(selectedValue);
                }
            }
        });

        History_List.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting())
            {
                String selectedValue = History_List.getSelectedValue();
                if(selectedValue!=null)
                {
                    System.out.println("巧妙的选中了：" + selectedValue);
                    listener.History_List_Selected(selectedValue);
                }

            }
        });



        // ------切换变化、历史按钮触发函数-------
        Changes_Tab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Changes_Tab.setBackground(new Color(111, 59, 227));
                History_Tab.setBackground(Left_Color); // 取消另一个按钮的选中状态
                Card_Layout.show(Content_Panel, "Changes");
                listener.Changes_Click();//函数回调
            }
        });

        History_Tab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                History_Tab.setBackground(new Color(111, 59, 227));
                Changes_Tab.setBackground(Left_Color); // 取消另一个按钮的选中状态
                Card_Layout.show(Content_Panel, "History");
                listener.History_Click(); //函数回调
            }
        });
        // ------触发函数-------

        Tab_Panel.add(Changes_Tab);
        Tab_Panel.add(History_Tab);
        add(Tab_Panel, BorderLayout.NORTH); // 将选项卡添加到左侧面板
        add(Content_Panel, BorderLayout.CENTER);


        //  提交按钮
        JPanel Button_Panel = new JPanel();
        Button_Panel.setLayout(new GridLayout(1, 1, 0, 0));
        Commit_Button = new JButton("Commit!");
        customizeButton(Commit_Button, 1, 0, 0, 0, new Color(102, 200, 243), 200, 50);
        Button_Panel.add(Commit_Button);


        // ------提交按钮回调函数-------
        Commit_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(null, "Name your commit", "Commit Your Code", JOptionPane.QUESTION_MESSAGE);
                if (input == null)
                    return;   //用户点击退出和取消直接返回
                if (input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please input English", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }// 用户输入为空，直接返回


                if (input.matches(".*[\\u4e00-\\u9fa5]+.*"))
                    JOptionPane.showMessageDialog(null, "Error: Please use English", "Error", JOptionPane.ERROR_MESSAGE);

                else if(IfNameExist(input,History_Code))
                    JOptionPane.showMessageDialog(null, "Error: Duplicate names are not allowed", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    JOptionPane.showMessageDialog(null, "Commit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(input);
                    listener.Commit_Click(input);
                }
                // 检查中文字符的正则表达式

            }
        });
        // ------回调函数-------

        add(Button_Panel, BorderLayout.SOUTH); // 将按钮面板添加到左侧面板
    }

    public boolean IfNameExist(String input,ArrayList<String> History_code)
    {
        for (String s : History_code)
            if (input.equals(s))
                return true;
        return false;
    }

    // 按钮外观函数
    private void customizeButton(JButton button, int top, int left, int bottom, int right, Color color, int width, int height) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK)); // 边框
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(width, height)); // 设置大小
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); //鼠标变手势
        button.setFont(new Font("Arial", Font.PLAIN, 20));
    }

    //更新History列表
    public void Content_Value_Change(ArrayList<String> data, DefaultListModel<String> target) {
        target.clear();
        if (data == null)
            System.out.println("当前没有Changes或History");

        else
            for (String element : data)
                target.addElement(element);
    }

    //根据更改MAP来改变列表颜色以及添加元素
    public void updateChangesList(Map<String, FileChangeType> changes_init)
    {
        Changes_Model.clear();
        for (Map.Entry<String, FileChangeType> entry : changes_init.entrySet()) {
            String fileName = entry.getKey();
            Changes_Model.addElement(fileName);
        }
        Changes_List.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHeight) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHeight);
                FileChangeType changeType = changes_init.get(value); // 获取变化类型
                if (changeType != null) {
                    switch (changeType) {
                        case ADDED:
                            label.setForeground(Color.GREEN);
                            break;
                        case MODIFIED:
                            label.setForeground(new Color(54, 60, 220));
                            break;
                        case DELETED:
                            label.setForeground(Color.RED);
                            break;
                    }
                }
                return label;
            }
        });

    }
}
