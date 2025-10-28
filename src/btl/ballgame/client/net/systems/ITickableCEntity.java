package btl.ballgame.client.net.systems;

/**
 * Usually, client entities need no fucking tick loop like server side entities, 
 * but some may need it for things like prediction, animation.
 * 
 * This runs at the same speed as the server, 30TPS
 */
public interface ITickableCEntity {
	void onTick();
}
