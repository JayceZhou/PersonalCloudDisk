package edu.usst.jayce.server.util;

import edu.usst.jayce.server.enumeration.AccountAuth;
import edu.usst.jayce.server.exception.FoldersTotalOutOfLimitException;
import edu.usst.jayce.server.mapper.FolderMapper;
import edu.usst.jayce.server.mapper.NodeMapper;
import edu.usst.jayce.server.model.Folder;
import edu.usst.jayce.server.model.Node;
import org.springframework.stereotype.*;
import javax.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FolderUtil {

	@Resource
	private FolderMapper fm;
	@Resource
	private NodeMapper fim;
	@Resource
	private FileBlockUtil fbu;

	// 获得指定文件夹的所有上级文件夹
	public List<Folder> getParentList(final String fid) {
		Folder f = this.fm.queryById(fid);
		final List<Folder> folderList = new ArrayList<Folder>();
		if (f != null) {
			while (!f.getFolderParent().equals("null") && folderList.size() < Integer.MAX_VALUE) {
				f = this.fm.queryById(f.getFolderParent());
				folderList.add(f);
			}
		}
		Collections.reverse(folderList);
		return folderList;
	}

	public List<String> getAllFoldersId(final String fid) {
		List<String> idList = new ArrayList<>();
		idList.addAll(getParentList(fid).parallelStream().map((e) -> e.getFolderId()).collect(Collectors.toList()));
		idList.add(fid);
		return idList;
	}

	// 删除一个文件夹树
	public void deleteAllChildFolder(final String folderId) {
		final Thread deleteChildFolderThread = new Thread(() -> this.iterationDeleteFolder(folderId));
		deleteChildFolderThread.start();
	}

	private void iterationDeleteFolder(final String folderId) {
		final List<Folder> cf = (List<Folder>) this.fm.queryByParentId(folderId);
		for (final Folder f : cf) {
			this.iterationDeleteFolder(f.getFolderId());
		}
		final List<Node> files = (List<Node>) this.fim.queryByParentFolderId(folderId);
		for (final Node f2 : files) {
			this.fbu.deleteNode(f2);
		}
		this.fm.deleteById(folderId);
	}

	public Folder createNewFolder(final String parentId, String account, String folderName, String folderConstraint)
			throws FoldersTotalOutOfLimitException {
		if (!ConfigureReader.instance().authorized(account, AccountAuth.CREATE_NEW_FOLDER, getAllFoldersId(parentId))) {
			return null;
		}
		if (parentId == null || folderName == null || parentId.length() <= 0 || folderName.length() <= 0) {
			return null;
		}
		if (folderName.equals(".") || folderName.equals("..")) {
			return null;
		}
		final Folder parentFolder = this.fm.queryById(parentId);
		if (parentFolder == null) {
			return null;
		}
		if (!ConfigureReader.instance().accessFolder(parentFolder, account)) {
			return null;
		}
		if (fm.queryByParentId(parentId).parallelStream().anyMatch((e) -> e.getFolderName().equals(folderName))) {
			return null;
		}
		if (fm.countByParentId(parentId) >= FileNodeUtil.MAXIMUM_NUM_OF_SINGLE_FOLDER) {
			throw new FoldersTotalOutOfLimitException();
		}
		Folder f = new Folder();
		// 设置子文件夹约束等级，不允许子文件夹的约束等级比父文件夹低
		int pc = parentFolder.getFolderConstraint();
		if (folderConstraint != null) {
			try {
				int ifc = Integer.parseInt(folderConstraint);
				if (ifc > 0 && account == null) {
					return null;
				}
				if (ifc < pc) {
					return null;
				} else {
					f.setFolderConstraint(ifc);
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
		f.setFolderId(UUID.randomUUID().toString());
		f.setFolderName(folderName);
		f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
		if (account != null) {
			f.setFolderCreator(account);
		} else {
			f.setFolderCreator("匿名用户");
		}
		f.setFolderParent(parentId);
		int i = 0;
		while (true) {
			try {
				final int r = this.fm.insertNewFolder(f);
				if (r > 0) {
					return f;
				}
				break;
			} catch (Exception e) {
				f.setFolderId(UUID.randomUUID().toString());
				i++;
			}
			if (i >= 10) {
				break;
			}
		}
		return null;
	}

	// 检查新建的文件夹是否有效
	public boolean isValidFolder(Folder f) {
		Folder[] repeats = fm.queryByParentId(f.getFolderParent()).parallelStream()
				.filter((e) -> e.getFolderName().equals(f.getFolderName())).toArray(Folder[]::new);
		if (fm.queryById(f.getFolderParent()) == null || repeats.length > 1) {
			// 如果插入后存在：
			// 1，该文件夹没有有效的父级文件夹（死节点）；
			// 2，与同级的其他文件夹重名，
			// 那么它就是一个无效的文件夹，应将插入操作撤销
			// 所谓撤销，也就是将该文件夹的数据立即删除（如果有）
			deleteAllChildFolder(f.getFolderId());
			return false;// 返回“无效”的判定结果
		} else {
			return true;// 否则，该节点有效，返回结果
		}
	}

	// 在指定路径内复制一份目标文件夹的拷贝，并可设定新名称
	private Folder copyFolderByNewNameToPath(Folder prototype, String account, Folder parentFolder, String newName,
			String excludeFolderId) {
		if (prototype == null || parentFolder == null) {
			return null;
		}
		try {
			// 先在指定文件夹下创建原文件夹的副本
			Folder newFolder = createNewFolder(parentFolder.getFolderId(), account,
					newName == null ? prototype.getFolderName() : newName,
					"" + (prototype.getFolderConstraint() < parentFolder.getFolderConstraint()
							? parentFolder.getFolderConstraint()
							: prototype.getFolderConstraint()));
			if (newFolder == null) {
				return null;// 副本创建失败则直接返回失败，无需继续执行后续的操作
			}
			// excludeFolderId的传参思路是：该参数为null？那肯定是第一层迭代，将此节点的ID作为“禁入ID”传下去，
			// 如果不为null，则说明是第一层以下的迭代，接收上层传入的excludeFolderId，确保本层复制不触碰此ID代表的
			// 文件夹即可。
			if (excludeFolderId == null) {
				excludeFolderId = newFolder.getFolderId();
			}
			// 创建成功后，检查原文件夹内是否有子文件夹
			List<Folder> childs = fm.queryByParentId(prototype.getFolderId());
			// 若有，则迭代执行本操作直至最底层文件夹
			for (Folder c : childs) {
				// 如果拷贝路径下还有个“自己”，那么说明目标文件夹是原文件夹的一个子文件夹
				// 这个时候，必须跳过自己，继续拷贝其他的文件夹
				if (c.getFolderId().equals(excludeFolderId) || c.getFolderId().equals(newFolder.getFolderId())) {
					continue;
				}
				// 注意：复制子文件夹时必须将newName传为null！因为子文件夹能存在就不会重名。
				if (copyFolderByNewNameToPath(c, account, newFolder, null, excludeFolderId) == null) {
					return null;// 如果中途哪个子文件夹复制失败，则返回失败
				}
			}
			// 之后，再复制原文件夹中的文件节点，并将文件的副本放在文件夹的副本下
			List<Node> nodes = fim.queryByParentFolderId(prototype.getFolderId());
			for (Node n : nodes) {
				Node newNode = fbu.insertNewNode(n.getFileName(), account, n.getFilePath(), n.getFileSize(),
						newFolder.getFolderId());
				if (newNode == null) {
					return null;// 某个文件节点复制失败同样返回失败
				}
			}
			// 上述操作都成功了？那么复制成功。
			return newFolder;
		} catch (FoldersTotalOutOfLimitException e) {
			return null;
		}
	}

	// 在指定路径内复制一份目标文件夹的拷贝，并可设定新名称
	public Folder copyFolderByNewNameToPath(Folder prototype, String account, Folder parentFolder, String newName) {
		return copyFolderByNewNameToPath(prototype, account, parentFolder, newName, null);
	}

	// 获取一个文件夹当前的逻辑路径
	public String getFolderPath(Folder f) {
		List<Folder> l = getParentList(f.getFolderId());
		StringBuffer pl = new StringBuffer();
		for (Folder i : l) {
			pl.append(i.getFolderName() + "/");
		}
		pl.append(f.getFolderName());
		return pl.toString();
	}

	// 迭代修改子文件夹约束
	public void changeChildFolderConstraint(String folderId, int c) {
		List<Folder> cfs = fm.queryByParentId(folderId);
		for (Folder cf : cfs) {
			if (cf.getFolderConstraint() < c) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("newConstraint", c);
				map.put("folderId", cf.getFolderId());
				fm.updateFolderConstraintById(map);
			}
			changeChildFolderConstraint(cf.getFolderId(), c);
		}
	}
}
