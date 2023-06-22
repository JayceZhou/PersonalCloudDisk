package edu.usst.jayce.server.util;

import java.util.*;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQueries;

// 时间相关处理工具
public class ServerTimeUtil {
	public static String accurateToSecond() {
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
		return dtfDateTimeFormatter.format(ldt);
	}

	public static String accurateToMinute() {
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
		return dtfDateTimeFormatter.format(ldt);
	}

	public static String accurateToDay() {
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
		return dtfDateTimeFormatter.format(ldt);
	}

	public static String accurateToLogName() {
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
		return dtfDateTimeFormatter.format(ldt);
	}

	public static Date getServerTime() {
		return new Date();
	}

	// 从文件块生成“最后修改时间”标签
	public static String getLastModifiedFormBlock(File block) {
		ZonedDateTime longToTime;
		if (block != null && block.exists()) {
			longToTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(block.lastModified()), ZoneId.of("GMT"));
		} else {
			longToTime = ZonedDateTime.now(ZoneId.of("GMT"));
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
				.withZone(ZoneId.of("GMT"));
		return longToTime.format(dtf);
	}

	// 将日期字符串（精确到日）转化为时间值
	public static long getTimeFromDateAccurateToDay(String date) {
		try {
			DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
			LocalDate ld = dtfDateTimeFormatter.parse(date, TemporalQueries.localDate());
			Instant instant = ld.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
			return instant.toEpochMilli();
		} catch (DateTimeParseException e) {
			return 0L;
		}
	}
}
