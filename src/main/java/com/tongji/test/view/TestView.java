package com.tongji.test.view;

import com.tongji.test.ClassHelper;
import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TotalResult;
import com.tongji.test.util.JavaCompilerUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class TestView extends JFrame {

    private String className;
    private ClassHelper classHelper;
    private int methodIndex = 0;

    private JPanel panel;
    private JTextField classFileField;
    private JTable dataTable;
    private JTextArea dataArea;
    private JComboBox comboBox;
    private JButton classSelectButton;
    private JTextField dataFileField;
    private JButton dataSelectButton;
    private JButton startButton;
    private JTextArea textArea;
    private JTable showTable;
    private JButton addDataButton;
    private String[] columnNames = { "Input Data", "Actual", "Expected", "Is Pass" };
    private String[][] tableText;
    private TableModel dataTableModel;
    private PieChart pieChart;
    public TestView() {
        this.setTitle("Test Tool");
        this.setLayout(null);

        panel = new JPanel();
        panel.setSize(1200,700);
        panel.setLayout(null);
        this.add(panel);


        // left
        classFileField = new JTextField(20);
        classFileField.setBounds(20,20,305,30);
        classFileField.setText("please select .java file");
        panel.add(classFileField);

        dataTable = new JTable();
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        dataScrollPane.setBounds(20, 200, 380, 350);
        panel.add(dataScrollPane);

        addDataButton = new JButton("Add row");
        addDataButton.setBounds(20, 570, 380, 30);
        addDataButton.addActionListener(e -> {
            // add row

            String[][] tmp = new String[tableText.length + 1][tableText[0].length];
            System.arraycopy(tableText, 0, tmp, 0, tableText.length);
            tmp[tableText.length] = new String[tmp[0].length];
            tableText = tmp;
            updateData();
        });
        panel.add(addDataButton);

        dataArea = new JTextArea();
        dataArea.setBorder(BorderFactory.createEtchedBorder());
        dataArea.setBounds(20, 620, 380, 35);
        dataArea.setFont(new Font(null, 0, 15));
        panel.add(dataArea);

        comboBox = new JComboBox();
        comboBox.setBounds(20, 80, 380, 30);
        panel.add(comboBox);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                methodIndex = getMethodIndex(e.getItem().toString());
            }
        });

        classSelectButton = new JButton("select");
        classSelectButton.setBounds(320, 20, 80, 30);
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
            // error
            comboBox.setModel(new DefaultComboBoxModel<>(getMethods()));
            comboBox.updateUI();

        });

        panel.add(classSelectButton);

        // data select
        dataFileField = new JTextField(20);
        dataFileField.setBounds(20,140,305,30);
        dataFileField.setText("please select .csv file");
        panel.add(dataFileField);

        dataSelectButton = new JButton("select");
        dataSelectButton.setBounds(320, 140, 80, 30);

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
            setData(filePath);
            dataFileField.setText(jfc.getSelectedFile().getName());
            tableText = classHelper.getTableText(methodIndex);
            updateData();
        });
        panel.add(dataSelectButton);

        //
        textArea = new JTextArea();
        textArea.setFont(new Font(null, 0, 18));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        textArea.setBounds(420, 620, 760, 35);

        panel.add(textArea);

        pieChart = new PieChart(null);
        pieChart.frame1.setBounds(415, 500, 765, 105);
        panel.add(pieChart.getChartPanel());

        String[] columnNames = { "Input Data", "Actual", "Expected", "Is Pass" };
        showTable = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(showTable);
        scrollPane.setBounds(420, 70, 760, 430);
        panel.add(scrollPane);

        startButton = new JButton("start");
        startButton.setBounds(420, 20, 760, 30);
        startButton.addActionListener(e -> {
            updateError();
            if (!checkNull(tableText) && !checkError(tableText)) {
                updateShow();
            } else {
                JOptionPane.showMessageDialog(
                        panel,
                        "Data cant not be null",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(startButton);


        // show
        this.pack();
        this.setVisible(true);
        this.setBounds(400, 300, 1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private static String[][] getTableText(String[][] data) {
        String[][] tmp = new String[data.length][];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = new String[data[0].length - 1];
            for (int j = 0; j < data[0].length - 1; j++) {
                tmp[i][j] = data[i][j];
            }
        }
        return tmp;
    }


    /**
     * 设置表格的某一行的背景色
     * @param table
     */
    private static void setOneRowBackgroundColor(JTable table, int rowIndex,
                                                 Color color) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (row == rowIndex) {
                        setBackground(color);
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                    return super.getTableCellRendererComponent(table, value,
                            isSelected, false, row, column);
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
                "target/classes/");

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
        new TestView();
    }


    private void updateData() {
        classHelper.setTableText(getTableText(tableText));
        String[] columnNames = new String[tableText[0].length];
        for (int i = 0; i < tableText[0].length - 1; i++) {
            columnNames[i] = "M" + (i + 1);
        }
        columnNames[tableText[0].length - 1] = "isLegal";
        dataTableModel = new DefaultTableModel() {

            @Override
            public void fireTableDataChanged() {
                super.fireTableDataChanged();
            }

            @Override
            public int getRowCount() {
                return tableText.length;
            }

            @Override
            public int getColumnCount() {
                return tableText[0].length;
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
                return true;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return tableText[rowIndex][columnIndex];
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                tableText[rowIndex][columnIndex] = aValue.toString();
                classHelper.setTableText(tableText);
                classHelper.updateTable();
                tableText = classHelper.getTableText();
                fireTableDataChanged();
                updateError();
            }
        };
        dataTable.setModel(dataTableModel);
        dataTableModel.addTableModelListener(e1 -> {
            if (e1.getType() == TableModelEvent.UPDATE) {
                classHelper.setTableText(getTableText(tableText));
                if (!checkNull(tableText) && !checkError(tableText)) {
                    updateShow();
                }
            }
        });
        updateError();
        panel.updateUI();
    }

    private void updateError() {
        List<Integer> errorsIndex = new ArrayList<>();
        for (int i = 0; i < tableText.length; i++) {
            if (tableText[i][tableText[0].length - 1] != null &&
                    tableText[i][tableText[0].length - 1].contains("false")) {
                errorsIndex.add(i);
            }
        }
        errorsIndex.forEach((integer -> setOneRowBackgroundColor(dataTable, integer, Color.RED)));
        dataArea.setText("Data Legal rate: " + (tableText.length - errorsIndex.size()) * 100 / tableText.length + " %");
    }


    private void updateShow() {
        List<ItemResult> itemResults;
        itemResults = classHelper.executeByTableText(methodIndex);
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
        HashMap<String, Integer> dataset = new HashMap<>();
        dataset.put("Correct", evaluate.getCorrectCount());
        dataset.put("Fail", evaluate.getWrongCount());

        // update
        pieChart.setDataset(dataset);
        pieChart.getChartPanel().updateUI();

        panel.add(pieChart.getChartPanel());
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
                return data[0].length;
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
        showTable.setGridColor(Color.BLUE);
        showTable.setModel(tableModel);
        if (errorsIndex.size() == 0) {
            for (int i = 0; i < tableText.length; i++) {
                setOneRowBackgroundColor(dataTable, i, Color.white);
            }
        } else {
            errorsIndex.forEach((integer -> setOneRowBackgroundColor(dataTable, integer, Color.RED)));
        }
        errorsIndex.forEach((integer -> setOneRowBackgroundColor(showTable, integer, Color.RED)));
        panel.updateUI();

        if (evaluate.getWrongCount() != 0) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Exist error samples",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean checkNull(String[][] tableText) {
        boolean update = false;
        for (int i = 0; i < tableText.length; i++) {
            for (int j = 0; j < tableText[0].length - 1; j++) {
                if (tableText[i][j] == null) {
                    update = true;
                }
            }
        }
        return update;
    }

    private boolean checkError(String[][] tableText) {
        boolean error = false;
        for (int i = 0; i < tableText.length; i++) {
            if (tableText[i][tableText[i].length - 1].contains("false")) {
                error = true;
            }
        }
        return error;
    }

    class PieChart {
        ChartPanel frame1;
        DefaultPieDataset dataset;
        JFreeChart chart;
        PiePlot pieplot;
        PieChart(HashMap<String, Integer> data) {
            dataset = new DefaultPieDataset();
            if (data != null) {
                data.forEach((key, value) -> dataset.setValue(key, value));
            }
            chart = ChartFactory.createPieChart3D("测试结果",
                    dataset,true,false,false);
            pieplot = (PiePlot) chart.getPlot();
            DecimalFormat df = new DecimalFormat("0.00%");
            NumberFormat nf = NumberFormat.getNumberInstance();
            StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);
            pieplot.setLabelGenerator(sp1);

            pieplot.setNoDataMessage("无数据显示");
            pieplot.setCircular(false);
            pieplot.setLabelGap(0.02D);

            pieplot.setIgnoreNullValues(true);
            pieplot.setIgnoreZeroValues(true);
            frame1 = new ChartPanel(chart,true);
            chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));
            PiePlot piePlot= (PiePlot) chart.getPlot();
            piePlot.setLabelFont(new Font("宋体",Font.BOLD,10));
            chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,10));
        }

        ChartPanel getChartPanel(){
            return frame1;
        }

        void setDataset(HashMap<String, Integer> data) {
            this.dataset = new DefaultPieDataset();
            data.forEach((key, value) -> dataset.setValue(key, value));
            chart = ChartFactory.createPieChart3D("测试结果",
                    dataset,true,false,false);
            pieplot = (PiePlot) chart.getPlot();
            DecimalFormat df = new DecimalFormat("0.00%");
            NumberFormat nf = NumberFormat.getNumberInstance();
            StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);
            pieplot.setLabelGenerator(sp1);

            pieplot.setNoDataMessage("无数据显示");
            pieplot.setCircular(false);
            pieplot.setLabelGap(0.02D);

            pieplot.setIgnoreNullValues(true);
            pieplot.setIgnoreZeroValues(true);
            frame1.setChart(chart);
            chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));
            PiePlot piePlot= (PiePlot) chart.getPlot();
            piePlot.setLabelFont(new Font("宋体",Font.BOLD,10));
            chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,10));
        }
    }

}
