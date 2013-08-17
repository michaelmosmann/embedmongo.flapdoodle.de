package de.flapdoodle.embed.mongo.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;


public interface IFeatureAwareVersion extends IVersion {
	boolean enabled(Feature feature);
}
