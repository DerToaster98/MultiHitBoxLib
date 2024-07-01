package de.dertoaster.multihitboxlib.example;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.example.entity.Anjanath;
import de.dertoaster.multihitboxlib.example.entity.AnjanathALib;
import de.dertoaster.multihitboxlib.example.init.MHLibExampleEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonListener {
	
	@SubscribeEvent
	public static void initializeAttributes(EntityAttributeCreationEvent event) {
		if (!MHLibMod.shouldRegisterExamples()) {
			return;
		}
		
		event.put(MHLibExampleEntities.ANJANATH.get(), Anjanath.createAttributes().build());
		event.put(MHLibExampleEntities.ANJANATH_AL.get(), AnjanathALib.createAttributes().build());
	}

}
