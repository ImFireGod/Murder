package fr.imfiregod.murder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameManager {

	private List<UUID> players = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private UUID murderUUID;
	private UUID detective;
	
	public void startGame() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getUniqueId());
		}
		
		if(players.size() < 2) {
			Bukkit.broadcastMessage("§cMurder §8» §7Il faut au moins §c2§7 joueurs pour lancer une partie.");
			return;
		}
		
		Collections.shuffle(players);
		murderUUID = players.get(0);
		detective = players.get(1);

	}
}
