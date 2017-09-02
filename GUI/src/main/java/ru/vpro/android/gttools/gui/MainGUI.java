package ru.vpro.android.gttools.gui;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainGUI extends JFrame
{
    public static void main(String args [])
    {
        new MainGUI();
    }
    private MainGUI(){
        setTitle( "Goodix touchscreen config tools" );
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new MainWindow());

        setSize (400,400) ;
        setVisible(true);
    }
}
