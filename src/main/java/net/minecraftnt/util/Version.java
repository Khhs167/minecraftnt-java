package net.minecraftnt.util;

public class Version {
    private int major;
    private int minor;
    private int build;
    public Version(int major, int minor, int build){
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    public Version(int major, int minor){
        this.major = major;
        this.minor = minor;
        this.build = 0;
    }

    public Version(int major){
        this.major = major;
        this.minor = 0;
        this.build = 0;
    }

    public Version(String string){

        String[] data = string.split("\\.");

        if(data.length > 0)
            this.major = Integer.parseInt(data[0]);

        if(data.length > 1)
            this.minor = Integer.parseInt(data[1]);

        if(data.length > 2)
            this.build = Integer.parseInt(data[2]);

    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getBuild() {
        return build;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }
}
