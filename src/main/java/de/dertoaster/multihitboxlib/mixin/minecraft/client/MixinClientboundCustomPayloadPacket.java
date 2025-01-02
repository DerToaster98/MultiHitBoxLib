package de.dertoaster.multihitboxlib.mixin.minecraft.client;

import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientboundCustomPayloadPacket.class)
public class MixinClientboundCustomPayloadPacket {

    @ModifyConstant(method = {"<init>"},
            constant = @Constant(intValue = 1048576))
    private int xlPackets(int old) {
        // Change limit to 2GB, a bit ridiculous but works...
        return 2147483647;
    }

}
