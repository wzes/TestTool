package com.tongji.test.view;

import com.tongji.test.ClassHelper;
import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TotalResult;
import com.tongji.test.util.JavaCompilerUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class TestView extends JFrame {

    private String className;
    private ClassHelper classHelper;
    private int methodIndex = 0;
    private boolean isReadFile = true;
    private boolean isSelected = false;


    public TestView() {
        this.setTitle("Test Tool");
        this.setLayout(null);

        JPanel panel = new JPanel();
        panel.setSize(1000,500);
        panel.setLayout(null);
        this.add(panel);

        JTextField classFileField = new JTextField(20);
        classFileField.setBounds(20,20,205,30);
        classFileField.setText("please select .java file");
        panel.add(classFileField);


        JTextArea dataArea = new JTextArea();
        dataArea.setBorder(BorderFactory.createEtchedBorder());
        dataArea.setBounds(20, 200, 280, 250);
        dataArea.setFont(new Font(null, 0, 15));
        dataArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    if (isSelected) {
                        String text = e.getDocument().getText(0, e.getDocument().getLength());
                        classHelper.setSystemInput(text);
                        isReadFile = false;
                    }
                    isSelected = true;
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        JScrollPane jScrollPane = new JScrollPane(dataArea);
        jScrollPane.setBounds(20, 200, 280, 250);
        panel.add(jScrollPane);

        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(20, 80, 280, 30);
        panel.add(comboBox);
        comboBox.addItemListener(e -> {
            if (e.getStateChange()==ItemEvent.SELECTED) {
                System.out.println(e.getItem());
                methodIndex = getMethodIndex(e.getItem().toString());
            }
        });


        JButton classSelectButton = new JButton("select");
        classSelectButton.setBounds(220, 20, 80, 30);
        classSelectButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showDialog(new JLabel(), "select");
            String filePath = jfc.getSelectedFile().getAbsolutePath();

            if (!filePath.contains(".java")) {
                JOptionPane.showMessageDialog(
                        panel,
                        "File Type Error, Please input .java File",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            classFileField.setText(jfc.getSelectedFile().getName());

            setCsvFilename(filePath);
            System.out.println(filePath);

            // error
            comboBox.setModel(new DefaultComboBoxModel<>(getMethods()));

            comboBox.updateUI();

        });

        panel.add(classSelectButton);

        // data select
        JTextField dataFileField = new JTextField(20);
        dataFileField.setBounds(20,140,205,30);
        dataFileField.setText("please select .csv file");
        panel.add(dataFileField);


        JButton dataSelectButton = new JButton("select");
        dataSelectButton.setBounds(220, 140, 80, 30);

        dataSelectButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showDialog(new JLabel(), "select");
            String filePath = jfc.getSelectedFile().getAbsolutePath();

            if (!filePath.contains(".csv")) {
                JOptionPane.showMessageDialog(
                        panel,
                        "File Type Error, Please input .csv or .txt File",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            isSelected = false;
            isReadFile = true;
            setData(filePath);
            dataFileField.setText(jfc.getSelectedFile().getName());
            String[][] tableText = classHelper.getTableText(methodIndex);

            dataArea.setText(classHelper.getDataText(methodIndex) + "\nData Legal rate: " +
            classHelper.dataEvaluate(methodIndex).getLegalRate() * 100 + " %");
        });
        panel.add(dataSelectButton);

        //
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(null, 0, 18));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        textArea.setBounds(320, 415, 660, 35);

        panel.add(textArea);

        String[] columnNames = { "Input Data", "Actual", "Expected", "Is Pass" };
        final JTable table = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(320, 70, 660, 325);
        panel.add(scrollPane);

        JButton startButton = new JButton("start");
        startButton.setBounds(320, 20, 660, 30);
        startButton.addActionListener(e -> {
            List<ItemResult> itemResults;
            if (isReadFile) {
                itemResults = classHelper.executeByCsv(methodIndex);
            } else {
                itemResults = classHelper.executeByInput(methodIndex);
            }
            StringBuilder sb = new StringBuilder();

            TotalResult evaluate = classHelper.evaluate(itemResults);
            sb.append("Test sample count: ")
                    .append(evaluate.getItemCount())
                    .append(" ")
                    .append("correct: ")
                    .append(evaluate.getCorrectCount())
                    .append(" ")
                    .append("wrong: ")
                    .append(evaluate.getWrongCount())
                    .append(" ")
                    .append("correct rate: ");
            double rate = evaluate.getCorrectRate() * 100;

            if (String.valueOf(rate).length() > 4) {
                sb.append(String.valueOf(rate), 0, 5).append("%");
            } else {
                sb.append(String.valueOf(rate)).append("%");
            }

            textArea.setText(sb.toString());
            textArea.updateUI();


            Object[][] data = new Object[itemResults.size()][4];

            List<Integer> errorsIndex = new ArrayList<>();
            for (int i = 0; i < itemResults.size(); i++) {
                data[i][0] = itemResults.get(i).getInput();
                data[i][1] = itemResults.get(i).getActual();
                data[i][2] = itemResults.get(i).getExpected();
                data[i][3] = itemResults.get(i).isCorrect();
                if (!itemResults.get(i).isCorrect()) {
                    errorsIndex.add(i);
                }
            }

            TableModel tableModel =new TableModel() {
                @Override
                public int getRowCount() {
                    return data.length;
                }

                @Override
                public int getColumnCount() {
                    return 4;
                }

                @Override
                public String getColumnName(int columnIndex) {
                    return columnNames[columnIndex];
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnNames[columnIndex].getClass();
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }

                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    return data[rowIndex][columnIndex];
                }

                @Override
                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

                }

                @Override
                public void addTableModelListener(TableModelListener l) {

                }

                @Override
                public void removeTableModelListener(TableModelListener l) {

                }
            };
            table.setGridColor(Color.BLUE);
            table.setModel(tableModel);
            errorsIndex.forEach((integer -> setOneRowBackgroundColor(table, integer, Color.RED)));
            panel.updateUI();
        });

        panel.add(startButton);


        // show
        this.pack();
        this.setVisible(true);
        this.setBounds(400, 300, 1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 设置表格的某一行的背景色
     * @param table
     */
    public static void setOneRowBackgroundColor(JTable table, int rowIndex,
                                                Color color) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (row == rowIndex) {
                        setBackground(color);
                        setForeground(Color.WHITE);
                    } else {
                        setBackground(Color.lightGray);
                        setForeground(Color.WHITE);
                    }
                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param filePath
     */
    private void setCsvFilename(String filePath) {
        this.className = JavaCompilerUtils.getClassName(filePath);
        String packageName = JavaCompilerUtils.getPackageName(filePath);
        JavaCompilerUtils.CompilerJavaFile(filePath,
                "target/classes/" + packageName);

        classHelper = new ClassHelper(className);
    }

    private void setData(String filePath) {
        classHelper.setCsvFilename(filePath);
    }

    /**
     *
     * @return
     */
    private String[] getMethods() {
        List<String> methods = classHelper.getMethods();
        String[] ms = new String[methods.size()];
        int i = 0;
        while (i < methods.size()) {
            ms[i] = methods.get(i);
            i++;
        }
        return ms;
    }

    private int getMethodIndex(String methodName) {
        for (int index = 0; index < classHelper.getMethods().size(); index++) {
            if (methodName.equals(classHelper.getMethods().get(index))) {
                return index;
            }
        }
        return 0;
    }


    public static void main(String[] args) {
        TestView testView = new TestView();
    }

}
