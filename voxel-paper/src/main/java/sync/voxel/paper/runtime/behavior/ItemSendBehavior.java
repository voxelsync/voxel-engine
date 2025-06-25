package sync.voxel.paper.runtime.behavior;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import org.jetbrains.annotations.NotNull;

public class ItemSendBehavior implements PacketListener {

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        User user = event.getUser();
        if (event.getPacketType() != PacketType.Play.Client.CHAT_MESSAGE) return;
        WrapperPlayClientChatMessage chatMessage = new WrapperPlayClientChatMessage(event);
        String message = chatMessage.getMessage();
        if (message.equalsIgnoreCase("ping")) {
            user.sendMessage("pong");
        }
    }

}
