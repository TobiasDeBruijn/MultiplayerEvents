package nl.thedutchmc.multiplayerevents.events.eventmobkill.listeners;

import java.util.HashMap;
import java.util.UUID;

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
		if(!event.getEntity().isDead()) return;
		if(!event.getEntityType().equals(eventEntityType)) return;
		
		if(!(event.getDamager() instanceof Player)) {
			if(event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				if(projectile.getShooter() instanceof Player) {
					updateStat((Player) projectile.getShooter());
					return;
				}
			}
		}
		
		updateStat((Player) event.getDamager());
	}
	
	private void updateStat(Player p) {
		eventMobKill.scoreCount.merge(p.getUniqueId(), 1, Integer::sum);
	}
}
