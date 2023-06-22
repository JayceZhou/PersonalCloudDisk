package edu.usst.jayce.server.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpSession;

// 带限流作用的缓存输出流
public class VariableSpeedBufferedOutputStream extends BufferedOutputStream {

	private long maxRate;// 该实例的最大输出限速，以B/s为单位。
	private HttpSession session;// 该实例所用的用户会话对象，用于线程锁。
	private long writtenLength;// 一秒之内已经写出的数据长度，以B为单位
	private long startTime;// 计时起始时间，以毫秒为单位

	// 创建一个限速缓存输出流实例
	public VariableSpeedBufferedOutputStream(OutputStream out, long maxRate, HttpSession session) {
		super(out);
		this.maxRate = maxRate;
		this.session = session;
		this.writtenLength = 0;
		this.startTime = System.currentTimeMillis();
	}

	// 升级的write(byte[] b, int off, int len)方法
	public void write(byte[] b, int off, int len) throws IOException {
		if (maxRate > 0) {
			// 如果限速值为正数，那么进行限速输出，输出时要求独占Session对象以确保和其它下载任务共享最大限速。
			int startIndex = off;// 记录当前应读数组的起始位置
			int surplusLength = len;// 记录数组中应写的剩余数据量
			while (surplusLength > 0) {
				synchronized (session) {
					// 如果数组内仍有数据存留，则执行写出操作
					// 如果尚未开始计量一秒内的写出量，则记录写出前的毫秒值
					if (writtenLength == 0) {
						startTime = System.currentTimeMillis();
					}
					// 计算此秒之内最多还能写出多少数据
					long shouldWriteLength = maxRate - writtenLength;
					if (surplusLength > shouldWriteLength) {
						// 如果数组内数据的剩余量大于此秒允许写出量，则写出允许写出量的数据
						super.write(b, startIndex, (int) shouldWriteLength);
						startIndex += shouldWriteLength;
						writtenLength += shouldWriteLength;
						surplusLength -= shouldWriteLength;
					} else {
						// 如果数组内数据的剩余量小于等于此秒的应写量，则将剩余量的数据全部写出
						super.write(b, startIndex, surplusLength);
						startIndex += surplusLength;
						writtenLength += surplusLength;
						surplusLength -= surplusLength;
					}
					if (writtenLength >= maxRate) {
						// 如果已写出量达到了每秒允许的最大写出量，则计算实际耗时
						long consumeTime = System.currentTimeMillis() - startTime;
						if (consumeTime < 1000) {
							// 如果实际耗时小于1秒，那么睡够一秒
							try {
								Thread.sleep(1000 - consumeTime);
							} catch (InterruptedException e) {
								// 如果收到中断指令，那么就响应中断
								Thread.currentThread().interrupt();
							}
						}
						// 写出量计数归0，以便下次写出时重新计量
						writtenLength = 0;
					}
				}
			}
		} else if (maxRate < 0) {// 如果限速值为负数，则不限速输出
			super.write(b, off, len);
		} else {
			// 如果限速值为0，那肯定是限速设置有误造成的。
			throw new IllegalArgumentException("Error:invalid maximum download rate value.");
		}
	}
}
