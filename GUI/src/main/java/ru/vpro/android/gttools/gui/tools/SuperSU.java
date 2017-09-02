package ru.vpro.android.gttools.gui.tools;


public class SuperSU
{
    private ADB adb = new ADB();
    private boolean isAlive = false;

    public boolean open()
    {
        isAlive = false;

        adb.open();
        adb.exec("su");
        adb.exec("id");

        String line = adb.readLine();
        if (line == null) return false;

        isAlive = line.contains("root");
        return isAlive();
    }

    public boolean isAlive(){
        return isAlive && adb.isAlive();
    }

    public boolean exec(String cmd){
        if (!isAlive) return false;
        return adb.exec(cmd);
    }

    public boolean available() {
        return adb.available();
    }
    public String readLine(){
        if (!isAlive) return null;
        return adb.readLine();
    }

    public boolean availableError() {
        return adb.availableError();
    }
    public String readErrorLine(){
        if (!isAlive) return null;
        return adb.readErrorLine();
    }
}
