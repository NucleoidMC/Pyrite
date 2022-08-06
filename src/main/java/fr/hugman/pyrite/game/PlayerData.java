package fr.hugman.pyrite.game;

import net.minecraft.util.math.Vec3d;

public record PlayerData(Vec3d lastTickPos) {
	public static PlayerData create() {
		return new PlayerData(null);
	}
}
