package de.dertoaster.multihitboxlib.util;

import java.util.List;
import java.util.UUID;

import com.mojang.serialization.Codec;

import com.mojang.serialization.DataResult;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec2;

public class UtilityCodecs {

    public static final Codec<Vec2> VEC2_CODEC = Codec.FLOAT.listOf().comapFlatMap((instance) -> {
        return Util.fixedSize(instance, 2).map((p_231081_) -> {
            return new Vec2(p_231081_.get(0), p_231081_.get(1));
        });
    }, (instance) -> {
        return List.of(instance.x, instance.y);
    });

    public static final Codec<UUID> UUID_STRING_CODEC = Codec.STRING.comapFlatMap(
            s ->
            {
                try {
                    return DataResult.success(UUID.fromString(s));
                } catch (Exception e) // fromString throws if it can't convert the string to a UUID
                {
                    return DataResult.error(e::getMessage);
                }
            },
            UUID::toString);

}
