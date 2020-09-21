package com.neotechindia.plugsmart.listeners;

public interface AquasmartDialogListener extends BaseUIListener {
	public void onButtonOneClick();
	public void onButtonTwoClick();
	abstract public void onButtonThreeClick(String password);
}
