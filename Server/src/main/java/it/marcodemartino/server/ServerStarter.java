package it.marcodemartino.server;

public class ServerStarter {

    public static void main(String[] args) {
        Server server = new SSLServer(8443, args[0]);
        new Thread(server).start();
    }
}
