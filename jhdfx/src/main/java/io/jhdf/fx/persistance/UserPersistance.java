package io.jhdf.fx.persistance;

import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserPersistance {

	private static final String userHome = System.getProperty("user.home");

	private static final String userPersistanceDir = Paths.get(userHome, ".jhdfx").toString();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final String recentFilesName = "recents";
	private static RecentFiles recentFiles;
	private static final Path recentsPath = Paths.get(userPersistanceDir, recentFilesName);

	static {
		// Check if the .jhdfx directory is there if not create it
		if (!Paths.get(userPersistanceDir).toFile().exists()) {
			Paths.get(userPersistanceDir).toFile().mkdir();
		}

		if (recentsPath.toFile().exists()) {
			try {
				String json = Files.readString(recentsPath);
				recentFiles = GSON.fromJson(json, RecentFiles.class);
			} catch (IOException e) {
				e.printStackTrace();
				recentFiles = new RecentFiles();
			}
		} else {
			recentFiles = new RecentFiles();
		}
	}

	public static RecentFiles getRecentFiles() {
		return recentFiles;
	}

	public static RecentFiles persistFileOpen(File file) {
		recentFiles.persistFileOpen(file);
		String json = GSON.toJson(recentFiles);
		try {
			Files.writeString(recentsPath, json, CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return recentFiles;
	}

}
