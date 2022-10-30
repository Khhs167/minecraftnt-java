package net.minecraftnt.client;

import net.minecraftnt.server.Server;

public class Client implements Runnable{

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }

    @Override
    public void run() {
        Server server = new Server();
        server.run();
    }
}
