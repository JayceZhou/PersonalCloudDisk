package edu.usst.jayce.server.util;

import edu.usst.jayce.server.enumeration.AccountAuth;
import edu.usst.jayce.server.mapper.FolderMapper;
import edu.usst.jayce.server.mapper.NodeMapper;
import edu.usst.jayce.server.model.Folder;
import edu.usst.jayce.server.model.Node;
import edu.usst.jayce.server.pojo.ExtendStores;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.*;

import edu.usst.jayce.printer.Printer;
import javax.annotation.*;
import org.springframework.web.multipart.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.*;
import java.util.zip.ZipEntry;

import org.zeroturnaround.zip.*;

// 文件块整合操作工具
@Component
public class FileBlockUtil {
	@Resource
	private NodeMapper fm;// 节点映射，用于遍历
	@Resource
	private FolderMapper flm;// 文件夹映射，同样用于遍历
	@Resource
	@Lazy
	private LogUtil lu;// 日志工具
	@Resource
	@Lazy
	private FolderUtil fu;// 文件夹操作工具

	// 清理临时文件夹
	public void initTempDir() {
		final String tfPath = ConfigureReader.instance().getTemporaryfilePath();
		final File f = new File(tfPath);
		if (f.isDirectory()) {
			try {
				Iterator<Path> listFiles = Files.newDirectoryStream(f.toPath()).iterator();
				while (listFiles.hasNext()) {
					File tempFile = listFiles.next().toFile();
					if(!tempFile.getName().startsWith(".")) {
						tempFile.delete();
					}
				}
			} catch (IOException e) {
				lu.writeException(e);
				Printer.instance.print("错误：临时文件清理失败，请手动清理" + f.getAbsolutePath() + "文件夹内的临时文件。");
			}
		} else {
			if (!f.mkdir()) {
				Printer.instance.print("错误：无法创建临时文件夹" + f.getAbsolutePath() + "，请检查主文件系统存储路径是否可用。");
			}
		}
	}

	// 将新上传的文件存入文件系统
	public File saveToFileBlocks(final MultipartFile f) {
		// 得到全部扩展存储区
		List<ExtendStores> ess = getExtendStoresBySort();
		if (ess.size() > 0) {
			// 从文件块最少的开始遍历这些扩展存储区
			for (ExtendStores es : ess) {
				if (es.getPath().getFreeSpace() > f.getSize()) {
					// 如果该存储区的空余容量大于待上传文件的体积
					File file = null;
					try {
						// 则尝试在该存储区中生成一个空文件块
						file = createNewBlock(es.getIndex() + "_", es.getPath());
						if (file != null) {
							// 生成成功，尝试存入数据
							f.transferTo(file);
							return file;
						} else {
							continue;// 如果本处无法生成新文件块，那么在其他路径下继续尝试
						}
					} catch (IOException e) {
						// 出现IO异常，则删除残留文件并继续尝试其他扩展存储区
						if (file != null) {
							file.delete();
						}
						continue;
					} catch (Exception e) {
						// 出现其他异常则记录日志
						lu.writeException(e);
						Printer.instance.print(e.getMessage());
						continue;
					}
				}
			}
		}
		// 如果不存在扩展存储区或者最大的扩展存储区的剩余容量依旧小于指定大小，则尝试在主文件系统路径下生成新文件块
		File file = null;
		try {
			file = createNewBlock("file_", new File(ConfigureReader.instance().getFileBlockPath()));
			if (file != null) {
				// 生成成功，则尝试存入数据
				f.transferTo(file);
				return file;
			}
		} catch (Exception e) {
			// 出现异常则记录日志，则删除残留数据并返回null
			if (file != null) {
				file.delete();
			}
			lu.writeException(e);
			Printer.instance.print("错误：文件块生成失败，无法存入新的文件数据。详细信息：" + e.getMessage());
		}
		// 因其他原因生成失败也返回null
		return null;
	}

	// 将新上传的文件存入文件系统
	public File saveToFileBlocks(final File f) {
		// 得到全部扩展存储区
		List<ExtendStores> ess = getExtendStoresBySort();
		if (ess.size() > 0) {
			// 从文件块最少的开始遍历这些扩展存储区
			for (ExtendStores es : ess) {
				if (es.getPath().getFreeSpace() > f.length()) {
					// 如果该存储区的空余容量大于待上传文件的体积
					File file = null;
					try {
						// 则尝试在该存储区中生成一个空文件块
						file = createNewBlock(es.getIndex() + "_", es.getPath());
						if (file != null) {
							// 生成成功，尝试存入数据
							Files.move(f.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
							return file;
						} else {
							continue;// 如果本处无法生成新文件块，那么在其他路径下继续尝试
						}
					} catch (IOException e) {
						// 出现IO异常，则删除残留文件并继续尝试其他扩展存储区
						if (file != null) {
							file.delete();
						}
						continue;
					} catch (Exception e) {
						// 出现其他异常则记录日志
						lu.writeException(e);
						Printer.instance.print(e.getMessage());
						continue;
					}
				}
			}
		}
		// 如果不存在扩展存储区或者最大的扩展存储区的剩余容量依旧小于指定大小，则尝试在主文件系统路径下生成新文件块
		File file = null;
		try {
			file = createNewBlock("file_", new File(ConfigureReader.instance().getFileBlockPath()));
			if (file != null) {
				// 生成成功，则尝试存入数据
				Files.move(f.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return file;
			}
		} catch (Exception e) {
			// 出现异常则记录日志，则删除残留数据并返回null
			if (file != null) {
				file.delete();
			}
			lu.writeException(e);
			Printer.instance.print("错误：文件块生成失败，无法存入新的文件数据。详细信息：" + e.getMessage());
		}
		// 因其他原因生成失败也返回null
		return null;
	}

	// 以剩余容量从小到大排序获取扩展存储区列表
	private List<ExtendStores> getExtendStoresBySort() {
		List<ExtendStores> ess = ConfigureReader.instance().getExtendStores();
		if (ess.size() > 0) {
			// 将所有扩展存储区按照已存储文件块的数目从小到大进行排序
			Collections.sort(ess, new Comparator<ExtendStores>() {
				@Override
				public int compare(ExtendStores o1, ExtendStores o2) {
					try {
						// 通常情况下，直接比较子文件列表长度即可
						return o1.getPath().list().length - o2.getPath().list().length;
					} catch (Exception e) {
						try {
							// 如果文件太多以至于超出数组上限，则换用如下统计方法
							long dValue = Files.list(o1.getPath().toPath()).count()
									- Files.list(o2.getPath().toPath()).count();
							return dValue > 0L ? 1 : dValue == 0 ? 0 : -1;
						} catch (IOException e1) {
							return 0;
						}
					}
				}
			});
		}
		return ess;
	}

	// 生成创建一个在指定路径下名称（编号）绝对不重复的新文件块
	private File createNewBlock(String prefix, File parent) throws IOException {
		int appendIndex = 0;
		int retryNum = 0;
		String newName = prefix + UUID.randomUUID().toString().replace("-", "");
		File newBlock = new File(parent, newName + ".block");
		while (!newBlock.createNewFile()) {
			if (appendIndex >= 0 && appendIndex < Integer.MAX_VALUE) {
				newBlock = new File(parent, newName + "_" + appendIndex + ".block");
				appendIndex++;
			} else {
				if (retryNum >= 5) {
					return null;
				} else {
					newName = prefix + UUID.randomUUID().toString().replace("-", "");
					newBlock = new File(parent, newName + ".block");
					retryNum++;
				}
			}
		}
		return newBlock;
	}

	// 计算上传文件的体积
	public String getFileSize(final long size) {
		final int mb = (int) (size / 1048576L);
		return "" + mb;
	}

	// 删除文件系统中的一个文件节点，同时清理文件块
	public boolean deleteNode(Node f) {
		if (f != null) {
			if (fm.deleteById(f.getFileId()) > 0) {
				// 尝试清理该节点对应的文件块
				if (!clearFileBlock(f)) {
					// 如果清理节点失败，尝试回滚节点
					if (fm.insert(f) > 0) {
						// 回滚失败，则认为删除失败
						return false;
					}
				}
				return true;// 文件清理成功，或者回滚失败，都认为删除成功
			}
		}
		return false;// 若节点删除失败，或是节点为null，则返回false
	}

	// 清理文件块
	private boolean clearFileBlock(Node n) {
		// 获取“删除留档”功能的设置路径。
		String recycleBinPath = ConfigureReader.instance().getRecycleBinPath();
		// 获取节点对应的文件块
		File file = getFileFromBlocks(n);
		// 检查该节点引用的文件块是否被其他节点引用
		Map<String, String> map = new HashMap<>();
		map.put("path", n.getFilePath());
		map.put("fileId", n.getFileId());
		List<Node> nodes = fm.queryByPathExcludeById(map);
		if (nodes == null || nodes.isEmpty()) {
			// 若已经无任何节点再引用此文件块
			if (file != null) {
				if (recycleBinPath != null && !saveToRecycleBin(file, recycleBinPath, n.getFileName(), false)) {
					// 若开启了“删除留档”功能且留档失败，则认为清理失败
					return false;
				} else {
					// 否则直接删除此文件块
					if (!file.delete()) {
						// 如果文件块删除失败
						if (file.exists()) {
							return false;// 如果如果文件块仍存在，返回false
						}
					}
				}
			} else {
				// 如果文件块获取失败，则检查是否开启了“删除留档”功能
				if (recycleBinPath != null) {
					// 如果开启了，那么由于无法留档，因此认为清理失败
					return false;
				}
				// 否则认为清理成功
			}
		} else {
			// 若仍有其他节点引用此文件块
			if (recycleBinPath != null && !saveToRecycleBin(file, recycleBinPath, n.getFileName(), true)) {
				// 若开启了“删除留档”功能且留档失败，则认为清理失败
				return false;
			}
			// 否则认为清理成功
		}
		return true;
	}

	private boolean saveToRecycleBin(File block, String recycleBinPath, String originalName, boolean isCopy) {
		File recycleBinDir = new File(recycleBinPath);
		if (recycleBinDir.isDirectory()) {
			// 当留档路径合法时，查找其中是否有当前日期的留档子文件夹
			File dateDir = new File(recycleBinDir, ServerTimeUtil.accurateToLogName());
			if (dateDir.isDirectory() || dateDir.mkdir()) {
				// 如果有，则直接使用，否则创建当前日期的留档子文件夹，之后检查此文件夹内是否有重名留档文件
				int i = 0;
				List<String> fileNames = Arrays.asList(dateDir.list());
				String newName = originalName;
				while (fileNames.contains(newName)) {
					i++;
					if (originalName.indexOf(".") >= 0) {
						newName = originalName.substring(0, originalName.lastIndexOf(".")) + " (" + i + ")"
								+ originalName.substring(originalName.lastIndexOf("."));
					} else {
						newName = originalName + " (" + i + ")";
					}
				}
				// 在确保不会产生重名文件的前提下，按照移动或拷贝两种方式留档
				File saveFile = new File(dateDir, newName);
				try {
					if (isCopy) {
						Files.copy(block.toPath(), saveFile.toPath());
					} else {
						Files.move(block.toPath(), saveFile.toPath());
					}
					// 如果不抛出任何异常，则操作成功
					return true;
				} catch (Exception e) {
					lu.writeException(e);
				}
			}
		}
		return false;
	}

	// 得到文件系统中的一个文件块
	public File getFileFromBlocks(Node f) {
		// 检查该节点对应的文件块存放于哪个位置（主文件系统/扩展存储区）
		try {
			File file = null;
			if (f.getFilePath().startsWith("file_")) {// 存放于主文件系统中
				// 直接从主文件系统的文件块存放区获得对应的文件块
				file = new File(ConfigureReader.instance().getFileBlockPath(), f.getFilePath());
			} else {// 存放于扩展存储区
				short index = Short.parseShort(f.getFilePath().substring(0, f.getFilePath().indexOf('_')));
				// 根据编号查到对应的扩展存储区路径，进而获取对应的文件块
				file = new File(ConfigureReader.instance().getExtendStores().parallelStream()
						.filter((e) -> e.getIndex() == index).findAny().get().getPath(), f.getFilePath());
			}
			if (file.isFile()) {
				return file;
			}
		} catch (Exception e) {
			lu.writeException(e);
			Printer.instance.print("错误：文件数据读取失败。详细信息：" + e.getMessage());
		}
		return null;
	}

	// 校对文件块与文件节点
	public void checkFileBlocks() {
		Thread checkThread = new Thread(() -> {
			// 检查是否存在未正确对应文件块的文件节点信息，若有则删除，从而确保文件节点信息不出现遗留问题
			checkNodes("root");
			// 检查是否存在未正确对应文件节点的文件块，若有则删除，从而确保文件块不出现遗留问题
			List<File> paths = new ArrayList<>();
			paths.add(new File(ConfigureReader.instance().getFileBlockPath()));
			for (ExtendStores es : ConfigureReader.instance().getExtendStores()) {
				paths.add(es.getPath());
			}
			for (File path : paths) {
				try (DirectoryStream<Path> ds = Files.newDirectoryStream(path.toPath())) {
					Iterator<Path> blocks = ds.iterator();
					while (blocks.hasNext()) {
						File testBlock = blocks.next().toFile();
						if (testBlock.isFile() && !testBlock.getName().startsWith(".")) {
							List<Node> nodes = fm.queryByPath(testBlock.getName());
							if (nodes == null || nodes.isEmpty()) {
								testBlock.delete();
							}
						}
					}
				} catch (IOException e) {
					Printer.instance.print("警告：文件节点效验时发生意外错误，可能未能正确完成文件节点效验。错误信息：" + e.getMessage());
					lu.writeException(e);
				}
			}
		});
		checkThread.start();
	}

	// 校对文件节点，要求某一节点必须有对应的文件块，否则将其移除（避免出现死节点）
	private void checkNodes(String fid) {
		List<Node> nodes = fm.queryByParentFolderId(fid);
		for (Node node : nodes) {
			File block = getFileFromBlocks(node);
			if (block == null) {
				fm.deleteById(node.getFileId());
			}
		}
		List<Folder> folders = flm.queryByParentId(fid);
		for (Folder fl : folders) {
			checkNodes(fl.getFolderId());
		}
	}

	// 将指定节点及文件夹打包为ZIP压缩文件。
	public String createZip(final List<String> idList, final List<String> fidList, String account) {
		final String zipname = "tf_" + UUID.randomUUID().toString() + ".zip";
		final String tempPath = ConfigureReader.instance().getTemporaryfilePath();
		final File f = new File(tempPath, zipname);
		try {
			final List<ZipEntrySource> zs = new ArrayList<>();
			// 避免压缩时出现同名文件导致打不开：
			final List<Folder> folders = new ArrayList<>();
			for (String fid : fidList) {
				Folder fo = flm.queryById(fid);
				if (ConfigureReader.instance().accessFolder(fo, account) && ConfigureReader.instance()
						.authorized(account, AccountAuth.DOWNLOAD_FILES, fu.getAllFoldersId(fo.getFolderParent()))) {
					if (fo != null) {
						folders.add(fo);
					}
				}
			}
			final List<Node> nodes = new ArrayList<>();
			for (String id : idList) {
				Node n = fm.queryById(id);
				if (ConfigureReader.instance().accessFolder(flm.queryById(n.getFileParentFolder()), account)
						&& ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES,
								fu.getAllFoldersId(n.getFileParentFolder()))) {
					if (n != null) {
						nodes.add(n);
					}
				}
			}
			for (Folder fo : folders) {
				int i = 1;
				String flname = fo.getFolderName();
				while (true) {
					if (folders.parallelStream().filter((e) -> e.getFolderName().equals(fo.getFolderName()))
							.count() > 1) {
						fo.setFolderName(flname + " " + i);
						i++;
					} else {
						break;
					}
				}
				addFoldersToZipEntrySourceArray(fo, zs, account, "");
			}
			for (Node node : nodes) {
				if (ConfigureReader.instance().accessFolder(flm.queryById(node.getFileParentFolder()), account)) {
					int i = 1;
					String fname = node.getFileName();
					while (true) {
						if (nodes.parallelStream().filter((e) -> e.getFileName().equals(node.getFileName())).count() > 1
								|| folders.parallelStream().filter((e) -> e.getFolderName().equals(node.getFileName()))
										.count() > 0) {
							if (fname.indexOf(".") >= 0) {
								node.setFileName(fname.substring(0, fname.lastIndexOf(".")) + " (" + i + ")"
										+ fname.substring(fname.lastIndexOf(".")));
							} else {
								node.setFileName(fname + " (" + i + ")");
							}
							i++;
						} else {
							break;
						}
					}
					zs.add((ZipEntrySource) new FileSource(node.getFileName(), getFileFromBlocks(node)));
				}
			}
			ZipUtil.pack(zs.toArray(new ZipEntrySource[0]), f);
			return zipname;
		} catch (Exception e) {
			lu.writeException(e);
			Printer.instance.print(e.getMessage());
			return null;
		}
	}

	// 迭代生成ZIP文件夹单元，将一个文件夹内的文件和文件夹也进行打包
	private void addFoldersToZipEntrySourceArray(Folder f, List<ZipEntrySource> zs, String account, String parentPath) {
		if (f != null && ConfigureReader.instance().accessFolder(f, account)) {
			String folderName = f.getFolderName();
			String thisPath = parentPath + folderName + "/";
			zs.add(new ZipEntrySource() {

				@Override
				public String getPath() {
					return thisPath;
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return null;
				}

				@Override
				public ZipEntry getEntry() {
					return new ZipEntry(thisPath);
				}
			});
			List<Folder> folders = flm.queryByParentId(f.getFolderId());
			for (Folder fo : folders) {
				int i = 1;
				String flname = fo.getFolderName();
				while (true) {
					if (folders.parallelStream().filter((e) -> e.getFolderName().equals(fo.getFolderName()))
							.count() > 1) {
						fo.setFolderName(flname + " " + i);
						i++;
					} else {
						break;
					}
				}
				addFoldersToZipEntrySourceArray(fo, zs, account, thisPath);
			}
			List<Node> nodes = fm.queryByParentFolderId(f.getFolderId());
			for (Node node : nodes) {
				int i = 1;
				String fname = node.getFileName();
				while (true) {
					if (nodes.parallelStream().filter((e) -> e.getFileName().equals(node.getFileName())).count() > 1
							|| folders.parallelStream().filter((e) -> e.getFolderName().equals(node.getFileName()))
									.count() > 0) {
						if (fname.indexOf(".") >= 0) {
							node.setFileName(fname.substring(0, fname.lastIndexOf(".")) + " (" + i + ")"
									+ fname.substring(fname.lastIndexOf(".")));
						} else {
							node.setFileName(fname + " (" + i + ")");
						}
						i++;
					} else {
						break;
					}
				}
				zs.add(new FileSource(thisPath + node.getFileName(), getFileFromBlocks(node)));
			}
		}
	}

	// 生成指定文件块资源对应的ETag标识
	public String getETag(File block) {
		if (block != null && block.exists()) {
			StringBuffer sb = new StringBuffer();
			sb.append("W\"");
			sb.append(block.length());
			sb.append("-");
			sb.append(block.lastModified());
			sb.append("\"");
			return sb.toString();
		}
		return "W\"0-0\"";
	}

	// 插入一个新的文件节点至文件系统数据库中
	public Node insertNewNode(String fileName, String account, String filePath, String fileSize,
			String fileParentFolder) {
		final Node f2 = new Node();
		f2.setFileId(UUID.randomUUID().toString());
		if (account != null) {
			f2.setFileCreator(account);
		} else {
			f2.setFileCreator("\u533f\u540d\u7528\u6237");
		}
		f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
		f2.setFileName(fileName);
		f2.setFileParentFolder(fileParentFolder);
		f2.setFilePath(filePath);
		f2.setFileSize(fileSize);
		int i = 0;
		// 尽可能避免UUID重复的情况发生，重试10次
		while (true) {
			try {
				if (this.fm.insert(f2) > 0) {
					if (isValidNode(f2)) {
						return f2;
					} else {
						break;
					}
				}
				break;
			} catch (Exception e) {
				f2.setFileId(UUID.randomUUID().toString());
				i++;
			}
			if (i >= 10) {
				break;
			}
		}
		return null;
	}

	// 检查指定的文件节点是否存在同名问题
	public boolean isValidNode(Node n) {
		Node[] repeats = fm.queryByParentFolderId(n.getFileParentFolder()).parallelStream()
				.filter((e) -> e.getFileName().equals(n.getFileName())).toArray(Node[]::new);
		if (flm.queryById(n.getFileParentFolder()) == null || repeats.length > 1) {
			// 如果插入后存在：
			// 1，该节点没有有效的父级文件夹（死节点）；
			// 2，与同级的其他节点重名，
			// 那么它就是一个无效的节点，应将插入操作撤销
			// 所谓撤销，也就是将该节点的数据立即删除（如果有）
			fm.deleteById(n.getFileId());
			return false;// 返回“无效”的判定结果
		} else {
			return true;// 否则，该节点有效，返回结果
		}
	}

	// 获取一个节点当前的逻辑路径
	public String getNodePath(Node n) {
		Folder folder = flm.queryById(n.getFileParentFolder());
		List<Folder> l = fu.getParentList(folder.getFolderId());
		StringBuffer pl = new StringBuffer();
		for (Folder i : l) {
			pl.append(i.getFolderName() + "/");
		}
		pl.append(folder.getFolderName());
		pl.append("/");
		pl.append(n.getFileName());
		return pl.toString();
	}

}
