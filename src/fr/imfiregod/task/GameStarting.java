package fr.imfiregod.task;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.imfiregod.murder.GameManager;
import fr.imfiregod.murder.GameState;
import fr.imfiregod.murder.Main;
import fr.imfiregod.utils.FastBoard;

public class GameStarting extends BukkitRunnable {

	private Main main;
	private int timer;
	
	public GameStarting(Main main) {
		this.main = main;
		this.timer = 15;
		this.changeBoard();
	}
	
	@Override
	public void run() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		
		if(players.size() < 2) {
			for(Player player : players) {
				player.setLevel(0);
				main.updateScoreboard(player);
			}
			new GameWaiting(main).runTaskTimer(main, 0, 20);
			main.setState(GameState.WAITING);
			cancel();
		} else {
			if(timer == -1) {
				GameManager game = new GameManager(this.main);
				this.main.setGame(game);
				game.startGame();
				cancel();
			} else {
				for(Player player : players) {
					FastBoard board = main.getPlayerBoard(player);
					if(board != null) {
						board.updateLine(7, "§7Début dans §c" + timer + "s");
					}
					player.setLevel(timer);
				}
			}
		}
		
		timer--;
	
	}
	
	private void changeBoard() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			FastBoard board = main.getPlayerBoard(player);
			if(board != null) {
				board.updateLine(4, "§7Status: §cLancement");
				board.updateLine(7, "§7Début dans §c15s");
				board.updateLine(8, "");
				board.updateLine(9, "§egamers-france.ga");
			}
		}
	}
}
