package net.river.hostilesinpeaceful;

// Minecraft & Fabric API Imports
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

// SLF4J Logger Imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Accessor Imports (your mixins)
import net.river.hostilesinpeaceful.mixin.MobEntityAccessor;
import net.river.hostilesinpeaceful.mixin.GoalSelectorAccessor;
import net.river.hostilesinpeaceful.mixin.ActiveTargetGoalAccessor;

import java.util.Set; // <-- IMPORT ADDED

public class HostilesInPeaceful implements ModInitializer {
	public static final String MOD_ID = "hostilesinpeaceful";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("HostilesInPeaceful initializing! Making hostiles passive in Peaceful.");
		registerEntityLoadListener();
	}

	private void registerEntityLoadListener() {
		ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerWorld world) -> {
			// Check if the entity is a hostile mob AND if the world is actually in peaceful difficulty
			// (Although we modify goals anyway, checking difficulty might prevent unnecessary work if the mod is used in non-peaceful)
			if (entity instanceof HostileEntity hostileEntity && world.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
				try {
					// Access the mob's target selector AI controller
					GoalSelector targetSelector = ((MobEntityAccessor) hostileEntity).getTargetSelector();

					// Check if the target selector exists (it should, but safe practice)
					if (targetSelector != null) {
						// Access the set of goals within the target selector
						// CORRECTED: Use Set instead of List
						Set<PrioritizedGoal> goals = ((GoalSelectorAccessor) targetSelector).getGoals();

						// Remove goals from the set if they meet the criteria
						goals.removeIf(prioritizedGoal -> {
							// Check if the goal is an ActiveTargetGoal (the kind used for targeting specific entity types)
							if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> activeTargetGoal) {
								// Access the class type this goal targets
								Class<?> targetClass = ((ActiveTargetGoalAccessor) activeTargetGoal).getTargetClass();
								// Check if the target class is PlayerEntity
								return PlayerEntity.class.isAssignableFrom(targetClass); // Use isAssignableFrom for robustness
							}
							// If it's not an ActiveTargetGoal, don't remove it
							return false;
						});
						// Optional Log:
						// LOGGER.info("Modified target goals for {} ({})", hostileEntity.getType().getName().getString(), hostileEntity.getUuidAsString());
					}
				} catch (Exception e) {
					// Log any errors during goal modification to help debug
					LOGGER.error("Failed to modify goals for entity {}: {}", entity.getType().getName().getString(), e.getMessage(), e);
				}
			}
		});
	}
}