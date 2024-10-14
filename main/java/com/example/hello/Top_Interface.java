package com.example.hello;
import javax.swing.*;
import java.awt.*;

public class Top_Interface extends JPanel
{

    public Top_Interface() {
        setLayout(new BorderLayout());
        setBackground(new Color(50, 48, 48));
        setPreferredSize(new Dimension(0, 80));
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        add(new JLabel("选择分支、新建分支的功能块", SwingConstants.CENTER), BorderLayout.CENTER);
    }

}
