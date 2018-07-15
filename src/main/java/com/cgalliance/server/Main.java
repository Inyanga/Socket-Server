package com.cgalliance.server;

public class Main {

    public static void main(String[] args) {
        Config config = Config.getInstance();

        NetworkAdapter networkAdapter = new NetworkAdapter(config.getPort());
        networkAdapter.startServer();
    }
}


