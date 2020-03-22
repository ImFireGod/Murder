package fr.imfiregod.murder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.imfiregod.task.FinishGame;
import fr.imfiregod.task.GameStarting;
import fr.imfiregod.task.GiveItems;
import fr.imfiregod.utils.FastBoard;
import fr.imfiregod.utils.Spawn;

public class GameManager {

	private List<UUID> players = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private boolean murderDead = false;
	private boolean detectiveDead = false;
	private UUID murderUUID;
	private UUID detectiveUUID;
	private Main plugin;
	
	public GameManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void startGame() {
		
		List<Spawn> spawns = plugin.getSpawns(); 
		int i = 0;
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getUniqueId());
			if(spawns.size() - 1 < i) {
				i = 0;
			}
			player.teleport(spawns.get(i).getLocation());
			i++;
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
			if(playerUUID == murderUUID) {
				p.sendTitle("§c§lMeurtrier", "§7Tuez tout le monde", 10, 20, 10);
				p.sendMessage("§cMurder §8» §7Vous êtes §cMeurtrier§7.");
			} else if (playerUUID == detectiveUUID) {
				p.sendTitle("§b§lDétective", "§7Démasquez le tueur et tuez le", 10, 20, 10);
				p.sendMessage("§cMurder §8» §7Vous êtes §cDétective§7.");
			} else {
				p.sendTitle("§7§lInnoncent", "§7Aider le détective à trouver le tueur", 10, 20, 10);
				p.sendMessage("§cMurder §8» §7Vous êtes §cInnocent§7.");
			}
			if(board != null) {
				plugin.updateScoreboard(p);
			}	
		}

		new GiveItems(plugin, Bukkit.getPlayer(murderUUID), Bukkit.getPlayer(detectiveUUID)).runTaskTimer(plugin, 0, 20);
		
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
			if(p.getUniqueId() == this.murderUUID) {
				this.murderDead = true;
			} else if (p.getUniqueId() == this.detectiveUUID) {
				Location loc = p.getLocation();
				if(p.getInventory().getItem(1) != null) {
					p.getLocation().getWorld().dropItem(p.getLocation(), p.getInventory().getItem(1));
					p.getInventory().clear();
				} else {
					p.getLocation().getWorld().dropItem(p.getLocation(), this.getBow());
				}			
				Bukkit.broadcastMessage("§cMurder §8» §7Le §cDétective§7 a perdu son arc. X: §c" + (int) loc.getX() + "§7 Y: §c" + (int) loc.getY() + " §7Z: §c" + (int) loc.getZ());
				this.detectiveDead = true;
			}
			if(p.isOnline()) {
				p.setGameMode(GameMode.SPECTATOR);
			}
			for(Player player : Bukkit.getOnlinePlayers()) {
				plugin.updateScoreboard(player);
			}
			this.checkWin();
		}
	}
	
	public UUID getDetective() {
		return this.detectiveUUID;
	}
	
    public UUID getMurder() {
    	return this.murderUUID;
    }

	public ItemStack getBow() {
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§7Cooldown: §c3s");
		bowMeta.setUnbreakable(true);
		bowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		bowMeta.setDisplayName("§7Arc du §cDétective");
		bowMeta.setLore(lore);
		bow.setItemMeta(bowMeta);
		return bow;
	}
	
	public ItemStack getSword() {
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemMeta swordMeta = sword.getItemMeta();
		swordMeta.setUnbreakable(true);
		swordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attackSpeed", 50, Operation.ADD_NUMBER));
		swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		swordMeta.setDisplayName("§7Couteau du §cMeurtrier");
		sword.setItemMeta(swordMeta);
		return sword;
	}
    
	public void checkWin() {
		if(this.murderIsDead()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.getUniqueId() != this.murderUUID) {
					player.sendTitle("§a§lVous avez gagné", "§7Le §cMeurtrier§7 a été tué.", 10, 20, 10);
				} else {
					player.sendTitle("§c§lVous avez perdu", "§7Les innocents ont gagnés.", 10, 20, 10);
				}
			}
			this.finishGame("§cMurder §8» §7Le §cMeutrier§7 est mort, les Innocents ont gagnés !");
		} else {
			if(this.players.size() == 1 && this.players.get(0) == this.murderUUID) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.getUniqueId() == this.murderUUID) {
						player.sendTitle("§a§lVous avez gagné", "§7Tout les §cInnocents§7 sont morts.", 10, 20, 10);
					} else {
						player.sendTitle("§c§lVous avez perdu", "§7Le §cMeurtrier§7 vous a tué.", 10, 20, 10);
					}
				}
			}
			this.finishGame("§cMurder §8» §7Les §cInnocents§7 sont morts, le §cMeurtrier§7 a gagné.");
		}
	}
	
	public Boolean detectiveIsDead() {
		return this.detectiveDead;
	}
	
	public Boolean murderIsDead() {
		return this.murderDead;
	}
	
	private void finishGame(String text) {
		Bukkit.broadcastMessage(text);
		plugin.setState(GameState.ENDING);
		plugin.setGame(null);
		new FinishGame(plugin).runTaskTimer(plugin, 0, 20);
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
