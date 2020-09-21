package com.neotechindia.plugsmart.listeners;

public interface IPacketReceiveCallBack {
    void onPacketReceiveSuccess(String response, int modifiedPosition);

    void onPacketReceiveError(String errorMessage, int modifiedPosition);
}
