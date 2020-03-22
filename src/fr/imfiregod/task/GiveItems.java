package fr.imfiregod.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.imfiregod.murder.GameManager;
import fr.imfiregod.murder.Main;

public class GiveItems extends BukkitRunnable {

	private Main plugin;
	private int timer;
	private Player murder;
	private Player detective;

	public GiveItems(Main plugin, Player murder, Player detective) {
		this.timer = 10;
		this.murder = murder;
		this.detective = detective;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		
		if(plugin.gameIsStarted()) {
			if(timer == 10) {
				Bukkit.broadcastMessage("§cMurder §8» §7Les objets seront distribués dans §c" + timer + "s§7.");
			}
			
			if(timer == 3 || timer == 2 || timer == 1) {
				Bukkit.broadcastMessage("§cMurder §8» §7Distribution des objets dans §c" + timer + "s§7.");
			}
			
			if(timer == 0) {
				if(murder.isOnline() && detective.isOnline()) {
					if(plugin.gameIsStarted()) {
						GameManager game = plugin.getGame();
						detective.getInventory().setItem(1, game.getBow());
						detective.getInventory().setItem(9, new ItemStack(Material.ARROW, 1));
						murder.getInventory().setItem(1, game.getSword());
						Bukkit.broadcastMessage("§cMurder §8» §7Le §cMeurtrier§7 a reçu son couteau§7 et le §cDétective§7 son arc.");
					}
				}
			}
		}
		
		timer--;
		
	}

}
