package fr.imfiregod.murder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.imfiregod.utils.FastBoard;

public class GameManager {

	private List<UUID> players = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private UUID murderUUID;
	private UUID detectiveUUID;
	private Main plugin;
	
	public GameManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void startGame() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getUniqueId());
		}
		
		if(players.size() < 2) {
			Bukkit.broadcastMessage("§cMurder §8» §7Il faut au moins §c2§7 joueurs pour lancer une partie.");
			return;
		}
		
		plugin.setState(GameState.GAMING);
		Collections.shuffle(players);
		murderUUID = players.get(0);
		detectiveUUID = players.get(1);

		for(UUID playerUUID : players) {
			Player p = Bukkit.getPlayer(playerUUID);
			FastBoard board = plugin.getPlayerBoard(p);
			String roleName;
			if(playerUUID == murderUUID) {
				p.sendTitle("§c§lMeurtrier", "§7Tuez tout le monde", 10, 20, 10);
				roleName = "Meurtrier";
			} else if (playerUUID == detectiveUUID) {
				p.sendTitle("§b§lDétective", "§7Démasquez le tueur et tuez le", 10, 20, 10);
				roleName = "Détective";
			} else {
				p.sendTitle("§7§lInnoncent", "§7Aider le détective à trouver le tueur", 10, 20, 10);
				roleName = "Innocent";
			}
			if(board != null) {
				plugin.updateScoreboard(p);
			}	
		}
	}
	
	public List<UUID> getPlayers() {
		return this.players;
	}
	
	public void addSpectator(Player p) {
		this.spectators.add(p.getUniqueId());
	}
	
	public void eliminate(Player p) {
		if(this.players.contains(p.getUniqueId())) {
			this.players.remove(p.getUniqueId());
			this.spectators.add(p.getUniqueId());
			for(Player player : Bukkit.getOnlinePlayers()) {
				plugin.updateScoreboard(player);
			}
		}
	}
	
	public UUID getDetective() {
		return this.detectiveUUID;
	}
	
	public String getPlayerRoleName(Player p) {
		if(murderUUID == p.getUniqueId()) {
			return "Meurtrier";
		} else if (detectiveUUID != null && detectiveUUID == p.getUniqueId()) {
			return "Détective";
		}
		return this.players.contains(p.getUniqueId()) ? "Innocent" : "Spectateur";
	}
}
