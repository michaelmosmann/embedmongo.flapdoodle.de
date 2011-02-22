package de.flapdoodle.embedmongo.distribution;

public class Distribution {

	private final Version _version;
	private final Platform _platform;
	private final BitSize _bitsize;

	public Distribution(Version version, Platform platform, BitSize bitsize) {
		_version = version;
		_platform = platform;
		_bitsize = bitsize;
	}

	public Version getVersion() {
		return _version;
	}

	public Platform getPlatform() {
		return _platform;
	}

	public BitSize getBitsize() {
		return _bitsize;
	}
	
	@Override
	public String toString() {
		return ""+_version+":"+_platform+":"+_bitsize;
	}
}
