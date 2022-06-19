package sample;

import java.io.File;

public interface Stribog {

    public byte[] getHash(File file, boolean outputMode);

    public byte[] getHash(String file, boolean outputMode);

    public byte[] getHash(byte[] message, boolean outputMode);

}