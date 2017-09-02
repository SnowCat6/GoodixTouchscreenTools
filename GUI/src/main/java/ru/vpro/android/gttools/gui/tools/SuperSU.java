package ru.vpro.android.gttools.gui.tools;


import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SuperSU
{
    private ADB adb = new ADB();
    private boolean misAlive = false;

    public boolean open()
    {
        misAlive = false;

        adb.open();
        adb.exec("su");
        adb.exec("id");

        String line = adb.readLine();
        if (line == null) return false;

        misAlive = line.contains("root");
        return isAlive();
    }

    public boolean isAlive()
    {
        return misAlive && adb.isAlive();
    }

    public int exitValue(){
        if (!isAlive()) return -1;
        exec("echo $?");
        return parseInt(readLine());
    }

    public boolean exec(String cmd){
        if (!isAlive()) return false;
        return adb.exec(cmd);
    }

    public boolean available() {
        return adb.available();
    }
    public String readLine(){
        if (!isAlive()) return null;
        return adb.readLine();
    }

    public boolean availableError() {
        return adb.availableError();
    }
    public String readErrorLine(){
        if (!isAlive()) return null;
        return adb.readErrorLine();
    }

    public List<String> readLines() {
        return adb.readLines();
    }
}
