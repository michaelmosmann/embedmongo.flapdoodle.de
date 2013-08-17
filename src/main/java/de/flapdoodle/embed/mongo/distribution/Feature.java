package de.flapdoodle.embed.mongo.distribution;

import java.util.Arrays;
import java.util.EnumSet;

public enum Feature {
	SYNC_DELAY;

	public static EnumSet<Feature> asSet(Feature... features) {
		if (features.length == 0) {
			return EnumSet.noneOf(Feature.class);
		}
		return EnumSet.copyOf(Arrays.asList(features));
	}
}
