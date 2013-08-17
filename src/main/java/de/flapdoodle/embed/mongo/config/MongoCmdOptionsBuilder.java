package de.flapdoodle.embed.mongo.config;

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.TypedProperty;


public class MongoCmdOptionsBuilder extends AbstractBuilder<IMongoCmdOptions> {

	protected static final TypedProperty<Integer> SYNC_DELAY = TypedProperty.with("syncDelay", Integer.class);

	
	public MongoCmdOptionsBuilder() {
		property(SYNC_DELAY).setDefault(0);
	}
	
	public MongoCmdOptionsBuilder syncDeplay(int deplay) {
		set(SYNC_DELAY, deplay);
		return this;
	}

	public MongoCmdOptionsBuilder defaultSyncDeplay() {
		set(SYNC_DELAY, null);
		return this;
	}
	
	@Override
	public IMongoCmdOptions build() {
		Integer syncDelay=get(SYNC_DELAY, null);
		return new MongoCmdOptions(syncDelay);
	}

	static class MongoCmdOptions implements IMongoCmdOptions {

		private final Integer _syncDelay;

		
		public MongoCmdOptions(Integer syncDelay) {
			_syncDelay = syncDelay;
		}
		
		@Override
		public Integer syncDelay() {
			return _syncDelay;
		}
		
	}
}
