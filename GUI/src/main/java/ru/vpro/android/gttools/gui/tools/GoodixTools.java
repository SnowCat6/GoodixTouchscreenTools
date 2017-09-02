package ru.vpro.android.gttools.gui.tools;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.ArrayList;

public class GoodixTools
{
    private SuperSU su;
    private String debugFile = "/proc/gt1x_debug";
    private String endLine = "--END--";

    public GoodixTools(SuperSU su)
    {
        this.su = su;
    }

    public ArrayList<String> readConfig()
    {
        su.exec("cat " + debugFile);
        su.exec("echo " + endLine);

        ArrayList<String> cfgLines = new ArrayList<>();

        while(true) {
            String line = su.readLine();
            if (line == null) break;
            if (line.equals(endLine)) break;
            cfgLines.add(line);
        }

        boolean bFound = false;
        ArrayList<String> cfg = new ArrayList<>();
        for (String line: cfgLines) {
            if (!bFound){
                bFound = line.contains("chip");
                continue;
            }
            if (!line.contains("0x")) continue;
            cfg.add(line);
        }

        return cfg;
    }

    public boolean flashConfig(String configText) {
        return true;
    }
}
