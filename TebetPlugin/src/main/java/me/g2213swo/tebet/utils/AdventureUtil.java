package me.g2213swo.tebet.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class AdventureUtil {
    public static void sendActionbarPacket(Player player, Component message) {
        String json = ComponentUtil.asJson(message);
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);

        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(json));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}
