package de.flapdoodle.embedmongo.distribution;

/**
 * Interface for mongodb versions
 */
public interface MongoDBVersion {

	String getVersionName();
	String getSpecificVersion();
}
