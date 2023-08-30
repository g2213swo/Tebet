package me.g2213swo.tebet.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatUserImpl;
import me.g2213swo.tebet.chat.handler.TebetMessageHandler;
import me.g2213swo.tebet.utils.AdventureUtil;
import me.g2213swo.tebet.utils.ComponentUtil;
import me.g2213swo.tebetapi.model.ChatUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener extends TebetMessageHandler implements Listener {

    private final Set<UUID> listenedPlayers = ConcurrentHashMap.newKeySet();

    private final TebetPlugin plugin;

    private int waitPlayerTaskId;

    public ChatListener(TebetPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        run();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (listenedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            ChatUser chatUser = ChatUserImpl.Factory.getInstance()
                    .getChatUserWithNick(player.getUniqueId(), player.getName());
            chatUser.setMessage(PlainTextComponentSerializer.plainText().serialize(event.message()));
            handleGPTMessage(chatUser, player::sendMessage);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeListenedPlayer(event.getPlayer());
    }

    public void addListenedPlayer(Player player) {
        listenedPlayers.add(player.getUniqueId());
    }

    public void removeListenedPlayer(Player player) {
        listenedPlayers.remove(player.getUniqueId());
    }

    public boolean isListenedPlayer(Player player) {
        return listenedPlayers.contains(player.getUniqueId());
    }

    private void run(){
        waitPlayerTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            getChatWaitingUsers().forEach(uuid -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    List<String> waitingMessages = plugin.getConfig().getStringList("waiting_messages");
                    List<Component> component = ComponentUtil.asComponent(waitingMessages);
                    AdventureUtil.sendActionbarPacket(player, component.get(random.nextInt(component.size())));
                }
            });
        }, 0, 20L).getTaskId();
    }

    public Set<UUID> getChatWaitingUsers() {
        return debouncer.getUsers();
    }

    public int getWaitPlayerTaskId() {
        return waitPlayerTaskId;
    }
}
