package fr.hugman.pyrite.game;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class PlayerData {
	public Vec3d lastTickPos;

	public static PlayerData create(ServerPlayerEntity player) {
		var data = new PlayerData();
		data.lastTickPos = player.getPos();
		return data;
	}
}
