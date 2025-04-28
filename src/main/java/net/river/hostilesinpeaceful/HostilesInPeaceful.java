package net.river.hostilesinpeaceful;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostilesInPeaceful implements ModInitializer {
	public static final String MOD_ID = "hostilesinpeaceful";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// We only need to log that the mod is active.
		// The mixins handle the actual game modification.
		LOGGER.info("HostilesInPeaceful initializing! Mixins will handle spawning and behavior changes.");
		// Goal modification logic is now handled within MobEntityMixin's initGoals injection.
		// Despawn prevention is handled by MobEntityMixin's isDisallowedInPeaceful injection.
		// Spawning is handled by NaturalSpawnerMixin.
	}

	// No other methods needed in this file for now.
}