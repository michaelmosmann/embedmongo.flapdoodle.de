package de.flapdoodle.embed.mongo.distribution;

import java.util.EnumSet;

import de.flapdoodle.embed.process.distribution.IVersion;


public class Versions {
	private Versions() {
		// no instance
	}
	
	public static IFeatureAwareVersion withFeatures(IVersion version, Feature...features) {
		return new GenericFeatureAwareVersion(version, features);
	}
	
	static class GenericFeatureAwareVersion implements IFeatureAwareVersion {

		private final IVersion _version;
		private final EnumSet<Feature> _features;

		public GenericFeatureAwareVersion(IVersion version, Feature...features) {
			_version = version;
			_features = Feature.asSet(features);
		}
		
		@Override
		public String asInDownloadPath() {
			return _version.asInDownloadPath();
		}

		@Override
		public boolean enabled(Feature feature) {
			return _features.contains(feature);
		}
		
	}
}
