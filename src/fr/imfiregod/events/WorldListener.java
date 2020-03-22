package fr.imfiregod.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import fr.imfiregod.murder.Main;

public class WorldListener implements Listener {

	private Main plugin;
	
	public WorldListener(Main main) {
		this.plugin = main;
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if(event.getEntity().getType() == EntityType.DROPPED_ITEM) {
			Item item = (Item) event.getEntity();
			if(item.getItemStack().getType() == Material.BOW) {
				item.setCustomName("§7Arc du §cDétective");
				item.setCustomNameVisible(true);
			}
		}
	}
}
