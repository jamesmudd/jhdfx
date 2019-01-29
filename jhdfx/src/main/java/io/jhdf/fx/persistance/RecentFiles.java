package io.jhdf.fx.persistance;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RecentFiles implements Iterable<String> {

	private LinkedList<String> recentFiles = new LinkedList<String>();

	public List<String> getRecentFiles() {
		return recentFiles;
	}

	public void setRecentFiles(List<String> recentFiles) {
		this.recentFiles = new LinkedList<>(recentFiles);
	}

	@Override
	public Iterator<String> iterator() {
		return recentFiles.iterator();
	}

	public boolean isEmpty() {
		return recentFiles.isEmpty();
	}

	public void persistFileOpen(File file) {
		String path = file.getPath();
		if (!recentFiles.contains(path)) {
			recentFiles.add(path);

			// Keep list size down
			if (recentFiles.size() > 10) {
				recentFiles.removeFirst();
			}
		}
	}
}
