package fr.imfiregod.murder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.imfiregod.utils.FastBoard;

public class Main extends JavaPlugin {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private GameState state;
    
    @Override
    public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("§6Murder §e| Chargement du plugin cree par §6TheStorm§e et §6ImFireGod§e.");	
    }
    
    public GameState getState() {
    	return this.state;
    }
    
    public void setState(GameState statut) {
    	this.state = statut;
    }
}
