package de.dertoaster.multihitboxlib.util;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class UtilityCodecs {

    public static final Codec<Vec2> VEC2_CODEC = Codec.FLOAT.listOf().comapFlatMap((instance) -> {
        return Util.fixedSize(instance, 2).map((p_231081_) -> {
            return new Vec2(p_231081_.get(0), p_231081_.get(1));
        });
    }, (instance) -> {
        return List.of(instance.x, instance.y);
    });

}