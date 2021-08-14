package me.rhys.client.module.player.disabler.modes;

import me.rhys.base.event.data.EventTarget;
import me.rhys.base.event.impl.network.PacketEvent;
import me.rhys.base.event.impl.player.PlayerMotionEvent;
import me.rhys.base.module.ModuleMode;
import me.rhys.client.module.player.disabler.Disabler;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;

/**
 * Created by Infames
 * Date: 14/08/2021 @ 17:10
 */
public class Ghostly extends ModuleMode<Disabler> {

    public Ghostly(String name, Disabler parent) {
        super(name, parent);
    }

    @EventTarget
    void onMotion(PlayerMotionEvent event) {
        mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(0));
        if (mc.thePlayer.ticksExisted % 3 == 0) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
    }

    @EventTarget
    void onPacket(PacketEvent event) {

        if (event.getPacket() instanceof S00PacketKeepAlive) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0CPacketInput());
        }
        if (event.getPacket() instanceof C0CPacketInput) {
            C0CPacketInput packet = (C0CPacketInput) event.getPacket();
            packet.forwardSpeed = Float.MAX_VALUE;
            packet.strafeSpeed = Float.MAX_VALUE;
            packet.jumping = (mc.thePlayer.ticksExisted % 2 == 0);
            packet.sneaking = (mc.thePlayer.ticksExisted % 2 != 0);
        }
    }

}
