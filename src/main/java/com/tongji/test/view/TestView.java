package com.tongji.test.view;

import com.tongji.test.ClassHelper;
import com.tongji.test.util.JavaCompilerUtils;

import javax.swing.*;
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


    public TestView() {
        this.setTitle("Test Util");
        this.setLayout(null);

        JTextField classFileField = new JTextField(20);
        classFileField.setBounds(20,20,165,30);
        classFileField.setToolTipText("please select .java file");
        this.add(classFileField);

        // select class

        JButton classSelectButton = new JButton("选择");
        classSelectButton.setBounds(200, 20, 80, 30);
        classSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                String filePath = jfc.getSelectedFile().getAbsolutePath();

                if (!filePath.contains(".java")) {
                    System.out.println("file type error");
                    return;
                }
                classFileField.setText(jfc.getSelectedFile().getName());

                setCsvFilename(filePath);
                System.out.println(filePath);

                // error
                JComboBox comboBox = new JComboBox(getMethods());
                getRootPane().add(comboBox);

                comboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange()==ItemEvent.SELECTED) {
                            System.out.println(e.getItem());
                        }
                    }
                });

                setSize(800, 600);
            }
        });

        this.add(classSelectButton);

        // data select
        JTextField dataFileField = new JTextField(20);
        dataFileField.setBounds(20,80,165,30);
        dataFileField.setToolTipText("please select .csv file");
        this.add(dataFileField);

        JButton dataSelectButton = new JButton("选择");
        dataSelectButton.setBounds(200, 80, 80, 30);

        dataSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                String filePath = jfc.getSelectedFile().getAbsolutePath();

                if (!filePath.contains(".csv")) {
                    System.out.println("file type error");
                    return;
                }

                dataFileField.setText(jfc.getSelectedFile().getName());

                setData(filePath);

                setSize(800, 600);
            }
        });
        this.add(dataSelectButton);

        JList<String> result = new JList<>();




        // show
        this.pack();
        this.setVisible(true);
        this.setBounds(200, 200, 800, 500);
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


    public static void main(String[] args) {
        TestView testView = new TestView();
    }
}
