package com.neotechindia.plugsmart.listeners;

public interface ISocketConnection {
    void onSocketConnect(int position);

    void onSocketDisconnect(int position);
}
