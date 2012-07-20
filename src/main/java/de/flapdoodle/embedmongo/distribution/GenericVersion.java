package de.flapdoodle.embedmongo.distribution;

/**
 * Generic version implementation for currently unsupported mongodb versions by embedmongo.
 */
public class GenericVersion implements MongoDBVersion {
	private String versionName;
	private String specificVersion;

	/**
	 * C'tor with version name = specific version
	 * @param vName
	 */
	public GenericVersion(String vName) {
		init(vName, vName);
	}

	/**
	 * C'tor for version and specific version, e.g. vName=2.1 sName=2.1.1
	 * @param vName
	 * @param sName
	 */
	public GenericVersion(String vName, String sName) {
		init(vName, sName);
	}

	private void init(String vName, String sName) {
		versionName = vName;
		specificVersion = sName;
	}

	@Override
	public String getVersionName() {
		return versionName;
	}

	@Override
	public String getSpecificVersion() {
		return specificVersion;
	}

	@Override
	public String toString() {
		return "GenericVersion{" +
				"versionName='" + versionName + '\'' +
				", specificVersion='" + specificVersion + '\'' +
				'}';
	}
}
