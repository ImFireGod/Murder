package fr.imfiregod.events;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.imfiregod.murder.GameState;
import fr.imfiregod.murder.Main;
import fr.imfiregod.utils.FastBoard;

public class PlayerListener implements Listener {

	private Main plugin;
	
	public PlayerListener(Main main) {
		this.plugin = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		event.setJoinMessage(null);
		 
		plugin.createScoreboard(p, new FastBoard(p));
		
		if(plugin.getState() == GameState.WAITING) {
			Bukkit.broadcastMessage("�cMurder �8� �c" + p.getDisplayName() + "�7 a rejoint la partie (�c" + Bukkit.getOnlinePlayers().size() + "�7/�c16�7)");
			this.updateOnlinePlayers();
		} else {
			if(plugin.gameIsStarted()) {
				
			}
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		 
        FastBoard board = plugin.getBoards().remove(p.getUniqueId());
		this.updateOnlinePlayers();

        if(board != null) {
            board.delete();
        }
	}
	
	private void updateOnlinePlayers() {
		if(plugin.getState() == GameState.WAITING || plugin.getState() == GameState.STARTING) {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			for(Player player : players) {
				FastBoard board = plugin.getPlayerBoard(player);
				if(board != null) {
					board.updateLine(5, "�7Joueurs: �c" + players.size() + "/16");
				}
			}
		}
	}
	
}
