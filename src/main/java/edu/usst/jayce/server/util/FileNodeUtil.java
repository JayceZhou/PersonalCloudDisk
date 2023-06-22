package edu.usst.jayce.server.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import edu.usst.jayce.printer.Printer;
import edu.usst.jayce.server.model.Folder;
import edu.usst.jayce.server.model.Node;

// 文件节点初始化工具
public class FileNodeUtil {

	private FileNodeUtil() {
	};

	private static Connection conn;
	private static String url;// 当前链接的节点数据库位置

	// 单文件夹内允许存放的最大文件和文件夹数目
	public static final int MAXIMUM_NUM_OF_SINGLE_FOLDER = Integer.MAX_VALUE;

	// 为数据库建立初始化节点表
	public static void initNodeTableToDataBase() {
		Printer.instance.print("初始化文件节点...");
		try {
			if (conn == null) {
				Class.forName(ConfigureReader.instance().getFileNodePathDriver()).newInstance();
			}
			String newUrl = ConfigureReader.instance().getFileNodePathURL();
			// 判断当前位置是否初始化文件节点
			if (url == null || !url.equals(newUrl)) {
				conn = DriverManager.getConnection(newUrl, ConfigureReader.instance().getFileNodePathUserName(),
						ConfigureReader.instance().getFileNodePathPassWord());
				url = newUrl;
				// 检查是否存在旧版本的归档数据，若有，则尝试将其导入。
				File upgradeFile = new File(ConfigureReader.instance().getFileNodePath() + "upgrade.sql");
				if (upgradeFile.isFile()) {
					Printer.instance.print("正在从旧版本导入数据...");
					final Statement state0 = conn.createStatement();
					state0.execute("RUNSCRIPT FROM '" + upgradeFile.getAbsolutePath() + "' FROM_1X");
					state0.close();
					if (!upgradeFile.delete()) {
						throw new IOException("错误：旧归档文件删除失败：" + upgradeFile.getAbsolutePath());
					}
				}
				// 生成数据库表并初始化数据内容
				final Statement state1 = conn.createStatement();
				state1.execute(
						"CREATE TABLE IF NOT EXISTS FOLDER(folder_id VARCHAR(128) PRIMARY KEY,  folder_name VARCHAR(128) NOT NULL,folder_creation_date VARCHAR(128) NOT NULL,  folder_creator VARCHAR(128) NOT NULL,folder_parent VARCHAR(128) NOT NULL,folder_constraint INT NOT NULL)");
				state1.executeQuery("SELECT count(*) FROM FOLDER WHERE folder_id = 'root'");
				ResultSet rs = state1.getResultSet();
				if (rs.next()) {
					if (rs.getInt(1) == 0) {
						final Statement state11 = conn.createStatement();
						state11.execute("INSERT INTO FOLDER VALUES('root', 'ROOT', '--', '--', 'null', 0)");
					}
				}
				state1.close();
				final Statement state2 = conn.createStatement();
				state2.execute(
						"CREATE TABLE IF NOT EXISTS FILE(file_id VARCHAR(128) PRIMARY KEY,file_name VARCHAR(128) NOT NULL,file_size VARCHAR(128) NOT NULL,file_parent_folder varchar(128) NOT NULL,file_creation_date varchar(128) NOT NULL,file_creator varchar(128) NOT NULL,file_path varchar(128) NOT NULL)");
				state2.close();
				// 为数据库生成索引，此处分为MySQL和H2两种操作
				if (ConfigureReader.instance().useMySQL()) {
					final Statement state4 = conn.createStatement();
					ResultSet indexCount = state4.executeQuery("SHOW INDEX FROM FILE WHERE Key_name = 'file_index'");
					if (!indexCount.next()) {
						final Statement state41 = conn.createStatement();
						state41.execute("CREATE INDEX file_index ON FILE (file_name)");
						state41.close();
					}
					state4.close();
				} else {
					final Statement state4 = conn.createStatement();
					state4.execute("CREATE INDEX IF NOT EXISTS file_index ON FILE (file_name)");
					state4.close();
				}
				// 生成用于持久化保存的、系统自动生成的、和文件系统相关设置项的存储表
				final Statement state5 = conn.createStatement();
				state5.execute(
						"CREATE TABLE IF NOT EXISTS PROPERTIES(propertie_key VARCHAR(128) PRIMARY KEY,propertie_value VARCHAR(128) NOT NULL)");
				state5.close();
			}
			Printer.instance.print("文件节点初始化完毕。");
		} catch (Exception e) {
			Printer.instance.print(e.getMessage());
			Printer.instance.print("错误：文件节点初始化失败。");
		}
	}

	// 获取文件节点的数据库链接
	public static Connection getNodeDBConnection() {
		return conn;
	}

	// 生成不与已存在文件同名的、带计数的新文件名
	public static String getNewNodeName(String originalName, List<Node> nodes) {
		int i = 0;
		List<String> fileNames = Arrays
				.asList(nodes.stream().parallel().map((t) -> t.getFileName()).toArray(String[]::new));
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
		return newName;
	}

	// 生成不与已存在文件夹同名的、带计数的新文件夹名
	public static String getNewFolderName(String originalName, List<? extends Folder> folders) {
		int i = 0;
		List<String> fileNames = Arrays
				.asList(folders.stream().parallel().map((t) -> t.getFolderName()).toArray(String[]::new));
		String newName = originalName;
		while (fileNames.contains(newName)) {
			i++;
			newName = originalName + " " + i;
		}
		return newName;
	}

	// 生成不与已存在文件夹同名的、带计数的新文件夹名
	public static String getNewFolderName(Folder folder, File parentfolder) {
		int i = 0;
		List<String> fileNames = Arrays.asList(Arrays.stream(parentfolder.listFiles()).parallel()
				.filter((e) -> e.isDirectory()).map((t) -> t.getName()).toArray(String[]::new));
		String newName = folder.getFolderName();
		while (fileNames.contains(newName)) {
			i++;
			newName = folder.getFolderName() + " " + i;
		}
		return newName;
	}

	// 生成不与已存在文件同名的、带计数的新文件名
	public static String getNewNodeName(Node n, File folder) {
		int i = 0;
		List<String> fileNames = Arrays.asList(Arrays.stream(folder.listFiles()).parallel().filter((e) -> e.isFile())
				.map((t) -> t.getName()).toArray(String[]::new));
		String newName = n.getFileName();
		while (fileNames.contains(newName)) {
			i++;
			if (n.getFileName().indexOf(".") >= 0) {
				newName = n.getFileName().substring(0, n.getFileName().lastIndexOf(".")) + " (" + i + ")"
						+ n.getFileName().substring(n.getFileName().lastIndexOf("."));
			} else {
				newName = n.getFileName() + " (" + i + ")";
			}
		}
		return newName;
	}

}
