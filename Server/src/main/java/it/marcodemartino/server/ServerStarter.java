package it.marcodemartino.server;

public class ServerStarter {

    public static void main(String[] args) {
        Server server = new SSLServer(8443);
        new Thread(server).start();
    }
}
