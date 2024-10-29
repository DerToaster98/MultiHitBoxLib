package de.dertoaster.multihitboxlib.api;

import com.mojang.serialization.Codec;
import de.dertoaster.multihitboxlib.util.LazyLoadField;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatapackRegistry<T> {
		
	protected final ResourceKey<Registry<T>> registryKey;
	protected final Codec<T> objectCodec;
	protected final Codec<Holder<T>> registryCodec;
	
	protected final LazyLoadField<Codec<T>> lazyLoadByNameCodec = new LazyLoadField<>(this::createByNameCodec);
		
	public DatapackRegistry(final ResourceLocation id, final Codec<T> codec) {
		this(ResourceKey.createRegistryKey(id), codec);
	}
	
	public DatapackRegistry(final ResourceKey<Registry<T>> resourceKey, final Codec<T> codec) {
		this(resourceKey, codec, RegistryFileCodec.create(resourceKey, codec));
	}
	
	public DatapackRegistry(final ResourceKey<Registry<T>> resourceKey, final Codec<T> codec, final Codec<Holder<T>> registryCodec) {
		super();
		this.registryCodec = registryCodec;
		this.objectCodec = codec;
		this.registryKey = resourceKey;
	}

	public void registerSynchable() {
		register(true);
	}
	
	public void register() {
		register(false);
	}
	
	public void register(boolean synchable) {
		if (synchable) {
			DynamicRegistries.registerSynced(registryKey, objectCodec, objectCodec);
		} else {
			DynamicRegistries.register(registryKey, objectCodec);
		}
	}
	
	@Nullable
	public T get(ResourceLocation id, RegistryAccess registryAccess) {
		Optional<Registry<T>> optRegistry = this.registry(registryAccess);
		if (optRegistry.isEmpty()) {
			return null;
		}
		return optRegistry.get().get(id);
	}
	
	public Optional<Registry<T>> registry(RegistryAccess registryAccess) {
		return registryAccess.registry(this.registryKey());
	}
	
	public List<T> values(RegistryAccess registryAccess) {
		Optional<Registry<T>> optRegistry = this.registry(registryAccess);
		if (optRegistry.isEmpty()) {
			return Collections.emptyList();
		}
		return optRegistry.get().stream().collect(Collectors.toList());
	}
	
	public ResourceKey<Registry<T>> registryKey() {
		return this.registryKey;
	}
	
	public Codec<T> objectCodec() {
		return this.objectCodec;
	}
	
	public Codec<T> byNameCodec() {
		return this.lazyLoadByNameCodec.get();
	}
	
	public Codec<Holder<T>> registryCodec() {
		return this.registryCodec;
	}
	
	protected Codec<T> createByNameCodec() {
		return createByNameCodec(this);
	}
	
	protected static <V> Codec<V> createByNameCodec(DatapackRegistry<V> registry) {
		return registry.registryCodec().xmap(Holder::value, Holder::direct);
	}
	
}
