package ru.vpro.android.gttools.gtreapirtool.tools;

import java.util.ArrayList;
import java.util.List;

public class GoodixTools
{
    private SuperSU su ;
    private String debugFile = "/proc/gt1x_debug";

    public GoodixTools(SuperSU su)
    {
        this.su = su;
    }

    private boolean execCmd(String cmd, String param){
        if (!param.isEmpty()) cmd += " " + param;
        return su.exec("echo " + cmd + "> " + debugFile);
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

    public boolean resetConfig(){
        return execCmd("reset", "");
    }
    public boolean clearConfig(){
        return execCmd("clear_config", "");
    }
    public boolean init(){
        return execCmd("init", "");
    }
    public boolean power(boolean bPowerOn)
    {
        if (bPowerOn) return execCmd("poweron", "");
        return execCmd("poweroff", "");
    }
    public boolean chipVersion(boolean bPowerOn)
    {
        return execCmd("chip", "");
    }
}
