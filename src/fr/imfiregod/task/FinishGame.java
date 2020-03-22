package fr.imfiregod.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.imfiregod.murder.GameState;
import fr.imfiregod.murder.Main;

public class FinishGame extends BukkitRunnable {
	
	private Main plugin;
	private int timer;
	
	public FinishGame(Main plugin) {
		this.plugin = plugin;
		this.timer = 20;
	}

	@Override
	public void run() {
		if(timer == 0) {
			plugin.setState(GameState.WAITING);
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.teleport(plugin.getMainSpawn().getLocation());
				player.setGameMode(GameMode.ADVENTURE);
				player.setExp(0);
				player.setLevel(0);
				plugin.updateScoreboard(player);
			}
			new GameWaiting(plugin).runTaskTimer(plugin, 0, 20);
		}
		timer--;
	}
	
}
