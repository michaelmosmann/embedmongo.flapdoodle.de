package de.flapdoodle.embedmongo.output;

public interface IProgressListener {

	void progress(String label, int percent);
}
