package fr.hugman.pyrite.util;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public record KeyableList<K, V>(Map<K, V> map) implements Iterable<V> {
	public static <K, V> Codec<KeyableList<K, V>> codec(Codec<K> keyCodec, Codec<V> valueCodec) {
		return Codec.unboundedMap(keyCodec, valueCodec).xmap(KeyableList::new, KeyableList::map);
	}

	public static <V> Codec<KeyableList<String, V>> codec(Codec<V> valueCodec) {
		return KeyableList.codec(Codec.STRING, valueCodec);
	}

	public static <K, V> KeyableList<K, V> empty() {
		return new KeyableList<>(Map.of());
	}

	@NotNull
	@Override
	public Iterator<V> iterator() {
		return this.map.values().iterator();
	}

	public Stream<V> stream() {
		return this.map.values().stream();
	}

	@Nullable
	public V byKey(K key) {
		return this.map.get(key);
	}

	@Nullable
	public K getKey(V value) {
		for(Map.Entry<K, V> entry : this.map.entrySet()) {
			if(Objects.equals(entry.getValue(), value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "KeyableList[" +
				"map=" + map + ']';
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null) return false;
		if(obj instanceof KeyableList<?, ?> other) {
			return Objects.equals(this.map, other.map);
		}
		return false;
	}
}
