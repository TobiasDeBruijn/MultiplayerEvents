package nl.thedutchmc.multiplayerevents.events.eventmobkill.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import nl.thedutchmc.multiplayerevents.events.eventmobkill.EventMobKill;

public class EntityDamageByEntityEventListener implements Listener {

	private EntityType eventEntityType;
	private EventMobKill eventMobKill;
	
	public EntityDamageByEntityEventListener(EntityType eventEntityType, EventMobKill eventMobKill) {
		this.eventEntityType = eventEntityType;
		this.eventMobKill = eventMobKill;
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		
		//Check if the damaged entity matches the entity this MultiplayerEvent is about
		if(!event.getEntityType().equals(eventEntityType)) return;
		
		//Check if the Entity would die by this event
		//If it isn't a LivingEntity, we return because we do not care.
		if(event.getEntity() instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) event.getEntity();
			double healthAfterEvent = le.getHealth() - event.getFinalDamage();
			if(healthAfterEvent > 0) return;
		} else return;
		
		//Check if the damager is not Player
		if(!(event.getDamager() instanceof Player)) {
						
			//If the damager is not a Player, check if it's a Projectile
			if(event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				
				//Check if the shooter of the Projectile is a player
				if(projectile.getShooter() instanceof Player) {

					//In the end the damager is a Player, so update the stat
					updateStat((Player) projectile.getShooter());
					return;
				}
			}
		}
		
		//Damager is a Player, so update the stat
		updateStat((Player) event.getDamager());
	}
	
	private void updateStat(Player p) {
		p.sendMessage("Score noted");
		
		eventMobKill.scoreCount.merge(p.getUniqueId(), 1, Integer::sum);
	}
}
