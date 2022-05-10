/**
 * @author Klik Konrad S22885
 */

package zad1;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {
    private Client client;
    private List<String> reqs;
    private boolean showSendRes;
    private StringBuilder logs = new StringBuilder();
    private volatile boolean logReady = false;

    private ClientTask(Client client, List<String> reqs, boolean showSendRes) {
        this.reqs = reqs;
        this.client = client;
        this.showSendRes = showSendRes;
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask(c, reqs, showSendRes);
    }

    @Override
    public void run() {
        reqs.add("bye and log transfer");
        client.connect();
        client.send("login " + client.getId() + "\n\n\n");

        for (String req : reqs) {
            String res=client.send(req);
            if(showSendRes){
                System.out.println(res);
            }
            logs.append(res).append("\n");
        }

        logReady = true;
    }

    public String get() throws InterruptedException, ExecutionException {
        while(!logReady){

        }
        if(showSendRes){
            return logs.toString();
        }
        return "";
    }
}
