package edu.usst.jayce.ui.callback;

import java.util.List;

import edu.usst.jayce.server.enumeration.LogLevel;
import edu.usst.jayce.server.enumeration.VCLevel;
import edu.usst.jayce.ui.pojo.FileSystemPath;

public interface GetServerStatus
{
    int getPropertiesStatus();
    
    boolean getServerStatus();
    
    int getPort();
    
    String getInitProt();
    
    int getBufferSize();
    
    String getInitBufferSize();
    
    LogLevel getLogLevel();
    
    LogLevel getInitLogLevel();
    
    VCLevel getVCLevel();
    
    VCLevel getInitVCLevel();
    
    String getFileSystemPath();
    
    String getInitFileSystemPath();
    
    boolean getMustLogin();
    
    boolean isAllowChangePassword();
    
    boolean isOpenFileChain();
    
    List<FileSystemPath> getExtendStores();
    
    int getMaxExtendStoresNum();
}
