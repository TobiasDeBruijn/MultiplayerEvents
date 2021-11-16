package dev.array21.multiplayerevents.config;

import java.util.Arrays;
import java.util.List;

public class ConfigManifest {
	int eventIntervalLowerBound, eventIntervalUpperBound;
	boolean debugMod;
	String language;
	
	private EventMobKill eventMobKill;
	private EventMoveToLocation eventMoveToLocation;

	public EventMoveToLocation getEventMoveToLocation() {
		return this.eventMoveToLocation;
	}

	public EventMobKill getEventMobKill() {
		return this.eventMobKill;
	}

	public class EventMobKill {
		private boolean enabled, hostileOnly;
		private String[] mobBlacklist;
		private int countLowerBound, countUpperBound, durationLowerBound, durationUpperBound, rewardLowerBound, rewardUpperBound;
		private float rewardMultiplier;

		public boolean isEnabled() {
			return this.enabled;
		}

		public boolean isHostileOnly() {
			return this.hostileOnly;
		}

		public List<String> getMobBlacklist() {
			return Arrays.asList(this.mobBlacklist);
		}

		public int getCountLowerBound() {
			return this.countLowerBound;
		}

		public int getCountUpperBound() {
			return this.countUpperBound;
		}

		public int getDurationLowerBound() {
			return this.durationLowerBound;
		}

		public int getDurationUpperBound() {
			return this.durationUpperBound;
		}

		public int getRewardLowerBound() {
			return this.rewardLowerBound;
		}

		public int getRewardUpperBound() {
			return this.rewardUpperBound;
		}

		public float getRewardMultiplier() {
			return this.rewardMultiplier;
		}
	}

	public class EventMoveToLocation {
		private boolean mobsEnabled, finishParticles;
		private int distanceLowerBound, distanceUpperBound, finishRadius, durationLowerBound, durationUpperBound, rewardsLowerBound, rewardsUpperBound;
		private double secondPlaceModifier, thirdPlacModifier, noPodiumModifier;
		private String[] mobWhitelist;

		public boolean isMobsEnabled() {
			return mobsEnabled;
		}

		public boolean isFinishParticles() {
			return finishParticles;
		}

		public int getDistanceLowerBound() {
			return distanceLowerBound;
		}

		public int getDistanceUpperBound() {
			return distanceUpperBound;
		}

		public int getFinishRadius() {
			return finishRadius;
		}

		public int getDurationLowerBound() {
			return durationLowerBound;
		}

		public int getDurationUpperBound() {
			return durationUpperBound;
		}

		public int getRewardsLowerBound() {
			return rewardsLowerBound;
		}

		public int getRewardsUpperBound() {
			return rewardsUpperBound;
		}

		public double getSecondPlaceModifier() {
			return secondPlaceModifier;
		}

		public double getThirdPlacModifier() {
			return thirdPlacModifier;
		}

		public double getNoPodiumModifier() {
			return noPodiumModifier;
		}

		public List<String> getMobWhitelist() {
			return Arrays.asList(mobWhitelist);
		}
	}

	public class EventItemCommission {
		private int countLowerBound, countUpperBound, durationLowerBound, durationUpperBound, rewardLowerBound, rewardUpperBound, collectionChestX, collectionChestY, collectionChestZ;
		String worldName;
		String[] itemWhitelist;

		public int getCountLowerBound() {
			return countLowerBound;
		}

		public int getCountUpperBound() {
			return countUpperBound;
		}

		public int getDurationLowerBound() {
			return durationLowerBound;
		}

		public int getDurationUpperBound() {
			return durationUpperBound;
		}

		public int getRewardLowerBound() {
			return rewardLowerBound;
		}

		public int getRewardUpperBound() {
			return rewardUpperBound;
		}

		public int getCollectionChestX() {
			return collectionChestX;
		}

		public int getCollectionChestY() {
			return collectionChestY;
		}

		public int getCollectionChestZ() {
			return collectionChestZ;
		}

		public String getWorldName() {
			return worldName;
		}

		public String[] getItemWhitelist() {
			return itemWhitelist;
		}
	}
}
