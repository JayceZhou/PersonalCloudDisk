package edu.usst.jayce.server.service.impl;

import com.google.gson.Gson;
import edu.usst.jayce.server.enumeration.AccountAuth;
import edu.usst.jayce.server.mapper.FolderMapper;
import edu.usst.jayce.server.mapper.NodeMapper;
import edu.usst.jayce.server.model.Node;
import edu.usst.jayce.server.pojo.AudioInfoList;
import edu.usst.jayce.server.service.PlayAudioService;
import edu.usst.jayce.server.util.AudioInfoUtil;
import edu.usst.jayce.server.util.ConfigureReader;
import edu.usst.jayce.server.util.FolderUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class PlayAudioServiceImpl implements PlayAudioService {
	@Resource
	private NodeMapper fm;
	@Resource
	private AudioInfoUtil aiu;
	@Resource
	private Gson gson;
	@Resource
	private FolderUtil fu;
	@Resource
	private FolderMapper flm;

	private AudioInfoList foundAudios(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		if (fileId != null && fileId.length() > 0) {
			Node targetNode = fm.queryById(fileId);
			if (targetNode != null) {
				final String account = (String) request.getSession().getAttribute("ACCOUNT");
				if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES,
						fu.getAllFoldersId(targetNode.getFileParentFolder()))
						&& ConfigureReader.instance().accessFolder(flm.queryById(targetNode.getFileParentFolder()),
								account)) {
					final List<Node> blocks = (List<Node>) this.fm.queryBySomeFolder(fileId);
					return this.aiu.transformToAudioInfoList(blocks, fileId);
				}
			}
		}
		return null;
	}

	// 解析播放音频文件
	public String getAudioInfoListByJson(final HttpServletRequest request) {
		final AudioInfoList ail = this.foundAudios(request);
		if (ail != null) {
			return gson.toJson((Object) ail);
		}
		return "ERROR";
	}
}
