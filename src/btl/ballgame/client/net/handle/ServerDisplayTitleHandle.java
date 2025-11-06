package btl.ballgame.client.net.handle;

import btl.ballgame.client.TextureAtlas;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.TextParticle;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutTitle;
import btl.ballgame.shared.libs.Constants.ParticlePriority;

public class ServerDisplayTitleHandle implements PacketHandler<PacketPlayOutTitle, CServerConnection> {
	@Override
	public void handle(PacketPlayOutTitle packet, CServerConnection context) {
		CSWorld world = context.client.getActiveWorld();
		if (world == null) {
			return; // the server is sending bullshit again
		}
		var particles = world.particles().get(ParticlePriority.LATEST_IGNORE_FLIP);
		if (particles != null) particles.forEach(p -> {
			if (p instanceof TextParticle tp && tp.getyOffset() == packet.getYOffset()) { 
				tp.remove();
			}
		});
		world.particles().spawn(ParticlePriority.LATEST_IGNORE_FLIP, new TextParticle(
			packet.getMessage(), 
			TextureAtlas.fromRgbInt(packet.getColor()),
			packet.getSize(), packet.getYOffset(), 
			packet.isBold(), packet.isItalic(), packet.isUnderline(),
			packet.getFadeIn(), packet.getPersistent(), packet.getFadeOut()
		));
	}
}
