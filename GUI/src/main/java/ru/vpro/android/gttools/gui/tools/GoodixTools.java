package ru.vpro.android.gttools.gui.tools;

import java.util.ArrayList;
import java.util.List;

public class GoodixTools
{
    private SuperSU su;
    private String debugFile = "/proc/gt1x_debug";

    public GoodixTools(SuperSU su)
    {
        this.su = su;
    }

    public ArrayList<String> readConfig()
    {
        su.exec("cat " + debugFile);

        boolean bFound = false;
        ArrayList<String> cfg = new ArrayList<>();

        List<String> cfgLines = su.readLines();
        if (cfgLines == null) return cfg;

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

    public boolean flashConfig(String configText)
    {
        String tmpName = "/sdcard/tp_config.txt";
        if (!ADB.pushContent(tmpName, configText)) return false;

        boolean bOK = execCmd("sendconfig", tmpName);
        su.exec("rm " + tmpName);

        return bOK && su.exitValue() == 0;
    }

    private boolean execCmd(String cmd, String param){
        if (!param.isEmpty()) cmd += " " + param;
        return su.exec("echo " + cmd + "> " + debugFile);
    }
}
