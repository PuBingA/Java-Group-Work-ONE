package com.example.hello;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Left_Interface extends JPanel
{
    String [] Changes_Code ;   //存放更改的条目
    String [] History_Code ;   //存放历史版本的条目
    JList<String> Changes_List;
    JList<String> History_List;
    DefaultListModel<String> Changes_Model;
    DefaultListModel<String> History_Model;
    Color Left_Color = new Color(193, 167, 221);

    private Button_Listener listener;  //回调接口

    public Left_Interface(Button_Listener listener)
    {
        this.listener=listener; //用于回调

        setPreferredSize(new Dimension(200, 0));
        setBackground(Left_Color);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 垂直布局
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));



        // 选中变化或者历史按钮
        JPanel Tab_Panel = new JPanel();
        Tab_Panel.setLayout(new GridLayout(1, 2));
        JButton  Changes_Tab = new JButton("Changes");
        JButton History_Tab = new JButton("History");
        customizeButton(Changes_Tab,0,0,1,1,Left_Color,100,40);
        customizeButton(History_Tab,0,0,1,0,Left_Color,100,40);
        Changes_Tab.setBackground(new Color(111, 59, 227));



        //中间显示内容
        CardLayout Card_Layout = new CardLayout();
        JPanel Content_Panel = new JPanel(Card_Layout);
        Changes_Model = new DefaultListModel<>();
        History_Model = new DefaultListModel<>();
        Changes_List = new JBList<>();
        History_List =new JBList<>();
        JScrollPane Changes_Scroll = new JBScrollPane(Changes_List);
        JScrollPane History_Scroll = new JBScrollPane(History_List);
        History_List.setModel(History_Model);
        Changes_List.setModel(Changes_Model);

        Changes_Code = new String[]
        {
                "Change 1: Update README",
                "Change 2: Fix bug in main()",
                "Change 3: Improve performance of data processing",
                "Change 4: Add error handling",
                "Change 5: Refactor code structure",
                "Change 6: Update dependencies",
                "Change 7: Improve logging",
                "Change 8: Add unit tests",
                "Change 9: Update documentation",
                "Change 10: Enhance UI design",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
                "Change 11: wuhuairline",
        }; //示例数据
        Content_Value_Change(Changes_Code,Changes_Model);

        History_Code = new String[]
        {
                "History 1: Initial commit",
                "History 2: First feature implemented",
                "History 3: Bug fixes and improvements",
                "History 4: Performance optimizations",
                "History 5: Security updates"
        };//示例数据
        Content_Value_Change(History_Code,History_Model);
        Content_Panel.add(Changes_Scroll, "Changes");
        Content_Panel.add(History_Scroll, "History");

        Changes_List.setBackground(new Color (255, 255, 255));
        History_List.setBackground(new Color (255, 255, 255));
        Font List_Font = new Font("Times New Roman", Font.PLAIN, 15);
        Changes_List.setForeground(Color.BLACK);
        History_List.setForeground(Color.BLACK);
        Changes_List.setFont(List_Font);
        History_List.setFont(List_Font);


        // ------触发函数-------
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
        add(Content_Panel,BorderLayout.CENTER);







        //  提交按钮
        JPanel Button_Panel = new JPanel();
        Button_Panel.setLayout(new GridLayout(1, 1, 0, 0));
        JButton Commit_Button = new JButton("Commit!");
        customizeButton(Commit_Button,1,0,0,0,new Color(102, 200, 243),200,50);
        Button_Panel.add(Commit_Button);


        // ------回调函数-------
        Commit_Button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String input = JOptionPane.showInputDialog(null, "Name your commit", "Commit Your Code", JOptionPane.QUESTION_MESSAGE);
                if(input==null)
                    return;   //用户点击退出和取消直接返回
                if (input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please input English", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }// 用户输入为空，直接返回


                if (input.matches(".*[\\u4e00-\\u9fa5]+.*"))
                    JOptionPane.showMessageDialog(null, "Error: Please use English", "Error", JOptionPane.ERROR_MESSAGE);
                else
                {
                    JOptionPane.showMessageDialog(null, "Commit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(input);
                }
                // 检查中文字符的正则表达式

            }
        });
        // ------回调函数-------

        add(Button_Panel,BorderLayout.SOUTH); // 将按钮面板添加到左侧面板
    }

    // 按钮外观函数
    private void customizeButton(JButton button,int top,int left,int bottom,int right,Color color,int width,int height) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK)); // 边框
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(width, height)); // 设置大小
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); //鼠标变手势
        button.setFont(new Font("Arial", Font.PLAIN, 20));
    }

    //根据读取到的字符串来进行显示内容的赋值
     public void Content_Value_Change(String [] data,DefaultListModel<String> target)
     {
           for(String element : data)
           {
               target.addElement(element);
           }
     }

}
