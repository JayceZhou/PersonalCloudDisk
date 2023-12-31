package edu.usst.jayce.server.service.impl;

import com.google.gson.Gson;
import edu.usst.jayce.printer.Printer;
import edu.usst.jayce.server.enumeration.AccountAuth;
import edu.usst.jayce.server.mapper.FolderMapper;
import edu.usst.jayce.server.mapper.NodeMapper;
import edu.usst.jayce.server.model.Node;
import edu.usst.jayce.server.pojo.VideoInfo;
import edu.usst.jayce.server.service.PlayVideoService;
import edu.usst.jayce.server.util.*;
import org.springframework.stereotype.Service;
import ws.schild.jave.MultimediaObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Service
public class PlayVideoServiceImpl implements PlayVideoService {
	@Resource
	private NodeMapper fm;
	@Resource
	private Gson gson;
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private LogUtil lu;
	@Resource
	private FolderMapper flm;
	@Resource
	private FolderUtil fu;
	@Resource
	private DFFMPEGLocator kfl;

	private VideoInfo foundVideo(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		if (fileId != null && fileId.length() > 0) {
			final Node f = this.fm.queryById(fileId);
			final VideoInfo vi = new VideoInfo(f);
			if (f != null) {
				final String account = (String) request.getSession().getAttribute("ACCOUNT");
				if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES,
						fu.getAllFoldersId(f.getFileParentFolder()))
						&& ConfigureReader.instance().accessFolder(flm.queryById(f.getFileParentFolder()), account)) {
					final String fileName = f.getFileName();
					// 检查视频格式
					final String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					switch (suffix) {
					case "mp4":
						if (kfl.getExecutablePath() != null) {
							// 因此对于mp4后缀的视频，进一步检查其编码是否为h264，如果是，则允许直接播放
							File target = fbu.getFileFromBlocks(f);
							if (target == null || !target.isFile()) {
								return null;
							}
							MultimediaObject mo = new MultimediaObject(target, kfl);
							try {
								if (mo.getInfo().getVideo().getDecoder().indexOf("h264") >= 0) {
									vi.setNeedEncode("N");
									return vi;
								}
							} catch (Exception e) {
								Printer.instance
										.print("错误：视频文件“" + f.getFileName() + "”在解析时出现意外错误。详细信息：" + e.getMessage());
								lu.writeException(e);
							}
							// 对于其他编码格式，则设定需要转码
							vi.setNeedEncode("Y");
						} else {
							vi.setNeedEncode("N");// 如果禁用了ffmpeg，那么怎么都不需要转码
						}
						return vi;
					case "mkv":
					case "mov":
					case "webm":
					case "avi":
					case "wmv":
					case "flv":
						if (kfl.getExecutablePath() != null) {
							vi.setNeedEncode("Y");
						} else {
							vi.setNeedEncode("N");
						}
						return vi;
					default:
						break;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getPlayVideoJson(final HttpServletRequest request) {
		final VideoInfo v = this.foundVideo(request);
		if (v != null) {
			return gson.toJson((Object) v);
		}
		return "ERROR";
	}
}
