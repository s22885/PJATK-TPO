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
import java.util.Map;
import java.util.Set;

public class Server extends Thread {
    private String host;
    private int port;
    private StringBuilder serverLog = new StringBuilder();
    private Map<String, ClientLog> clientsLog = new HashMap<>();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private Charset charset = StandardCharsets.UTF_8;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;

    }

    String getServerLog() {
        return serverLog.toString();
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
                        continue;
                    }
                    if (key.isReadable()) {
                        SocketChannel cc = (SocketChannel) key.channel();

                        serviceRequest(cc);
                        continue;
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return;

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = stringBuffer.toString();


        String resMess = "";
        String[] dataAr = data.split(" +");
        if (dataAr.length > 0) {
            switch (dataAr[0]) {
                case "login":
                    if(dataAr.length>1){
                        try {
                            ClientLog log=new ClientLog(dataAr[1]);
                            clientsLog.put(sc.getRemoteAddress().toString(),log);
                            resMess="logged in";
                            serverLog.append(dataAr[1]).append(" logged in at ")
                                    .append(LocalDateTime.now().toLocalTime())
                                    .append("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "bye":
                    if (dataAr.length == 1) {
                        resMess = "logged out";
                        try {
                            clientsLog.get(sc.getRemoteAddress().toString()).addLog("logged out")
                                    .addLog("=== "+clientsLog.get(sc.getRemoteAddress().toString())
                                            .getName()+" log end ===");
                            serverLog.append(clientsLog.get(sc.getRemoteAddress().toString())
                                    .getName()).append(" logged out at ").append(LocalDateTime.now().toLocalTime())
                                    .append("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (data.equals("bye and log transfer")) {
                            try {
                                clientsLog.get(sc.getRemoteAddress().toString()).addLog("logged out")
                                        .addLog("=== "+clientsLog.get(sc.getRemoteAddress().toString())
                                                .getName()+" log end ===");
                                resMess=clientsLog.get(sc.getRemoteAddress().toString()).getLogs();
                                serverLog.append(clientsLog.get(sc.getRemoteAddress().toString())
                                        .getName()).append(" logged out at ").append(LocalDateTime.now().toLocalTime())
                                        .append("\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    if(dataAr.length>1){
                        try {
                            String tmpRes = Time.passed(dataAr[0],dataAr[1]);
                            clientsLog.get(sc.getRemoteAddress().toString()).addLog("Request: "+data);
                            clientsLog.get(sc.getRemoteAddress().toString()).addLog("Result:\n"+tmpRes);
                            serverLog.append(clientsLog.get(sc.getRemoteAddress().toString())
                                    .getName()).append(" request at ").append(LocalDateTime.now().toLocalTime())
                                    .append(" \"").append(data).append("\"\n");
                            resMess=tmpRes;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


            }
        }
        try {
            sc.write(ByteBuffer.wrap(resMess.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void stopServer() {
        this.interrupt();
    }
}
