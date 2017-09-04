package ru.vpro.android.gttools.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import ru.vpro.android.gttools.gui.tools.GoodixTools;
import ru.vpro.android.gttools.gui.tools.SuperSU;

class MainWindow extends JPanel
{
    private SuperSU su = new SuperSU();
    private GoodixTools gt = new GoodixTools(su);

    private String configText = "";
    private String defaultConfigName = "gtx_config.txt";

    private JPanel resultPanel = new JPanel();
    private JTextArea resultTextArea = new JTextArea();
    private JButton btnSaveCfg = new JButton("Save config ...");

    MainWindow()
    {
        resultTextArea.setEditable(false);
        resultTextArea.setCursor(null);
        resultTextArea.setOpaque(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);

        JButton btnReadCfg = new JButton("Read config");
        btnReadCfg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                actionReadConfig();
            }
        });

        btnSaveCfg.setEnabled(false);
        btnSaveCfg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                actionSaveConfig();
            }
        });

        JButton btnFlashCfg = new JButton("Flash config ...");
        btnFlashCfg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                actionFlashConfig();
            }
        });

        setLayout(new BorderLayout());
        Box verticalBox = Box.createVerticalBox();

        JPanel horizontalPanel = new JPanel();

        horizontalPanel.add(btnReadCfg);
        horizontalPanel.add(btnSaveCfg);
        horizontalPanel.add(btnFlashCfg);

        verticalBox.add(horizontalPanel);

        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(resultTextArea);

        verticalBox.add(resultPanel);

        add(verticalBox);
    }

    private void actionFlashConfig()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultConfigName));

        if (fileChooser.showOpenDialog(getRootPane()) != JFileChooser.APPROVE_OPTION) return;

        //  Read file
        configText = "";
        btnSaveCfg.setEnabled(false);
        setResultValue("Read config values for flash", "");

        File file = fileChooser.getSelectedFile();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder cfg = new StringBuilder();
            for(String line: lines){
                cfg.append(line).append("\n");
            }
            configText = cfg.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doFlashConfig();
    }
    private void doFlashConfig()
    {
        new Thread(new Runnable() {
            public void run()
            {
                if (!checkSU()) return;

                if (!gt.flashConfig(configText)) {
                    setResultValue("Flash config values FILED", configText);
                    return;
                }
                setResultValue("Flash config values OK", configText);
            }
        }).start();
    }

    private void actionSaveConfig()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultConfigName));

        if (fileChooser.showSaveDialog(getRootPane()) != JFileChooser.APPROVE_OPTION) return;

        // save to file
        File file = fileChooser.getSelectedFile();
        try {
            Files.write(file.toPath(), configText.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actionReadConfig()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                if (!checkSU()) return;

                resultTextArea.setText("Reading config...");

                ArrayList<String> cfg = gt.readConfig();
                StringBuilder text = new StringBuilder();
                for(String line: cfg){
                    text.append(line).append("\n");
                }

                configText = text.toString();
                btnSaveCfg.setEnabled(!configText.isEmpty());
                setResultValue("Chip config values", configText);
            }
        }).start();
    }

    private boolean checkSU()
    {
        if (su.isAlive()) return true;
        if (su.open()) return true;
        setResultValue("Error open SU", "No ADB or su exists!");
        return false;
    }

    private void setResultValue(String title, String value){
        resultPanel.setBorder(new TitledBorder(title));
        resultTextArea.setText(value);
    }
}
