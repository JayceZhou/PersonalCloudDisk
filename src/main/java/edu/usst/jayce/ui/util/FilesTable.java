package edu.usst.jayce.ui.util;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import edu.usst.jayce.server.model.Node;
import edu.usst.jayce.util.file_system_manager.pojo.Folder;

// 文件列表表格
public class FilesTable extends JTable {

	private static final String[] columns = new String[] { "名称", "创建日期", "大小（MB）", "创建者" };
	private static List<Folder> folders;// 当前显示的文件夹列表
	public static final int MAX_LIST_LIMIT = Integer.MAX_VALUE;

	/**  */
	private static final long serialVersionUID = -3436472714356711024L;

	public FilesTable() {
		// TODO 自动生成的构造函数存根
		super(new Object[][] {}, columns);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO 自动生成的方法存根
		return false;
	}

	public void updateValues(List<Folder> folders, List<Node> files) {
		Runnable doUpdate = new Runnable() {
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				try {
					setModel(new TableModel() {
						@Override
						public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
							// TODO 自动生成的方法存根
						}

						@Override
						public void removeTableModelListener(TableModelListener l) {
							// TODO 自动生成的方法存根
						}

						@Override
						public boolean isCellEditable(int rowIndex, int columnIndex) {
							// TODO 自动生成的方法存根
							return false;
						}

						@Override
						public Object getValueAt(int rowIndex, int columnIndex) {
							// TODO 自动生成的方法存根
							switch (columnIndex) {
							case 0:
								return rowIndex < folders.size() ? "/" + folders.get(rowIndex).getFolderName()
										: files.get(rowIndex - folders.size()).getFileName();
							case 1:
								return rowIndex < folders.size() ? folders.get(rowIndex).getFolderCreationDate()
										: files.get(rowIndex - folders.size()).getFileCreationDate();
							case 2:
								return rowIndex < folders.size() ? "--"
										: files.get(rowIndex - folders.size()).getFileSize();
							case 3:
								return rowIndex < folders.size() ? folders.get(rowIndex).getFolderCreator()
										: files.get(rowIndex - folders.size()).getFileCreator();
							default:
								return "--";
							}
						}

						@Override
						public int getRowCount() {
							long totalSize = folders.size() + files.size();
							return totalSize > MAX_LIST_LIMIT ? MAX_LIST_LIMIT : (int) totalSize;
						}

						@Override
						public String getColumnName(int columnIndex) {
							// TODO 自动生成的方法存根
							return columns[columnIndex];
						}

						@Override
						public int getColumnCount() {
							// TODO 自动生成的方法存根
							return columns.length;
						}

						@Override
						public Class<?> getColumnClass(int columnIndex) {
							// TODO 自动生成的方法存根
							return Object.class;
						}

						@Override
						public void addTableModelListener(TableModelListener l) {
							// TODO 自动生成的方法存根
						}
					});
					setRowFontColor();
					validate();
					FilesTable.folders = folders;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
		// 避免操作过快导致的异常
		Thread t = new Thread(() -> {
			SwingUtilities.invokeLater(doUpdate);
		});
		t.start();
	}

	private void setRowFontColor() {
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
			/**  */
			private static final long serialVersionUID = 5132133158132959506L;

			@Override
			protected void setValue(Object value) {
				// TODO 自动生成的方法存根
				if (value instanceof String && ((String) value).startsWith("/")) {
					setForeground(Color.BLUE);
				} else {
					setForeground(Color.black);
				}
				setText((String) value);
			}
		};
		DefaultTableCellRenderer dtcr2 = new DefaultTableCellRenderer() {
			/**  */
			private static final long serialVersionUID = 5132133158132959506L;

			@Override
			protected void setValue(Object value) {
				// TODO 自动生成的方法存根
				if (value instanceof String && ((String) value).equals("0")) {
					setText((String) "<1");
				} else {
					setText((String) value);
				}
			}
		};
		getColumn(columns[0]).setCellRenderer(dtcr);
		getColumn(columns[2]).setCellRenderer(dtcr2);
	}

	// 获取被双击的文件夹
	public Folder getDoubleClickFolder(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int row = rowAtPoint(e.getPoint());
			if (row >= 0 && row < folders.size()) {
				return folders.get(row);
			}
		}
		return null;
	}
}
