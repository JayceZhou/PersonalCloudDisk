package edu.usst.jayce.server.mapper;

import edu.usst.jayce.server.model.Node;

import java.util.List;
import java.util.Map;

public interface NodeMapper {
	//根据文件夹ID查询其中的所有文件节点
	List<Node> queryByParentFolderId(final String pfid);

	//按照父文件夹的ID查找其下的所有文件（分页）
	List<Node> queryByParentFolderIdSection(final Map<String, Object> keyMap);

	// 按照父文件夹的ID统计其下的所有文件数目
	long countByParentFolderId(final String pfid);

	int insert(final Node f);

	int update(final Node f);

	int deleteById(final String fileId);

	Node queryById(final String fileId);

	int updateFileNameById(final Map<String, String> map);

	// 根据文件块DI查询对所有对应的节点
	List<Node> queryByPath(final String path);

	// 根据文件块DI查询对所有对应的节点，并排除指定节点
	List<Node> queryByPathExcludeById(final Map<String, String> map);

	// 查询与目标文件节点处于同一文件夹下的全部文件节点
	List<Node> queryBySomeFolder(final String fileId);

}
