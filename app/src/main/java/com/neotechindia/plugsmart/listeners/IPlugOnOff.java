package com.neotechindia.plugsmart.listeners;

import com.neotechindia.plugsmart.model.DeviceBean;

public interface IPlugOnOff {
    public void plugState(boolean b, DeviceBean deviceBean);
}
