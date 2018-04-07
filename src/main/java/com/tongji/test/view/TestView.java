package com.tongji.test.view;

import com.tongji.test.ClassHelper;
import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TotalResult;
import com.tongji.test.util.FileUtils;
import com.tongji.test.util.JavaCompilerUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class TestView extends JFrame {

    private String className;
    private ClassHelper classHelper;
    private int methodIndex = 0;


    public TestView() {
        this.setTitle("Test Util");
        this.setLayout(null);

        JPanel panel = new JPanel();
        panel.setSize(1000,500);
        panel.setLayout(null);
        this.add(panel);

        JTextField classFileField = new JTextField(20);
        classFileField.setBounds(20,20,205,30);
        classFileField.setText("please select .java file");
        panel.add(classFileField);

        // select class
//        JTextField methodFileField = new JTextField(20);
//        methodFileField.setBounds(20,150,265,30);
//        methodFileField.setText("please select method tested");
//        panel.add(methodFileField);


        JTextArea dataArea = new JTextArea();
        dataArea.setBorder(BorderFactory.createEtchedBorder());
        dataArea.setBounds(20, 200, 280, 250);

        panel.add(dataArea);

        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(20, 80, 280, 30);
        panel.add(comboBox);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED) {
                    System.out.println(e.getItem());
                    methodIndex = getMethodIndex(e.getItem().toString());
                }
            }
        });


        JButton classSelectButton = new JButton("select");
        classSelectButton.setBounds(220, 20, 80, 30);
        classSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "select");
                String filePath = jfc.getSelectedFile().getAbsolutePath();

                if (!filePath.contains(".java")) {
                    System.out.println("file type error");
                    return;
                }
                classFileField.setText(jfc.getSelectedFile().getName());

                setCsvFilename(filePath);
                System.out.println(filePath);

                // error
                comboBox.setModel(new DefaultComboBoxModel<>(getMethods()));

                comboBox.updateUI();

            }
        });

        panel.add(classSelectButton);

        // data select
        JTextField dataFileField = new JTextField(20);
        dataFileField.setBounds(20,140,205,30);
        dataFileField.setText("please select .csv file");
        panel.add(dataFileField);


        JButton dataSelectButton = new JButton("select");
        dataSelectButton.setBounds(220, 140, 80, 30);

        dataSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "select");
                String filePath = jfc.getSelectedFile().getAbsolutePath();

                if (!filePath.contains(".csv")) {
                    System.out.println("file type error");
                    return;
                }
                setData(filePath);
                dataFileField.setText(jfc.getSelectedFile().getName());
                dataArea.setText(classHelper.getDataText(methodIndex));
            }
        });
        panel.add(dataSelectButton);


        JTextArea textArea = new JTextArea();
        textArea.setBorder(BorderFactory.createEtchedBorder());
        textArea.setBounds(320, 50, 660, 400);

        panel.add(textArea);

        JButton startButton = new JButton("start");
        startButton.setBounds(320, 20, 660, 30);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ItemResult> itemResults = classHelper.executeByCsv(methodIndex);
                StringBuilder sb = new StringBuilder();
                sb.append("input\t\t").append("actual\t\t").append("expected\t\t").append("pass").append("\n");
                for (ItemResult itemResult : itemResults) {
                    sb.append(itemResult.getInput())
                            .append("\t\t")
                            .append(itemResult.getActual())
                            .append("\t\t")
                            .append(itemResult.getExpected())
                            .append("\t\t")
                            .append(itemResult.isCorrect())
                            .append("\n");
                }
                TotalResult evaluate = classHelper.evaluate(itemResults);
                sb.append("test sample count: ")
                        .append(evaluate.getItemCount())
                        .append("\t")
                        .append("correct count: ")
                        .append(evaluate.getCorrectCount())
                        .append("\t")
                        .append("wrong count: ")
                        .append(evaluate.getWrongCount())
                        .append("\t")
                        .append("correct rate: ")
                        .append(evaluate.getCorrectRate());

                textArea.setText(sb.toString());
                textArea.updateUI();
            }
        });

        panel.add(startButton);


        // show
        this.pack();
        this.setVisible(true);
        this.setBounds(400, 300, 1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
