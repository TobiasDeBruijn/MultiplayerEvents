package nl.thedutchmc.multiplayerevents.events.eventmobkill.listeners;

import org.bukkit.entity.EntityType;
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
	public void onEntityDeathEvent(EntityDamageByEntityEvent event) {
		
		//Check if the entity is dead, or if the killed entity is not the event's entity
		//if so, return
		if(!event.getEntity().isDead()) return;
		if(!event.getEntityType().equals(eventEntityType)) return;
		
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
		eventMobKill.scoreCount.merge(p.getUniqueId(), 1, Integer::sum);
	}
}
