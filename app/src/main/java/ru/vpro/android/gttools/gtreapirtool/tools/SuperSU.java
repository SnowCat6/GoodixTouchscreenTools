package ru.vpro.android.gttools.gtreapirtool.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SuperSU
{
    private Process suProcess = null;
    private OutputStream writeStream = null;
    private InputStream readStream = null;
    private InputStream readErrorStream = null;

    private boolean misAlive = false;
    final static String endLineMarker = "---READ_LINES---";

    public boolean open()
    {
        misAlive = false;

        try {
            close();
            suProcess = Runtime.getRuntime().exec("su");

            writeStream = suProcess.getOutputStream();
            readStream = suProcess.getInputStream();
            readErrorStream = suProcess.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
            suProcess = null;
            return false;
        }

        exec("id");

        String line = readLine();
        if (line == null) return false;

        misAlive = line.contains("root");
        return isAlive();
    }

    public void close(){
        if (isAlive()) {
            suProcess.destroy();
        }
        suProcess = null;
    }
    public boolean isAlive()
    {
        return misAlive;
    }

    public int exitValue(){
        if (!isAlive()) return -1;
        exec("echo $?");
        return parseInt(readLine());
    }

    public boolean exec(String cmd)
    {
        if (writeStream == null) return false;

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
        if (stream == null) return null;

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
}
