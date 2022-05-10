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

public class Client {
    private SocketChannel socketChannel;
    private String ip, id;
    private int port;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private Charset charset = StandardCharsets.UTF_8;

    public Client(String host, int port, String id) {
        this.ip = host;
        this.id = id;
        this.port = port;
    }

    public void connect() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(ip, port));
            while (!socketChannel.finishConnect()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String send(String req) {
        StringBuilder res = new StringBuilder();

        try {
            socketChannel.write(ByteBuffer.wrap((req+"\n").getBytes(StandardCharsets.UTF_8)));
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
                        res.append(charset.decode(byteBuffer));
                        got = true;
                        //done =true;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
