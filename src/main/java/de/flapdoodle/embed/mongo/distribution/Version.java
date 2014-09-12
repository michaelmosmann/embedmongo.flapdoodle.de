/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.mongo.distribution;

import java.util.EnumSet;

/**
 * MongoDB Version enum
 */
public enum Version implements IFeatureAwareVersion {

	@Deprecated
	V1_6_5("1.6.5"),
	@Deprecated
	V1_7_6("1.7.6"),
	@Deprecated
	V1_8_0_rc0("1.8.0-rc0"),
	@Deprecated
	V1_8_0("1.8.0"),
	@Deprecated
	V1_8_1("1.8.1"),
	@Deprecated
	V1_8_2_rc0("1.8.2-rc0"),
	@Deprecated
	V1_8_2("1.8.2"),
	@Deprecated
	V1_8_4("1.8.4"),
	@Deprecated
	V1_8_5("1.8.5"),

	@Deprecated
	V1_9_0("1.9.0"),
	@Deprecated
	V2_0_1("2.0.1"),
	@Deprecated
	V2_0_4("2.0.4"),
	@Deprecated
	V2_0_5("2.0.5"),
	@Deprecated
	V2_0_6("2.0.6"),
	@Deprecated
	V2_0_7_RC1("2.0.7-rc1"),
	@Deprecated
	V2_0_7("2.0.7"),
	@Deprecated
	V2_0_8_RC0("2.0.8-rc0"),
	@Deprecated
	V2_0_9("2.0.9"),

	@Deprecated
	V2_1_0("2.1.0"),
	@Deprecated
	V2_1_1("2.1.1"),
  @Deprecated
	V2_1_2("2.1.2"),

	@Deprecated
	V2_2_0_RC0("2.2.0-rc0"),
	@Deprecated
	V2_2_0("2.2.0"),
	@Deprecated
	V2_2_1("2.2.1"),
  @Deprecated
  V2_2_3("2.2.3"),
  @Deprecated
	V2_2_4("2.2.4"),
	@Deprecated
	V2_2_5("2.2.5"),
	@Deprecated
  V2_2_6("2.2.6"),
  V2_2_7("2.2.7"),

	@Deprecated
	V2_3_0("2.3.0"),

	@Deprecated
	V2_4_0_RC3("2.4.0-rc3"),
	@Deprecated
	V2_4_0("2.4.0",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
  V2_4_1("2.4.1",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
 	V2_4_2("2.4.2",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
	V2_4_3("2.4.3",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
  V2_4_5("2.4.5",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
	V2_4_6("2.4.6",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
	V2_4_7("2.4.7",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
	@Deprecated
	V2_4_8("2.4.8",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
	V2_4_9("2.4.9",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  V2_4_10("2.4.10",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),

  @Deprecated
  V2_5_0("2.5.0",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
  V2_5_1("2.5.1",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
  V2_5_3("2.5.3",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),
  @Deprecated
  V2_5_4("2.5.4",Feature.SYNC_DELAY, Feature.TEXT_SEARCH),

  /**
	 * 2.6 series production releases --------------
	 */
  @Deprecated
  V2_6_0("2.6.0",Feature.SYNC_DELAY),

  /**
   * Latest 2.6 production release
   */
  V2_6_1("2.6.1",Feature.SYNC_DELAY),

  /**
   * Latest 2.7 series development release
   */
  @Deprecated
  V2_7_0("2.7.0",Feature.SYNC_DELAY),

  /**
   * Latest 2.7 series development release
   */
  V2_7_1("2.7.1",Feature.SYNC_DELAY),

  ;

	private final String specificVersion;
	private EnumSet<Feature> features;

	Version(String vName,Feature...features) {
		this.specificVersion = vName;
		this.features = Feature.asSet(features);
	}

	@Override
	public String asInDownloadPath() {
		return specificVersion;
	}

	@Override
		public boolean enabled(Feature feature) {
			return features.contains(feature);
		}

	@Override
	public String toString() {
		return "Version{" + specificVersion + '}';
	}

	public static enum Main implements IFeatureAwareVersion {
		@Deprecated
		V1_8(V1_8_5),

		@Deprecated
		V2_0(V2_0_9),
		@Deprecated
		V2_1(V2_1_2),
		V2_2(V2_2_7),
		@Deprecated
		V2_3(V2_3_0),
		V2_4(V2_4_10),
    @Deprecated
		V2_5(V2_5_4),
		/**
		 * Latest production release
		 */
		V2_6(V2_6_1),

    /**
     * Latest development release
     */
    V2_7(V2_7_1),

		PRODUCTION(V2_6),
		DEVELOPMENT(V2_7), ;

		private final IFeatureAwareVersion _latest;

		Main(IFeatureAwareVersion latest) {
			_latest = latest;
		}

		@Override
		public String asInDownloadPath() {
			return _latest.asInDownloadPath();
		}

		@Override
		public boolean enabled(Feature feature) {
			return _latest.enabled(feature);
		}
	}
}
