package com.example.hello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Top_Interface extends JPanel {

    String currentBranch = "Main"; // 当前分支名称
    private JLabel branchLabel; // 用于显示当前分支的标签

    private Button_Listener listener; //监听器，与主类沟通

    ArrayList<String> branchNames;   //存储分支名字
    ArrayList<JButton> branchButtons; //显示分支按钮

    public Top_Interface(Button_Listener listener,Set<String>branch_init) {
        // 设置布局和样式

        this.listener=listener; //用于回调

        setLayout(new BorderLayout());
        setBackground(new Color(50, 48, 48));  // 整个面板的背景设置为黑色
        setPreferredSize(new Dimension(0, 80)); // 设置顶部高度，去除不需要的下方内容区域
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        // 左侧显示当前分支名称
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(50, 48, 48));  // 保持黑色背景
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        branchLabel = new JLabel("Branch: " + currentBranch);
        branchLabel.setForeground(Color.WHITE);
        branchLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        leftPanel.add(branchLabel);


        //中间新建分支和合并分支按钮
        JPanel midPanel = new JPanel ();
        midPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton BranchBuild = new JButton("New Branch");
        CustomizeMidButton(BranchBuild,new Color(36, 27, 216));
        midPanel.add(BranchBuild);
        JButton BranchMerge = new JButton("Merge Branch");
        CustomizeMidButton(BranchMerge,new Color(111, 59, 227));
        midPanel.add(BranchMerge);




        // 右侧显示多个分支按钮
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(50, 48, 48)); // 保持黑色背景
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 分支读取
        Set<String> branchCopy = new HashSet<>(branch_init);
        branchNames =new ArrayList<>(branchCopy);
        branchButtons = new ArrayList<>();
        if(branchNames.isEmpty())
        {
            System.out.println("空分支，自行建立一个Main分支");
            branchNames.add(currentBranch);
        }
        System.out.println("下面输出所有分支名字：");
        System.out.println(branchNames);



        // 创建每个分支按钮
        for (int i = 0; i < branchNames.size(); i++) {
            branchButtons.add(new JButton(branchNames.get(i)));
            customizeButton(branchButtons.get(i), branchNames.get(i));
            rightPanel.add(branchButtons.get(i));
        }

        for(JButton button: branchButtons)
            if(button.getText().equals(currentBranch))
               button.setBackground(new Color(63, 60, 246));//默认选中主分支



        /*------中间按钮触发函数---------------*/

        //新建分支函数
        BranchBuild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String input = JOptionPane.showInputDialog(null, "Name your New Branch", "New Branch", JOptionPane.QUESTION_MESSAGE);
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
                    System.out.println("尝试添加分支："+input);
                    if(branchNames.size()>=9)
                        JOptionPane.showMessageDialog(null, "Error: Too many Branches!", "Error", JOptionPane.ERROR_MESSAGE);

                    else if(if_name_same(input,branchNames))
                        JOptionPane.showMessageDialog(null, "Error: The same name is not allowed!", "Error", JOptionPane.ERROR_MESSAGE);

                    else
                    {
                        JOptionPane.showMessageDialog(null, "Branch Build Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        branchNames.add(input);
                        branchButtons.add(new JButton(branchNames.getLast()));
                        customizeButton(branchButtons.getLast(),branchNames.getLast());
                        rightPanel.add(branchButtons.getLast());
                        rightPanel.revalidate();
                        rightPanel.repaint();
                        listener.Branch_Build(input);
                    }


                }
                // 检查中文字符的正则表达式

            }
        });

        //分支合并函数
        BranchMerge.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // 创建一个新的对话框
                JDialog dialog = new JDialog();
                dialog.setTitle("Choose a Branch to Merge Into:"+currentBranch);
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(null); // 居中显示
                dialog.setLayout(new FlowLayout());

                // 在对话框中添加按钮
                for (String branch : branchNames) {
                    if (!branch.equals(currentBranch)) {
                        JButton branchButton = new JButton(branch);
                        branchButton.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent event)
                            {
                                // 处理分支合并逻辑
                                System.out.println("Merging into branch: " + branch);
                                // 在这里可以添加实际的合并逻辑
                                dialog.dispose(); // 关闭对话框
                                listener.Merge_Click(currentBranch,branch);
                            }
                        });
                        dialog.add(branchButton); // 将按钮添加到对话框
                    }
                }

                dialog.setVisible(true); // 显示对话框
            }
        });

        /*------中间按钮触发函数---------------*/



        // 将左侧和右侧的面板添加到顶部面板
        add(leftPanel, BorderLayout.WEST);
        add(midPanel,BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // 自定义按钮外观和行为
    private void customizeButton(JButton button, String branchName) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(102, 200, 243));
        button.setMinimumSize(new Dimension(50, 40)); // 最小尺寸
        button.setMaximumSize(new Dimension(100, 40)); // 最大尺寸
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        // 添加按钮点击事件，切换页面并更新当前分支名称
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Objects.equals(currentBranch, branchName)) {
                    currentBranch = branchName; // 更新当前分支名称
                    branchLabel.setText("Branch: " + currentBranch); // 更新左上角的分支名称显示


                    if (listener != null)
                        listener.Branch_Change(branchName);
                    else
                        System.out.println("没传入");
                    //同时用颜色突出选中的分支
                    for (JButton btn : branchButtons) {
                        btn.setBackground(new Color(102, 200, 243)); // 恢复默认背景色
                    }
                    button.setBackground(new Color(63, 60, 246)); // 自定义选中颜色
                }
                else
                    System.out.println("你已经在这个分支里面了！");
            }
        });
    }


    private void CustomizeMidButton(JButton Button,Color color)
    {
        Button.setOpaque(true);
        Button.setFocusPainted(false);
        Button.setFont(new Font("Arial", Font.PLAIN, 18));
        Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Button.setPreferredSize(new Dimension(120,40));
        Button.setBackground(color);
    }

    //判断名字是否重复函数
    private boolean if_name_same(String input,ArrayList<String> target)
    {
        for(int i=0;i<target.size();i++)
            if(input.equals(target.get(i)))
                return true;
        return false ;
    }
}
