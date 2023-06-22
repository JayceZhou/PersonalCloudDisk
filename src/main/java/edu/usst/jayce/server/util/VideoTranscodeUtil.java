package edu.usst.jayce.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import edu.usst.jayce.server.mapper.NodeMapper;
import edu.usst.jayce.server.model.Node;
import edu.usst.jayce.server.pojo.VideoTranscodeThread;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

// 视频转码工具
@Component
public class VideoTranscodeUtil {

	private EncodingAttributes ea;// 转码设定
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private NodeMapper nm;
	@Resource
	private DFFMPEGLocator kfl;

	public static Map<String, VideoTranscodeThread> videoTranscodeThreads = new HashMap<>();

	{
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(128000);
		audio.setChannels(2);
		audio.setSamplingRate(44100);
		VideoAttributes video = new VideoAttributes();
		video.setCodec("libx264");
		ea = new EncodingAttributes();
		ea.setOutputFormat("MP4");
		ea.setVideoAttributes(video);
		ea.setAudioAttributes(audio);
	}

	// 获取指定视频转码进度
	public String getTranscodeProcess(String fId) throws Exception {
		synchronized (videoTranscodeThreads) {
			VideoTranscodeThread vtt = videoTranscodeThreads.get(fId);
			Node n = nm.queryById(fId);
			File f = fbu.getFileFromBlocks(n);
			if (vtt != null) {
				if ("FIN".equals(vtt.getProgress())) {
					String md5 = DigestUtils.md5Hex(new FileInputStream(f));
					if (md5.equals(vtt.getMd5())
							&& new File(ConfigureReader.instance().getTemporaryfilePath(), vtt.getOutputFileName())
									.isFile()) {
						return vtt.getProgress();
					} else {
						videoTranscodeThreads.remove(fId);
					}
				} else {
					return vtt.getProgress();
				}
			}
			String suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".") + 1).toLowerCase();
			switch (suffix) {
			case "mp4":
			case "webm":
			case "mov":
			case "avi":
			case "wmv":
			case "mkv":
			case "flv":
				break;
			default:
				throw new IllegalArgumentException();
			}
			videoTranscodeThreads.put(fId, new VideoTranscodeThread(f, ea, kfl));
			return "0.0";
		}
	}

}
