/**
 * @author Klik Konrad S22885
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private static Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private SocketChannel socketChannel;
    private String ip, id;
    private int port;
    private StringBuilder personalLog = new StringBuilder();

    public ChatClient(String host, int port, String id) {
        this.ip = host;
        this.id = id;
        this.port = port;
    }

    public void send(String req) {
        try {

            socketChannel.write(ByteBuffer.wrap((req + "\n").getBytes(StandardCharsets.UTF_8)));
            boolean done = false;
            boolean got = false;
            while (!done) {

                byteBuffer.clear();
                switch (socketChannel.read(byteBuffer)) {
                    case -1:
                        done = true;
                        break;
                    case 0:
                        if (got) done = true;
                        break;
                    default:
                        byteBuffer.flip();
                        personalLog.append(charset.decode(byteBuffer));
                        got = true;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        try {
            send("logout "+id);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketChannel.close();
        } catch (IOException ignored) {

        }
    }
    public void connect(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(ip, port));
            while (!socketChannel.finishConnect()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        connect();
        send("login "+id);
    }

    public String getChatView() {
        return "=== "+id+" chat view\n"+personalLog.toString();
    }
}
