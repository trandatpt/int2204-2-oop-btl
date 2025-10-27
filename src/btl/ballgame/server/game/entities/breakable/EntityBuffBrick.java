// package btl.ballgame.server.game.entities.breakable;

// import btl.ballgame.shared.libs.Location;
// import btl.ballgame.server.game.entities.dynamic.EntityBuff;

// public class EntityBuffBrick extends BreakableEntity {

//     public EntityBuffBrick(int id, Location location) {
//         super(id, location);
//     }

//     @Override
//     protected void tick() { }

//     @Override
//     public int getWidth() { return 48; }

//     @Override
//     public int getHeight() { return 18; }

//     @Override
//     public int getMaxHealth() { return 1; }

//     @Override
//     void onObjectBroken() {
//         Location loc = getLocation();
//         EntityBuff buff = new EntityBuff(
//             world.nextEntityId(),
//             new Location(world, loc.getX(), loc.getY(), 0)
//         );
//         world.addEntity(buff);
//         this.remove();
//     }
// }
