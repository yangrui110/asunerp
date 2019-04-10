package com.hanlin.fadp.petrescence.datasource;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		String cmd="cmd.exe /C start \"\" \""+System.getProperty("user.dir")+"\\stop.bat\"";
//		String cmd="cmd.exe /C start \"\" \"a:/hello.bat\"";
		Process ps = Runtime.getRuntime().exec(cmd);
	}
}
