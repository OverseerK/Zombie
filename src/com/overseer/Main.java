package com.overseer;

import org.bukkit.*;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
    ItemStack Vaccine = new ItemStack(Material.POTION);

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
        ItemMeta Meta = Antibiotic.getItemMeta();
        Meta.setDisplayName("§b항생제");
        Antibiotic.setItemMeta(Meta);
        PotionMeta PotionMeta = (PotionMeta) Vaccine.getItemMeta();
        PotionMeta.setColor(Color.AQUA);
        PotionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        PotionMeta.setDisplayName("§b백신");
        Vaccine.setItemMeta(PotionMeta);
        ShapedRecipe AntibioticRecipe = new ShapedRecipe(new NamespacedKey(this, "antibiotic"), Antibiotic);
        AntibioticRecipe.shape("GGG", "GHG", "GGG");
        AntibioticRecipe.setIngredient('G', Material.GOLD_INGOT);
        AntibioticRecipe.setIngredient('H', Material.HONEY_BOTTLE);
        Bukkit.addRecipe(AntibioticRecipe);
        ShapedRecipe VaccineRecipe = new ShapedRecipe(new NamespacedKey(this, "vaccine"), Vaccine);
        VaccineRecipe.shape(" G ", " A ", "CBM");
        VaccineRecipe.setIngredient('G', Material.GHAST_TEAR);
        VaccineRecipe.setIngredient('A', Antibiotic);
        VaccineRecipe.setIngredient('B', Material.BLAZE_POWDER);
        VaccineRecipe.setIngredient('C', Material.GOLDEN_CARROT);
        VaccineRecipe.setIngredient('M', Material.GLISTERING_MELON_SLICE);
        Bukkit.addRecipe(VaccineRecipe);
    }

    @Override
    public void onDisable() {
        System.out.println("[Zombie] Disabled.");
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
                    zEffect(p);
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

    public void zEffect(Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
            @Override
            public void run() {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, MAX_VALUE, 0, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, MAX_VALUE, 0, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, MAX_VALUE, 0, false, false));
            }
        }, 1L);
    }

    @EventHandler // 좀비 행동 제한
    public void onZombieInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            Action a = e.getAction();
            if (e.getClickedBlock() != null) {
                Material cb = e.getClickedBlock().getType();
                Material i = p.getInventory().getItemInMainHand().getType();
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    if (cb == Material.CRAFTING_TABLE || cb == Material.ANVIL || cb == Material.FURNACE || cb == Material.ENCHANTING_TABLE) {
                        e.setCancelled(true);
                        p.sendMessage("§c당신은 지성이 없습니다.");
                    }
                }
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    if (i == Material.FLINT_AND_STEEL || i == Material.FIREWORK_ROCKET || i == Material.FIRE_CHARGE || i == Material.ENDER_EYE) {
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
                if (!Bukkit.getScheduler().isCurrentlyRunning(InfectTask)) {
                    if (Rand.nextInt(10) == 0) {
                        Victim.sendMessage("§c물린 상처가 엄청나게 깊습니다...");
                        Victim.sendMessage("§c빨리 항생제를 찾지 못하면 5분 후에는 사람이 아니게 될 것입니다.");
                        InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.broadcastMessage("§c" + p.getName() + "(이)가 좀비가 되었습니다!");
                                Zombie.addPlayer(p);
                                zEffect(p);
                            }
                        }, 600L);
                    }
                }
            }
        }
    }

    @EventHandler //아이템 사용
    public void onItemUse(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (e.getItem().getType() == Material.HONEY_BOTTLE) {
            if (Human.hasPlayer(p) && Bukkit.getScheduler().isCurrentlyRunning(InfectTask)) {
                Bukkit.getScheduler().cancelTask(InfectTask);
                p.sendMessage("§b당신은 몸이 정화되는 것을 느꼈습니다.");
            }
        } else if (e.getItem().getType() == Material.POTION) {
            if (Zombie.hasPlayer(p)) {
                Human.addPlayer(p);
                for (PotionEffect effect : p.getActivePotionEffects())
                    p.removePotionEffect(effect.getType());
                p.sendMessage("§b당신은 몸에 생기가 도는 것을 느꼈습니다!");
                Bukkit.broadcastMessage("§b" + p.getName() + "(이)가 인간이 되었습니다!");
            } else {
                e.setCancelled(true);
                p.sendMessage("이것을 낭비할 수 없다는 강한 욕구가 느껴집니다.");
            }
        }
    }

    @EventHandler // 좀비 리스폰
    public void onZombieRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            zEffect(p);
        }
    }

    public Player getRandomPlayer(Team team, Player p) { // 팀 안의 랜덤 플레이어 찾기
        ArrayList<Player> PlayerList = new ArrayList<>();
        for (Player Player : Bukkit.getOnlinePlayers()) {
            if (team.hasPlayer(Player) && Player != p) {
                PlayerList.add(Player);
            }
        }
        Player o;
        if (PlayerList.size() == 0) {
            o = null;
        } else {
            o = PlayerList.get(Rand.nextInt(team.getSize()));
        }
        return o;
    }

    @EventHandler // 좀비의 아이템 사용
    public void onZombieUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack i = p.getInventory().getItemInMainHand();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            if (Zombie.hasPlayer(p)) {
                if (i.getType() == Material.DIAMOND) {
                    Player o = getRandomPlayer(Zombie, p);
                    if (p.getCooldown(Material.DIAMOND) != 0) {
                        p.sendMessage((p.getCooldown(Material.DIAMOND) / 20 + 1) + "초 남음");
                    } else if (o == null) {
                        p.sendMessage("주변에 좀비가 없습니다!");
                    } else {
                        o.sendMessage("§d" + p.getName() + "이(가) 당신을 소환했습니다!");
                        o.teleport(p);
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND));
                        p.setCooldown(Material.DIAMOND, 2000);
                    }
                } else if (i.getType() == Material.HEART_OF_THE_SEA) {
                    Player o = getRandomPlayer(Human, p);
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
}
