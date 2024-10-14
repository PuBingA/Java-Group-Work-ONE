package com.example.hello;
import javax.swing.*;
import java.awt.*;

public class Right_Interface extends JPanel {
    private JPanel historyContainer;       // 历史记录容器
    private JPanel comparisonContainer;    // 文本对比容器
    public boolean isHistory = false;     // 控制状态的布尔值

    public Right_Interface() {
        setLayout(new BorderLayout()); // 设置布局
        setBackground(new Color(255, 255, 255)); // 设置背景颜色

        // 创建历史记录的容器
        historyContainer = new JPanel();
        historyContainer.setBackground(Color.LIGHT_GRAY); // 设置背景颜色
        historyContainer.add(new JLabel("这是历史记录的内容", SwingConstants.CENTER));

        // 创建文本对比的容器
        comparisonContainer = new JPanel();
        comparisonContainer.setBackground(Color.WHITE); // 设置背景颜色
        comparisonContainer.add(new JLabel("文本对比的内容", SwingConstants.CENTER));

        // 初始显示文本对比的容器
        add(comparisonContainer, BorderLayout.CENTER);
    }

    // 方法：切换内容状态
    public void toggleContent() {
        isHistory = !isHistory; // 切换布尔值

        // 根据状态切换容器的可见性
        if (isHistory) {
            remove(comparisonContainer); // 移除对比容器
            add(historyContainer, BorderLayout.CENTER); // 添加历史记录容器
        } else {
            remove(historyContainer); // 移除历史记录容器
            add(comparisonContainer, BorderLayout.CENTER); // 添加对比容器
        }

        // 刷新界面
        revalidate();
        repaint();
    }
}