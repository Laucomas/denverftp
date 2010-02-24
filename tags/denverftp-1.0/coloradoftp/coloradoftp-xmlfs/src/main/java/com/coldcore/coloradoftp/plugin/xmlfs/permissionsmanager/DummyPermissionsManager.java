package com.coldcore.coloradoftp.plugin.xmlfs.permissionsmanager;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;

/**
 * This permissions manager allows everything, a real dummy.
 * Exists for testing purposes or when no permissions checks required.
 */
public class DummyPermissionsManager implements PermissionsManager {

    public void setFileAdapter(FileAdapter fileAdapter) {}


    public boolean canAccessDirectory(String dirname, UserHome home) {
        return true;
    }


    public boolean canAccessFile(String filename, UserHome home) {
        return true;
    }


    public boolean canListDirectory(String dirname, UserHome home) {
        return true;
    }


    public boolean canListFile(String filename, UserHome home) {
        return true;
    }


    public boolean canDeleteDirectory(String dirname, UserHome home) {
        return true;
    }


    public boolean canDeleteFile(String filename, UserHome home) {
        return true;
    }


    public boolean canRenameDirectory(String dirname, UserHome home) {
        return true;
    }


    public boolean canRenameFile(String filename, UserHome home) {
        return true;
    }


    public boolean canCreateDirectory(String dirname, UserHome home) {
        return true;
    }


    public boolean canCreateFile(String filename, UserHome home) {
        return true;
    }


    public boolean canAppendFile(String filename, UserHome home) {
        return true;
    }


    public boolean canOverwriteFile(String filename, UserHome home) {
        return true;
    }
}