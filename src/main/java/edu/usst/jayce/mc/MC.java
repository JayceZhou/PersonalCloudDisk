package edu.usst.jayce.mc;

public class MC {
	// main方法

	public static void main(final String[] args) {
		if (args == null || args.length == 0) {
			try {
				UIRunner.build();// 以界面模式启动CloudDisk。
			} catch (Exception e) {
				System.out.println(new String(
						"错误！无法以图形界面模式启动CloudDisk，您的操作系统可能不支持图形界面。您可以尝试使用命令模式参数“-console”来启动并开始使用CloudDisk。".getBytes()));
			}
		} else {
			ConsoleRunner.build(args);// 以控制台模式启动CloudDisk。
		}
	}
}
