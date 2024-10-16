package com.example.hello;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Right_Interface extends JPanel {
    private RSyntaxTextArea codeTextArea;    // 用于显示和编辑代码的文本区域

    // 构造函数
    public Right_Interface() {
        setLayout(new BorderLayout()); // 设置布局
        setBackground(Color.WHITE);    // 背景为白色

        // 顶部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);  // 设置按钮面板背景为白色

        // 创建“Copy Paste”按钮
        JButton copyPasteButton = new JButton("Copy Paste");
        customizeButton(copyPasteButton);
        buttonPanel.add(copyPasteButton);

        // 创建“File Import”按钮
        JButton fileImportButton = new JButton("File Import");
        customizeButton(fileImportButton);
        buttonPanel.add(fileImportButton);

        // 添加按钮面板到顶部
        add(buttonPanel, BorderLayout.NORTH);

        // 创建 RSyntaxTextArea 用于显示和编辑代码
        codeTextArea = new RSyntaxTextArea(20, 60);
        codeTextArea.setEditable(false); // 初始设置为不可编辑
        codeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // 使用等宽字体显示代码
        codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA); // 设定语法高亮为 Java
        codeTextArea.setCodeFoldingEnabled(true); // 启用代码折叠
        codeTextArea.setForeground(Color.BLUE.darker()); // 设置字体颜色为深蓝色
        codeTextArea.setBackground(Color.WHITE);  // 文本区域背景为白色

        RTextScrollPane scrollPane = new RTextScrollPane(codeTextArea); // 使用 RTextScrollPane 以支持行号显示
        add(scrollPane, BorderLayout.CENTER);

        // 添加按钮的事件监听器
        copyPasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCopyPasteDialog();
            }
        });

        fileImportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileImportDialog();
            }
        });
    }

    // 自定义按钮外观
    private void customizeButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);  // 按钮背景为白色
        button.setPreferredSize(new Dimension(120, 40)); // 设置按钮大小
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);  // 按钮文字颜色为黑色
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // 黑色边框
    }

    // 打开“复制传入”对话框
    private void openCopyPasteDialog() {
        RSyntaxTextArea inputArea = new RSyntaxTextArea(20, 50);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA); // 设置语法高亮
        inputArea.setBackground(Color.WHITE);  // 设置对话框中的文本区域背景为白色
        RTextScrollPane scrollPane = new RTextScrollPane(inputArea);

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Please Paste Code Here", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String code = inputArea.getText();
            if (!code.trim().isEmpty()) {
                displayCode(code, true); // 显示代码，允许编辑
            } else {
                JOptionPane.showMessageDialog(this, "Code content cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 打开“文件导入”对话框
    private void openFileImportDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a code file to import");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            Path filePath = fileChooser.getSelectedFile().toPath();
            loadCodeFromFile(filePath);
        }
    }

    // 显示代码内容，并决定是否允许编辑
    private void displayCode(String code, boolean editable) {
        codeTextArea.setText(code);             // 将代码显示在文本区域
        codeTextArea.setCaretPosition(0);       // 将光标置于文本的开头
        codeTextArea.setEditable(editable);     // 根据参数决定是否允许编辑
        codeTextArea.repaint();                 // 重新绘制
    }

    // 方法：读取指定文件的内容并显示在文本区域
    public void loadCodeFromFile(Path filePath) {
        try {
            // 读取文件内容
            String fileContent = Files.readString(filePath);
            // 显示代码内容，并允许编辑
            displayCode(fileContent, true);
        } catch (IOException e) {
            // 处理文件读取异常
            JOptionPane.showMessageDialog(this, "Unable to read file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



