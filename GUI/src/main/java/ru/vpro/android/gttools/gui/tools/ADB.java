package ru.vpro.android.gttools.gui.tools;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ADB
{
    private Process adbProcess = null;
    private OutputStream writeStream = null;
    private InputStream readStream = null;
    private InputStream readErrorStream = null;

    final static String endLineMarker = "---READ_LINES---";

    public boolean open()
    {
        return open("shell");
    }
    public boolean open(String cmd)
    {
        try {
            close();
            adbProcess = Runtime.getRuntime().exec("adb.exe " + cmd);

            writeStream = adbProcess.getOutputStream();
            readStream = adbProcess.getInputStream();
            readErrorStream = adbProcess.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
            adbProcess = null;
            return false;
        }
        return true;
    }
    public void close(){
        if (isAlive()) {
            adbProcess.destroy();
        }
        adbProcess = null;
    }
    public boolean isAlive(){
        if (adbProcess == null) return false;
        return adbProcess.isAlive();
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
    public List<String> readLines()
    {
        if (!exec("echo " + endLineMarker)) return null;

        List<String> result = new ArrayList<String>();
        while(isAlive()){
            String line = readLine();
            if (line == null) return null;
            if (line.equals(endLineMarker)) break;
            result.add(line);
        }
        return result;
    }

    public static boolean pushFile(String remoteFileName, String localFileName)
    {
        ADB adb = new ADB();
        boolean bOK = false;
        if (adb.open("push " + localFileName + " " + remoteFileName))
        {
            try {
                adb.adbProcess.waitFor();
                bOK = adb.adbProcess.exitValue() == 0;
                String err = adb.readLine();
                System.out.print(err);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        adb.close();
        return bOK;
    }
    public static boolean pushContent(String remoteFileName, String content)
    {
        try {
            File tmpFile = File.createTempFile("adb_temp_push", null);
            Files.write(tmpFile.toPath(), content.getBytes());

            boolean bOK = pushFile(remoteFileName, tmpFile.getAbsolutePath());
            tmpFile.delete();

            return bOK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
