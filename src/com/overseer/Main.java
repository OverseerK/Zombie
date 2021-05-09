package com.overseer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.MAX_VALUE;

public class Main extends JavaPlugin implements Listener {

    Random Rand = new Random();

    static Main Plugin;
    Scoreboard Board = Bukkit.getScoreboardManager().getMainScoreboard();
    Team Zombie;
    Team Human;
    ItemStack Antibiotic = new ItemStack(Material.HONEY_BOTTLE);

    @Override
    public void onEnable() {
        Plugin = this;
        System.out.println("[Zombie] Enabled.");
        Bukkit.getPluginManager().registerEvents(this, this);
        if (Board.getTeam("Zombie") == null) {
            Zombie = Board.registerNewTeam("Zombie");
            Zombie.setColor(ChatColor.RED);
        } else {
            Zombie = Board.getTeam("Zombie");
        }
        if (Board.getTeam("Human") == null) {
            Human = Board.registerNewTeam("Human");
        } else {
            Human = Board.getTeam("Human");
            Human.setColor(ChatColor.AQUA);
        }
        Antibiotic.getItemMeta().setDisplayName("§b항생제");
        ShapedRecipe AntibioticRecipe = new ShapedRecipe(new NamespacedKey(this, "antibiotic"), Antibiotic);
        AntibioticRecipe.shape("GGG", "GHG", "GGG");
        AntibioticRecipe.setIngredient('G', Material.GOLD_INGOT);
        AntibioticRecipe.setIngredient('H', Material.HONEY_BOTTLE);
        Bukkit.addRecipe(AntibioticRecipe);
    }

    @Override
    public void onDisable() {
        System.out.println("[Zombie] Disabled.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().addItem(Antibiotic);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Player o = p;
        if (args.length != 0 && Bukkit.getServer().getPlayer(args[0]) != null) {
            o = Bukkit.getServer().getPlayer(args[0]);
        }
        if (cmd.getName().equalsIgnoreCase("zombie")) {
            if (sender instanceof Player) {
                if (Zombie.hasPlayer(o)) {
                    p.sendMessage("§c대상이 이미 좀비입니다.");
                } else {
                    Zombie.addPlayer(o);
                    o.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10000000, 0, false, false));
                    o.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 0, false, false));
                    o.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000000, 0, false, false));
                    Bukkit.broadcastMessage("§c" + p.getName() + "(이)가 좀비가 되었습니다!");
                    return true;
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("human")) {
            if (sender instanceof Player) {
                if (Human.hasPlayer(o)) {
                    p.sendMessage("§c대상이 이미 인간입니다.");
                } else {
                    Human.addPlayer(o);
                    for (PotionEffect effect : o.getActivePotionEffects())
                        o.removePotionEffect(effect.getType());
                    Bukkit.broadcastMessage("§b" + p.getName() + "(이)가 인간이 되었습니다!");
                }
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("zremove")) {
            if (sender instanceof Player) {
                if (Human.hasPlayer(o)) {
                    Human.removePlayer(o);
                }
                if (Zombie.hasPlayer(o)) {
                    Zombie.removePlayer(o);
                }
                for (PotionEffect effect : o.getActivePotionEffects())
                    o.removePotionEffect(effect.getType());
                Bukkit.broadcastMessage(p.getName() + "(은)는 이제 아무것도 아닙니다.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("zlist")) {
            sender.sendMessage("좀비 수: " + Zombie.getSize());
            sender.sendMessage("생존자 수: " + Human.getSize());
            return true;
        }
        return false;
    }

    @EventHandler // 좀비 행동 제한
    public void onZombieInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            Action a = e.getAction();
            if (e.getClickedBlock() != null) {
                Material cb = e.getClickedBlock().getType();
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    if (cb == Material.CRAFTING_TABLE || cb == Material.ANVIL || cb == Material.FURNACE
                            || cb == Material.ENCHANTING_TABLE) {
                        e.setCancelled(true);
                        p.sendMessage("§c당신은 지성이 없습니다.");
                    }
                }
                Material i = p.getInventory().getItemInMainHand().getType();
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    if (i == Material.FLINT_AND_STEEL || i == Material.FIREWORK_ROCKET
                            || i == Material.FIRE_CHARGE || i == Material.ENDER_EYE) {
                        e.setCancelled(true);
                        p.sendMessage("§c당신은 지성이 없습니다.");
                    }
                } else if (a == Action.RIGHT_CLICK_AIR) {
                    if (i == Material.ENDER_EYE) {
                        e.setCancelled(true);
                        p.sendMessage("§c당신은 지성이 없습니다.");
                    }
                }
            }
        }
    }

    int InfectTask;

    @EventHandler // 좀비 감염
    public void onZombieInfect(EntityDamageByEntityEvent e) {
        Entity Victim = e.getEntity();
        Entity Damager = e.getDamager();
        if (Victim instanceof Player && Damager instanceof Player) {
            if (Zombie.hasPlayer((Player) Damager) && Human.hasPlayer((Player) Victim)) {
                Player p = (Player) Victim;
                if (Rand.nextInt(10) == 0) {
                    Victim.sendMessage("§c물린 상처가 엄청나게 깊습니다...");
                    InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage("§c" + p.getName() + "(이)가 좀비가 되었습니다!");
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, MAX_VALUE, 0, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, MAX_VALUE, 0, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, MAX_VALUE, 0, false, false));
                            p.setGlowing(true);
                        }
                    }, 600L);
                }
            }
        }
    }

    @EventHandler
    public void onHoneyUse(PlayerItemConsumeEvent e) {
        if (e.getItem() == Antibiotic) {
            if (Human.hasPlayer(e.getPlayer())) {
                Bukkit.getScheduler().cancelTask(InfectTask);
                e.getPlayer().sendMessage("§b당신은 몸이 정화되는 것을 느꼈습니다.");
            }
        }
    }

    @EventHandler // 좀비 리스폰
    public void onZombieRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                @Override
                public void run() {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10000000, 0, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 0, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000000, 0, false, false));
                }
            }, 1L);
        }
    }

    public Player getRandomPlayer(Team team) { // 팀 안의 랜덤 플레이어 찾기
        ArrayList<Player> PlayerList = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (team.hasPlayer(p)) {
                PlayerList.add(p);
            }
        }
        Player o = null;
        if (PlayerList.size() != 0) {
            o = PlayerList.get(Rand.nextInt(team.getSize()));
        }
        return o;
    }

    @EventHandler // 좀비의 다이아몬드 사용
    public void onZombieTeleport(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack i = p.getInventory().getItemInMainHand();
        if (a == Action.RIGHT_CLICK_AIR && i.getType() == Material.DIAMOND) {
            if (Zombie.hasPlayer(p)) {
                if (p.getCooldown(Material.DIAMOND) != 0) {
                    p.sendMessage((p.getCooldown(Material.DIAMOND) / 20 + 1) + "초 남음");
                } else {
                    Player o = getRandomPlayer(Zombie);
                    if (o == null) {
                        p.sendMessage("주변에 좀비가 없습니다!");
                    } else {
                        o.sendMessage("§d" + p.getName() + "이(가) 당신을 소환했습니다!");
                        o.teleport(p);
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND));
                        p.setCooldown(Material.DIAMOND, 2000);
                    }
                }
            }
        }
    }

    @EventHandler // 좀비의 바다의 심장 사용
    public void onZombieTrack(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack i = p.getInventory().getItemInMainHand();
        if (a == Action.RIGHT_CLICK_AIR && i.getType() == Material.HEART_OF_THE_SEA) {
            if (Zombie.hasPlayer(p)) {
                Player o = getRandomPlayer(Human);
                if (p.getCooldown(Material.HEART_OF_THE_SEA) != 0) {
                    p.sendMessage((p.getCooldown(Material.HEART_OF_THE_SEA) / 20) + "초 남음");
                } else if (o == null) {
                    p.sendMessage("주변에 인간이 없습니다!");
                } else {
                    int oX = o.getLocation().getBlockX();
                    int oY = o.getLocation().getBlockY();
                    int oZ = o.getLocation().getBlockZ();
                    o.sendMessage("당신은 추적당하는 기분을 느낍니다.");
                    p.sendMessage("§d당신은 이공간에서 " + o.getName() + "에 대한 지식을 꺼내왔습니다!");
                    p.sendMessage(o.getName() + "의 위치 - X: " + oX + " Y: " + oY + " Z: " + oZ);
                    p.setCooldown(Material.HEART_OF_THE_SEA, 2000);
                }
            }
        }
    }
}
