package de.dertoaster.multihitboxlib.example;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.example.entity.Anjanath;
import de.dertoaster.multihitboxlib.example.init.MHLibExampleEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.MOD)
public class CommonListener {
	
	@SubscribeEvent
	public static void initializeAttributes(EntityAttributeCreationEvent event) {
		if (!MHLibMod.shouldRegisterExamples()) {
			return;
		}
		
		event.put(MHLibExampleEntities.ANJANATH.get(), Anjanath.createAttributes().build());
	}

}
