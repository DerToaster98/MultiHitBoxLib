package de.dertoaster.multihitboxlib.api.event.server;

import de.dertoaster.multihitboxlib.assetsynch.assetfinders.AbstractAssetFinder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface SynchAssetFinderRegistrationEvent {

    Event<SynchAssetFinderRegistrationEvent> EVENT = EventFactory.createArrayBacked(
            SynchAssetFinderRegistrationEvent.class,
            (listeners) -> (map) -> {
                for (SynchAssetFinderRegistrationEvent listener : listeners) {
                    listener.register(map);
                }
            }
    );

    void register(Map<ResourceLocation, AbstractAssetFinder> map);
}
