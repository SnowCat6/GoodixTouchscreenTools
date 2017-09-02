package ru.vpro.android.gttools.gui.tools;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ADB
{
    private Process adb = null;
    private OutputStream writeStream = null;
    private InputStream readStream = null;
    private InputStream readErrorStream = null;

    public boolean open()
    {
        try {
            close();
            adb = Runtime.getRuntime().exec("adb.exe shell");

            writeStream = adb.getOutputStream();
            readStream = adb.getInputStream();
            readErrorStream = adb.getErrorStream();

        } catch (IOException e) {
            e.printStackTrace();
            adb = null;
            return false;
        }
        return true;
    }
    public void close(){
        if (isAlive()) {
            adb.destroy();
        }
        adb = null;
    }
    public boolean isAlive(){
        if (adb == null) return false;
        return adb.isAlive();
    }
    public boolean exec(String cmd)
    {
        if (!isAlive()) return false;
        try {
            cmd += "\n";
            writeStream.write(cmd.getBytes());
            writeStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean available(){
        return available(readStream);
    }
    public String readLine(){
        return readLine(readStream);
    }

    public boolean availableError(){
        return available(readErrorStream);
    }
    public String readErrorLine(){
        return readLine(readErrorStream);
    }

    private boolean available(InputStream stream)
    {
        try {
            return stream.available() > 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private String readLine(InputStream stream)
    {
        if (!isAlive()) return null;

        StringBuilder ret = new StringBuilder();
        while(true)
        {
            try {
                int nChar = stream.read();
                if (nChar == 10 || nChar == 13){
                    if (ret.length() > 0) break;
                    continue;
                }
                if (nChar == -1) return null;

                ret.append((char)nChar);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return ret.toString();
    };
}
