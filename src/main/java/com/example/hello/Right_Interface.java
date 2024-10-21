package com.example.hello;

import VersionStorage.FileChangeType;
import com.intellij.ui.components.JBScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Right_Interface extends JPanel
{

    private JPanel changesPanel;            // Changes视图的面板
    private JPanel historyPanel;            // History视图的面板
    private CardLayout cardLayout;          // 用于切换视图的CardLayout
    private Highlighter.HighlightPainter addPainter;    // 高亮新增行的Painter（绿色）
    private Highlighter.HighlightPainter deletePainter; // 高亮删除行的Painter（红色）
    private Highlighter.HighlightPainter changePainter; // 高亮修改行的Painter（蓝色）
    // 美化文件名标签的样式


    //动态改变的东西*-------

    //changes内容的动态显示
    private RSyntaxTextArea Changes_File_One_Text;
    private RSyntaxTextArea Changes_File_Two_Text;
    private JLabel Changes_File_One_Label;
    private JLabel Changes_File_Two_Label;

    //History内容动态显示
    private RSyntaxTextArea History_File_One_Text;
    private RSyntaxTextArea History_File_Two_Text;
    private JLabel History_File_One_Label;
    private JLabel History_File_Two_Label;

    //History下的文件列表
    private  DefaultListModel<String> History_List_Model;
    private  JList<String> History_List;

    //History下change的选择文件名
    public String Picked_History_Name;

    //回调接口
    private Button_Listener listener;

    private void styleFileLabel(JLabel label)
    {
        label.setOpaque(true);
        label.setBackground(new Color(173, 216, 230)); // 浅蓝色背景
        label.setForeground(Color.BLACK); // 字体颜色
        label.setFont(new Font("Arial", Font.BOLD, 16)); // 字体样式
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 添加一些内边距
    }
    // 构造函数
    public Right_Interface(Button_Listener listener)
    {
        this.listener=listener;
        cardLayout = new CardLayout();
        setLayout(cardLayout); // 设置布局为CardLayout

        // 初始化高亮Painter
        addPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(144, 238, 144)); // 绿色
        deletePainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 182, 193)); // 红色
        changePainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(173, 216, 230)); // 蓝色

        // 创建Changes视图的面板
        changesPanel = new JPanel(new BorderLayout());
        initChangesView(); // 初始化Changes视图
        add(changesPanel, "Changes");

        // 创建History视图的面板
        historyPanel = new JPanel(new BorderLayout());
        initHistoryView(); // 初始化History视图
        add(historyPanel, "History");

        // 默认显示Changes面板
        cardLayout.show(this, "Changes");
    }

    // 初始化Changes视图，显示代码对比，不显示文件列表
    public void initChangesView()
    {
        // 创建对比区域，包含两个RSyntaxTextArea
        JPanel comparisonPanel = new JPanel(new GridLayout(1, 2));

        // 第一个文件的面板，包含文件名和文本区域
        JPanel file1Panel = new JPanel(new BorderLayout());
        Changes_File_One_Label = new JLabel("Please choose a file To Comparison", SwingConstants.CENTER); // 文件名居中显示
        styleFileLabel(Changes_File_One_Label); // 美化文件名标签
        Changes_File_One_Text = new RSyntaxTextArea(20, 60);
        Changes_File_One_Text.setEditable(false);
        Changes_File_One_Text.setFont(new Font("Monospaced", Font.PLAIN, 12));
        Changes_File_One_Text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        Changes_File_One_Text.setCodeFoldingEnabled(true);
        RTextScrollPane file1ScrollPane = new RTextScrollPane(Changes_File_One_Text);
        file1Panel.add(Changes_File_One_Label, BorderLayout.NORTH); // 添加文件名标签
        file1Panel.add(file1ScrollPane, BorderLayout.CENTER); // 添加文本区域
        comparisonPanel.add(file1Panel); // 添加第一个文件的面板

        // 第二个文件的面板，包含文件名和文本区域
        JPanel file2Panel = new JPanel(new BorderLayout());
        Changes_File_Two_Label = new JLabel("Please choose a file To Comparison", SwingConstants.CENTER); // 文件名居中显示
        styleFileLabel(Changes_File_Two_Label); // 美化文件名标签
        Changes_File_Two_Text = new RSyntaxTextArea(20, 60);
        Changes_File_Two_Text.setEditable(false);
        Changes_File_Two_Text.setFont(new Font("Monospaced", Font.PLAIN, 12));
        Changes_File_Two_Text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        Changes_File_Two_Text.setCodeFoldingEnabled(true);
        RTextScrollPane file2ScrollPane = new RTextScrollPane(Changes_File_Two_Text);
        file2Panel.add(Changes_File_Two_Label, BorderLayout.NORTH); // 添加文件名标签
        file2Panel.add(file2ScrollPane, BorderLayout.CENTER); // 添加文本区域
        comparisonPanel.add(file2Panel); // 添加第二个文件的面板
        // 添加对比面板到Changes面板
        changesPanel.add(comparisonPanel, BorderLayout.CENTER);
        // 默认对比test1和test2
    }


    // 初始化History视图，显示文件列表和选中的文件内容对比
    private void initHistoryView() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); // 设置分割条初始位置
        Picked_History_Name=new String();

        // 左边的文件列表
        History_List_Model= new DefaultListModel<>();
        History_List = new JList<>();
        History_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        History_List.setPreferredSize(new Dimension(200, 0));

              //选中文件列表触发时间
        History_List.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFile = History_List.getSelectedValue();
                if (selectedFile != null)
                {
                    System.out.println("选中该版本下的："+selectedFile+"----文件");
                    listener.History_Change_Selected(Picked_History_Name,selectedFile);
                }
            }
        });
        History_List.setModel(History_List_Model);

        History_List.setBackground(new Color(255, 255, 255));
        Font List_Font = new Font("Times New Roman", Font.PLAIN, 15);
        History_List.setFont(List_Font);
        History_List.setSelectionForeground(Color.GRAY);
        JBScrollPane fileScrollPane = new JBScrollPane(History_List);
        splitPane.setLeftComponent(fileScrollPane);

        // 右边的对比区域，包含两个垂直布局的面板
        JPanel comparisonPanel = new JPanel(new GridLayout(1, 2));

        // 第一个文件的面板，包含文件名和文本区域
        JPanel file1Panel = new JPanel(new BorderLayout());
        History_File_One_Label = new JLabel("test1.java", SwingConstants.CENTER); // 文件名居中显示
        styleFileLabel(History_File_One_Label); // 美化文件名标签
        History_File_One_Text = new RSyntaxTextArea(20, 60);
        History_File_One_Text.setEditable(false);
        History_File_One_Text.setFont(new Font("Monospaced", Font.PLAIN, 12));
        History_File_One_Text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        History_File_One_Text.setCodeFoldingEnabled(true);
        RTextScrollPane file1ScrollPane = new RTextScrollPane(History_File_One_Text);
        file1Panel.add(History_File_One_Label, BorderLayout.NORTH); // 添加文件名标签
        file1Panel.add(file1ScrollPane, BorderLayout.CENTER); // 添加文本区域
        comparisonPanel.add(file1Panel); // 添加第一个文件的面板

        // 第二个文件的面板，包含文件名和文本区域
        JPanel file2Panel = new JPanel(new BorderLayout());
        History_File_Two_Label = new JLabel("test2.java", SwingConstants.CENTER); // 文件名居中显示
        styleFileLabel(History_File_Two_Label); // 美化文件名标签
        History_File_Two_Text = new RSyntaxTextArea(20, 60);
        History_File_Two_Text.setEditable(false);
        History_File_Two_Text.setFont(new Font("Monospaced", Font.PLAIN, 12));
        History_File_Two_Text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        History_File_Two_Text.setCodeFoldingEnabled(true);
        RTextScrollPane file2ScrollPane = new RTextScrollPane(History_File_Two_Text);
        file2Panel.add(History_File_Two_Label, BorderLayout.NORTH); // 添加文件名标签
        file2Panel.add(file2ScrollPane, BorderLayout.CENTER); // 添加文本区域
        comparisonPanel.add(file2Panel); // 添加第二个文件的面板

        splitPane.setRightComponent(comparisonPanel);

        historyPanel.add(splitPane, BorderLayout.CENTER);
    }



    // 显示所选文件的代码对比---******
                //先传对应的标签进来
    public void Changes_Show_FileComparison(String File_Name,String File_Path,String Store_Name, String Store_Path)
    {
        Changes_File_One_Label.setText(File_Name);
        Changes_File_Two_Label.setText(Store_Name);
        Path filePath1 = Path.of(File_Path); // 替换为实际路径
        Path filePath2 = Path.of(Store_Path); // 替换为实际路径
        // 调用文件对比函数
        SwingUtilities.invokeLater(() -> {
            compareFiles(filePath1, filePath2,Changes_File_One_Text,Changes_File_Two_Text);
            Changes_File_One_Text.revalidate();
            Changes_File_One_Text.repaint();
            Changes_File_Two_Text.revalidate();
            Changes_File_Two_Text.repaint();
        });
        // showChangesView();
    }

    public void History_Show_FileComparison(String File_Name,String File_Path,String Store_Name, String Store_Path)
    {
        History_File_One_Label.setText(File_Name);
        History_File_Two_Label.setText(Store_Name);
        Path filePath1 = Path.of(File_Path); // 替换为实际路径
        Path filePath2 = Path.of(Store_Path); // 替换为实际路径
        // 调用文件对比函数
        SwingUtilities.invokeLater(() -> {
            compareFiles(filePath1, filePath2,History_File_One_Text,History_File_Two_Text);
            History_File_One_Text.revalidate();
            History_File_One_Text.repaint();
            History_File_Two_Text.revalidate();
            History_File_Two_Text.repaint();
        });
        // showChangesView();
    }








    // 文件对比显示函数 --------


    public void compareFiles(Path filePath1, Path filePath2,RSyntaxTextArea File_One_Text,RSyntaxTextArea File_Two_Text) {
        try {
            // 使用FileDiff进行文件对比
            FileDiff fileDiff = new FileDiff(filePath1.toString(), filePath2.toString());

            if (!fileDiff.hasDifferences()) {
                JOptionPane.showMessageDialog(this, "No differences found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<Map<String, Object>> differences = fileDiff.getDifferences();

            // 读取文件内容
            List<String> file1Content = FileReader.ReadFile(filePath1.toString());
            List<String> file2Content = FileReader.ReadFile(filePath2.toString());


            // 显示文件内容
            File_One_Text.setText(String.join("\n", file1Content));
            File_Two_Text.setText(String.join("\n", file2Content));

            // 清除之前的高亮
            File_One_Text.getHighlighter().removeAllHighlights();
            File_Two_Text.getHighlighter().removeAllHighlights();

            // 遍历差异并高亮显示
            for (Map<String, Object> diff : differences) {
                String type = (String) diff.get("type");

                if ("add".equals(type)) {
                    List<Integer> addLineNumbers = (List<Integer>) diff.get("line_numbers");
                    for (int lineNumber : addLineNumbers) {
                        highlightLine(File_Two_Text, lineNumber, addPainter); // 高亮新增行（绿色）
                    }
                } else if ("delete".equals(type)) {
                    List<Integer> delLineNumbers = (List<Integer>) diff.get("line_numbers");
                    for (int lineNumber : delLineNumbers) {
                        highlightLine(File_One_Text, lineNumber, deletePainter); // 高亮删除行（红色）
                    }
                } else if ("change".equals(type)) {
                    List<Integer> origLineNumbers = (List<Integer>) diff.get("original_line_numbers");
                    List<Integer> revLineNumbers = (List<Integer>) diff.get("revised_line_numbers");
                    for (int lineNumber : origLineNumbers) {
                        highlightLine(File_One_Text, lineNumber, changePainter); // 高亮左侧修改行（蓝色）
                    }
                    for (int lineNumber : revLineNumbers) {
                        highlightLine(File_Two_Text, lineNumber, changePainter); // 高亮右侧修改行（蓝色）
                    }
                }
            }



        } catch (IOException e) {
            // 如果发生异常，显示错误信息
            JOptionPane.showMessageDialog(this, "Error comparing files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 高亮指定行
    private void highlightLine(RSyntaxTextArea textArea, int lineNumber, Highlighter.HighlightPainter painter) {
        try {
            int startOffset = textArea.getLineStartOffset(lineNumber);
            int endOffset = textArea.getLineEndOffset(lineNumber);
            textArea.getHighlighter().addHighlight(startOffset, endOffset, painter);
        } catch (BadLocationException e) {
            System.err.println("Error highlighting line " + lineNumber + ": " + e.getMessage());
        }
    }

    // 切换到Changes视图
    public void showChangesView() {
        cardLayout.show(this, "Changes");
    }

    // 切换到History视图
    public void showHistoryView() {
        cardLayout.show(this, "History");
    }

    //清空页面
    public void Clean_View()
    {
        Changes_File_One_Label.setText("Please choose a File!");
        Changes_File_Two_Label.setText("Please choose a File!");

        History_File_One_Label.setText("Please choose a File!");
        History_File_Two_Label.setText("Please choose a File!");

        Changes_File_One_Text.setText("");
        Changes_File_Two_Text.setText("");

        History_File_One_Text.setText("");
        History_File_Two_Text.setText("");

        History_List_Model.clear();
    }

    //更新列表函数
    public void Update_History_List(Map<String, FileChangeType> changes_init)
    {
        History_List_Model.clear();
        for (Map.Entry<String, FileChangeType> entry : changes_init.entrySet()) {
            String fileName = entry.getKey();
            History_List_Model.addElement(fileName);
        }
        History_List.setCellRenderer(new DefaultListCellRenderer() {
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