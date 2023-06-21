package edu.usst.jayce.mc;

import java.util.ArrayList;
import java.util.List;

import edu.usst.jayce.printer.Printer;
import edu.usst.jayce.server.ctl.SystemCtl;
import edu.usst.jayce.server.enumeration.LogLevel;
import edu.usst.jayce.server.enumeration.VCLevel;
import edu.usst.jayce.server.pojo.ExtendStores;
import edu.usst.jayce.server.pojo.ServerSetting;
import edu.usst.jayce.server.util.ConfigureReader;
import edu.usst.jayce.server.util.ServerTimeUtil;
import edu.usst.jayce.ui.callback.GetServerStatus;
import edu.usst.jayce.ui.callback.UpdateSetting;
import edu.usst.jayce.ui.module.ServerUIModule;
import edu.usst.jayce.ui.pojo.FileSystemPath;

//UI界面模式启动器

public class UIRunner {

	private static UIRunner ui;
	
	// 实例化图形界面并显示它，同时将图形界面的各个操作与服务器控制器对应起来。
	private UIRunner() throws Exception {
		Printer.init(true);
		final ServerUIModule ui = ServerUIModule.getInsatnce();
		SystemCtl ctl = new SystemCtl();// 服务器控制层，用于连接UI与服务器内核
		ServerUIModule.setStartServer(() -> ctl.start());
		ServerUIModule.setOnCloseServer(() -> ctl.stop());
		ServerUIModule.setGetServerTime(() -> ServerTimeUtil.getServerTime());
		ServerUIModule.setGetServerStatus(new GetServerStatus() {

			@Override
			public boolean getServerStatus() {
				// TODO 自动生成的方法存根
				return ctl.started();
			}

			@Override
			public int getPropertiesStatus() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getPropertiesStatus();
			}

			@Override
			public int getPort() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getPort();
			}

			@Override
			public boolean getMustLogin() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().mustLogin();
			}

			@Override
			public LogLevel getLogLevel() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getLogLevel();
			}

			@Override
			public String getFileSystemPath() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getFileSystemPath();
			}

			@Override
			public int getBufferSize() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getBuffSize();
			}

			@Override
			public VCLevel getVCLevel() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getVCLevel();
			}

			@Override
			public List<FileSystemPath> getExtendStores() {
				List<FileSystemPath> fsps = new ArrayList<FileSystemPath>();
				for (ExtendStores es : ConfigureReader.instance().getExtendStores()) {
					FileSystemPath fsp = new FileSystemPath();
					fsp.setIndex(es.getIndex());
					fsp.setPath(es.getPath());
					fsp.setType(FileSystemPath.EXTEND_STORES_NAME);
					fsps.add(fsp);
				}
				return fsps;
			}


			@Override
			public LogLevel getInitLogLevel() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getInitLogLevel();
			}

			@Override
			public VCLevel getInitVCLevel() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getInitVCLevel();
			}

			@Override
			public String getInitFileSystemPath() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getInitFileSystemPath();
			}

			@Override
			public String getInitProt() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getInitPort();
			}

			@Override
			public String getInitBufferSize() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getInitBuffSize();
			}

			@Override
			public boolean isAllowChangePassword() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().isAllowChangePassword();
			}

			@Override
			public boolean isOpenFileChain() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().isOpenFileChain();
			}

			@Override
			public int getMaxExtendStoresNum() {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().getMaxExtendstoresNum();
			}
		});
		ServerUIModule.setUpdateSetting(new UpdateSetting() {

			@Override
			public boolean update(ServerSetting s) {
				// TODO 自动生成的方法存根
				return ConfigureReader.instance().doUpdate(s);
			}
		});
		ui.show();
	}

	public static UIRunner build() throws Exception {
		if (UIRunner.ui == null) {
			UIRunner.ui = new UIRunner();
		}
		return UIRunner.ui;
	}
}
