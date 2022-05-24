/**
 * @author Klik Konrad S22885
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ChatServer extends Thread {
    private static Charset charset = StandardCharsets.UTF_8;

    private String host;
    private int port;
    private StringBuilder logs = new StringBuilder();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private HashMap<String, SocketChannel> channels = new HashMap<>();

    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getServerLog() {
        return logs.toString();
    }

    public void stopServer() {
        this.interrupt();
    }

    public void startServer() {
        this.start();

    }

    @Override
    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


//            Selector tmpSel = Selector.open();
//            SelectionKey tmpSK = serverSocketChannel.register(selector, SelectionKey.OP_WRITE);

            while (true) {
                if (this.isInterrupted()) break;
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();

                    if (key.isAcceptable()) {
                        SocketChannel cc = serverSocketChannel.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                        channels.put(cc.toString(), cc);
                        continue;
                    }
                    if (key.isReadable()) {
                        SocketChannel cc = (SocketChannel) key.channel();

                        String data = serviceRequest(cc);
                        if (!data .equals("")) {
                            channels.forEach((s, sc) -> {
                                if (sc.isOpen()) {
                                    try {
                                        if(sc.isConnected())
                                        sc.write(ByteBuffer.wrap((data).getBytes(StandardCharsets.UTF_8)));
                                    } catch (IOException ignored) {
                                    }
                                }
                            });
                        }


//                        Set<SelectionKey> tmpKeys = tmpSel.selectedKeys();
//                        Iterator<SelectionKey> tmpIter = tmpKeys.iterator();
//                        while (tmpIter.hasNext()) {
//                            if (this.isInterrupted()) break;
//                            SocketChannel cctmp = (SocketChannel) tmpIter.next().channel();
//                            String data = serviceRequest(cc);
//                            if (data != null) {
//                                cctmp.write(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
//                            }
//                        }

                        continue;
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return null;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.setLength(0);

        byteBuffer.clear();

        try {
            readLoop:
            while (true) {
                int n = sc.read(byteBuffer);

                if (n > 0) {
                    byteBuffer.flip();
                    CharBuffer cbuf = charset.decode(byteBuffer);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        stringBuffer.append(c);
                    }
                }
                else{
                    break readLoop;
                }
            }

        } catch (IOException e) {
            //
        }
        String data = stringBuffer.toString();
        if(!data.equals("")) {
            logs.append(LocalDateTime.now().toLocalTime());
            if (data.startsWith("login")) {
                String[] tmp = data.split(" +");
                if (tmp.length >= 2) {
                    logs.append(" ").append(tmp[1]).append(" logged in");
                    data = tmp[tmp.length - 1] + " logged in\n";
                }


            } else if (data.startsWith("logout")) {
                String[] tmp = data.split(" +");
                if (tmp.length >= 2) {
                    logs.append(" ").append(tmp[1]).append(" logged out");
                    data = tmp[tmp.length - 1] + " logged out\n";
                }

            } else {
                String[] tmp = data.split(" +");
                logs.append(" ").append(tmp[tmp.length - 1]).append(": ").append(data);
                data = tmp[tmp.length - 1] + ": " + data + "\n";
            }
            logs.append("\n");
        }
        return data;
    }
}
