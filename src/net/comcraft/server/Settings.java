package net.comcraft.server;

public class Settings {

    public String ip = "0.0.0.0";
    public int port = 9999;
    public int worldSize = 16;
    public String worldType = "NORMAL";
    public int flatLevel = 12;
    public boolean generateTrees = false;
    public boolean allowcommands = false;

    public Settings(String ip, int port, int worldSize, String worldType, int flatLevel, boolean generateTrees,
            boolean allowcommands) {
        super();
        this.ip = ip;
        this.port = port;
        this.worldSize = worldSize;
        this.worldType = worldType;
        this.flatLevel = flatLevel;
        this.generateTrees = generateTrees;
        this.allowcommands = allowcommands;
    }

}
