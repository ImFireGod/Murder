package fr.imfiregod.task;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class BowCooldown extends BukkitRunnable {

	private int timer;
	private Player player;
	
	public BowCooldown(Player player) {
		this.player = player;
		this.timer = 30;
	}
	
	@Override
	public void run() {

		player.setLevel(((timer - 1) / 10) + 1);
		player.setExp((float) timer / 30.0f);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Rechargement §c" + (float) timer / 10 + "s"));
		
		if(timer == 0) {
			player.getInventory().setItem(9, new ItemStack(Material.ARROW));
			player.setLevel(0);
			cancel();
		}
		
		timer -= 1;
		
	}

}
