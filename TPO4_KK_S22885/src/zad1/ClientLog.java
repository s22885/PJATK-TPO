package zad1;

public class ClientLog {
    private String name;
    private StringBuilder logs=new StringBuilder();;
    public ClientLog(String name){
        this.name=name;
        logs.append("=== ")
                .append(name)
                .append(" log start ===\nlogged in\n");
    }

    public String getName() {
        return name;
    }

    public String getLogs() {
        return logs.toString();
    }
    public ClientLog addLog(String log){
        logs.append(log).append("\n");
        return this;
    }
}
