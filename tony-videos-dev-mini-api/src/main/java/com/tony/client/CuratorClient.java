package com.tony.client;

public interface CuratorClient {

    public void init();

    public void addChildWatch(String nodePath) throws Exception;
}
