package com.neotechindia.plugsmart.listeners;

import com.neotechindia.plugsmart.model.JsonDataFieldsModel;

public interface IScheduleEnableDisableListener {
    public void scheduleState(boolean state, JsonDataFieldsModel jsonDataFieldsModel);
}
