package com.neotechindia.plugsmart.excutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {
	private static BackgroundExecutor _bgExecutor;
	private static ExecutorService _exService;
	
	private BackgroundExecutor() {}
	
	public static BackgroundExecutor getInstance() {
		if(_bgExecutor == null) {
			_bgExecutor = new BackgroundExecutor();
		}
		if(_exService == null) {
			_exService = Executors.newFixedThreadPool(16);
		}
		
		return _bgExecutor;
	}
	
	public void execute(Runnable runnable) {
		if(_exService == null) {
			_exService = Executors.newFixedThreadPool(16);
		}
		
		_exService.submit(runnable);
	}

	public void stop() {
		if (_exService!=null) {
			_exService.shutdownNow();
			_exService=null;
		}
	}
}
