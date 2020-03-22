package fr.imfiregod.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.imfiregod.murder.GameState;
import fr.imfiregod.murder.Main;

public class GameWaiting extends BukkitRunnable {

	private Main main;
	private int timer = 40;
	
	public GameWaiting(Main main) {
		this.main = main;
	}
	
	@Override
	public void run() {
		if(Bukkit.getOnlinePlayers().size() > 0 &&  timer == 0) {
			Bukkit.broadcastMessage("§cMurder §8» §7Il faut au moins §c2§7 joueurs pour lancer une partie.");
			timer = 30;
		} else {
			if(Bukkit.getOnlinePlayers().size() >= 2) {
				new GameStarting(main).runTaskTimer(main, 0, 20);
				main.setState(GameState.STARTING);
				cancel();
			}
		}
		timer--;
	}

}
