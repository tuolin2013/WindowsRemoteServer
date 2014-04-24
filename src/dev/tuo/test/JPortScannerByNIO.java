package dev.tuo.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class JPortScannerByNIO {
	public static void main(String[] args) throws IOException {
		int port = 8848;
		int retry = 10;// 重试次数
		String ip = InetAddress.getLocalHost().getHostAddress();
		String ipHead = ip.substring(0, ip.lastIndexOf('.') + 1);
		Selector selector = Selector.open();

		for (int tail = 0; tail < 255; tail++) {
			SocketChannel channel = SocketChannel.open();

			SocketAddress address = new InetSocketAddress(ipHead + tail, port);

			channel.configureBlocking(false);
			channel.connect(address);
			channel.register(selector, SelectionKey.OP_CONNECT, address);// 这里你也可以用输入或者输出
		}
		while (retry-- > 0) {
			// selector.select(1000 * 5);这里可以设置超时时间
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();

			for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
				SelectionKey key = it.next();
				it.remove();

				if (key.isConnectable()) {
					System.err.println(key.attachment());
					key.cancel();
				}
			}
		}
	}
}