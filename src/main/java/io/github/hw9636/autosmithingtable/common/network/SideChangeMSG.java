package io.github.hw9636.autosmithingtable.common.network;

import io.github.hw9636.autosmithingtable.common.AutoSmithingContainer;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SideChangeMSG {

    public final int containerId;
    public final int newSidesConfig;

    public SideChangeMSG(int containerId, int newSidesConfig) {
        this.containerId = containerId;
        this.newSidesConfig = newSidesConfig;
    }

    public static void handle(SideChangeMSG msg, Supplier<NetworkEvent.Context> ctx) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.containerMenu instanceof AutoSmithingContainer container && container.containerId == msg.containerId) {
            container.data.set(4, msg.newSidesConfig);
        }
        ctx.get().setPacketHandled(true);
    }

    public void serialize(FriendlyByteBuf buffer) {
        buffer.writeInt(this.containerId);
        buffer.writeInt(this.newSidesConfig);
    }

    public static SideChangeMSG deserialize(FriendlyByteBuf buffer) {
        return new SideChangeMSG(buffer.readInt(), buffer.readInt());
    }
}
