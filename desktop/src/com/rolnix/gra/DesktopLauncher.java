package com.rolnix.gra;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.Objects;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("gra");
		config.useVsync(false);
		config.setResizable(false);
		config.setWindowIcon("icon.png");
		config.setWindowedMode(1366, 768);
		new Lwjgl3Application(new gra(), config);
	}
}

