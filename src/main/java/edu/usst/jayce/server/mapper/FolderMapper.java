package edu.usst.jayce.server.mapper;

import edu.usst.jayce.server.model.Folder;
import java.util.*;

public interface FolderMapper
{
    Folder queryById(final String fid);
    
    // 按照父文件夹的ID查找其下的所有文件夹（分页）
    List<Folder> queryByParentIdSection(final Map<String, Object> keyMap);
    
    // 按照父文件夹的ID统计其下的所有文件夹数目
    long countByParentId(final String pid);
    
    // 根据目标文件夹ID查询其中的所有文件夹
    List<Folder> queryByParentId(final String pid);
    
    Folder queryByParentIdAndFolderName(final Map<String, String> map);
    
    int insertNewFolder(final Folder f);
    
    int deleteById(final String folderId);
    
    int updateFolderNameById(final Map<String, String> map);
    
    int updateFolderConstraintById(final Map<String, Object> map);

	int moveById(Map<String, String> map);
	
	// 将指定文件夹节点（按照ID确定）更新
	int update(final Folder f);
}
