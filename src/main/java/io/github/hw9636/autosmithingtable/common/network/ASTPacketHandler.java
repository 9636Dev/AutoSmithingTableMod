package io.github.hw9636.autosmithingtable.common.network;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ASTPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AutoSmithingTableMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
}
