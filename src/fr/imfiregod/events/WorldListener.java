package fr.imfiregod.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class WorldListener implements Listener {

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
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
}
