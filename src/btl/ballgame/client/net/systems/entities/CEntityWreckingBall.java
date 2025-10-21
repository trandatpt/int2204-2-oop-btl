package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;

public class CEntityWreckingBall extends CSInterpolatedEntity {
	@Override
	public void render() {
		this.computeLerps();
	}
}
