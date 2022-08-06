package fr.hugman.pyrite.util;

import com.mojang.serialization.Codec;

public record PyriteDate(int day, int month, int year) {
	public static final Codec<PyriteDate> CODEC = Codec.STRING.xmap(PyriteDate::fromString, PyriteDate::toString);

	public static PyriteDate fromString(String s) {
		String[] split = s.split("/");
		return new PyriteDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	public String toString() {
		return String.format("%d/%d/%d", day, month, year);
	}
}
