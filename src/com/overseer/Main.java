package com.overseer;

import org.bukkit.*;
import org.bukkit.entity.Zombie;
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

    Main Plugin;
    Scoreboard Board;
    Team Zombie;
    Team Human;
    ItemStack Antibiotic = new ItemStack(Material.HONEY_BOTTLE);
    ItemStack Vaccine = new ItemStack(Material.POTION);

    @Override
    public void onEnable() {
        Plugin = this;
        System.out.println("[Zombie] Enabled.");
        Bukkit.getPluginManager().registerEvents(this, this);
        Board = Bukkit.getScoreboardManager().getMainScoreboard();
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
        PotionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
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
        VaccineRecipe.setIngredient('A', Material.GLASS_BOTTLE);
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
        if (cmd.getName().equalsIgnoreCase("zombie")) {
            if (args.length != 0) {
                if (args[0].length() != 0 && Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (!Zombie.hasPlayer(p)) {
                        Bukkit.broadcastMessage("§c" + p.getName() + "이(가) 좀비가 되었습니다!");
                        Human.removePlayer(p);
                        Zombie.addPlayer(p);
                        zEffect(p);
                    } else {
                        sender.sendMessage("§c" + p.getName() + "은(는) 이미 좀비입니다.");
                    }
                } else {
                    sender.sendMessage("§c올바른 대상이 있어야 합니다!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Zombie.hasPlayer(p)) {
                    Bukkit.broadcastMessage("§c" + p.getName() + "이(가) 좀비가 되었습니다!");
                    Human.removePlayer(p);
                    Zombie.addPlayer(p);
                    zEffect(p);
                } else {
                    sender.sendMessage("§c" + p.getName() + "은(는) 이미 좀비입니다.");
                }
            } else {
                sender.sendMessage("§c올바른 대상이 있어야 합니다!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("human")) {
            if (args.length != 0) {
                if (args[0].length() != 0 && Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (!Human.hasPlayer(p)) {
                        Bukkit.broadcastMessage("§b" + p.getName() + "이(가) 인간이 되었습니다!");
                        Zombie.removePlayer(p);
                        Human.addPlayer(p);
                        for (PotionEffect e : p.getActivePotionEffects()) {
                            p.removePotionEffect(e.getType());
                        }
                    } else {
                        sender.sendMessage("§b" + p.getName() + "은(는) 이미 인간입니다.");
                    }
                } else {
                    sender.sendMessage("§c올바른 대상이 있어야 합니다!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Human.hasPlayer(p)) {
                    Bukkit.broadcastMessage("§b" + p.getName() + "이(가) 인간이 되었습니다!");
                    Zombie.removePlayer(p);
                    Human.addPlayer(p);
                    for (PotionEffect e : p.getActivePotionEffects()) {
                        p.removePotionEffect(e.getType());
                    }
                } else {
                    sender.sendMessage("§b" + p.getName() + "은(는) 이미 인간입니다.");
                }
            } else {
                sender.sendMessage("§c올바른 대상이 있어야 합니다!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("zremove")) {
            if (args.length != 0) {
                if (args[0].length() != 0 && Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    Human.removePlayer(p);
                    Zombie.removePlayer(p);
                    sender.sendMessage(p.getName() + "은(는) 이제 아무것도 아닙니다.");
                } else {
                    sender.sendMessage("§c올바른 대상이 있어야 합니다!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                Human.removePlayer(p);
                Zombie.removePlayer(p);
                sender.sendMessage(p.getName() + "은(는) 이제 아무것도 아닙니다.");
            } else {
                sender.sendMessage("§c올바른 대상이 있어야 합니다!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("zlist")) {
            ArrayList<Player> ZombieList = new ArrayList();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Zombie.hasPlayer(p)) {
                    ZombieList.add(p);
                }
            }
            ArrayList<Player> HumanList = new ArrayList();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Human.hasPlayer(p)) {
                    HumanList.add(p);
                }
            }
            sender.sendMessage("§c현재 좀비 수: " + ZombieList.size());
            for (Player p : ZombieList) {
                sender.sendMessage("§c" + p);
            }
            sender.sendMessage("§b현재 인간 수: " + HumanList.size());
            for (Player p : HumanList) {
                sender.sendMessage("§b" + p);
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("zget")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.getInventory().addItem(Antibiotic);
                p.getInventory().addItem(Vaccine);
                p.getInventory().addItem(new ItemStack(Material.DIAMOND));
                p.getInventory().addItem(new ItemStack(Material.HEART_OF_THE_SEA));
            }
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

    @EventHandler // 좀비 행동 제한
    public void onZombieComsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            if (!ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals("백신")) {
                e.setCancelled(true);
                p.sendMessage("§c당신의 몸은 무언가를 소화할 수 없습니다.");
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
                    if (!Bukkit.getScheduler().isQueued(InfectTask)) {
                        if (Rand.nextInt(5) == 0) {
                            Victim.sendMessage("§c물린 상처가 엄청나게 깊습니다...");
                            Victim.sendMessage("§c빨리 항생제를 찾지 못하면 5분 후에는 사람이 아니게 될 것입니다.");
                            InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                                @Override
                                public void run() {
                                    Bukkit.broadcastMessage("§c" + p.getName() + "(이)가 좀비가 되었습니다!");
                                    Zombie.addPlayer(p);
                                    zEffect(p);
                                }
                            }, 6000L);
                    }
                }
            }
        }
    }

    @EventHandler // 좀비 감염2222
    public void onZombieInfectbyMob(EntityDamageByEntityEvent e) {
        Entity Damager = e.getDamager();
        if (Damager instanceof Zombie) {
            if (e.getEntity() instanceof Player && Human.hasPlayer((Player) e.getEntity())) {
                Player p = (Player) e.getEntity();
                if (!Bukkit.getScheduler().isQueued(InfectTask)) {
                    if (Rand.nextInt(20) == 0) {
                        p.sendMessage("§c물린 상처가 엄청나게 깊습니다...");
                        p.sendMessage("§c빨리 항생제를 찾지 못하면 20분 후에는 사람이 아니게 될 것입니다.");
                        InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.broadcastMessage("§c" + p.getName() + "(이)가 좀비가 되었습니다!");
                                Zombie.addPlayer(p);
                                zEffect(p);
                            }
                        }, 24000L);
                    }
                }
            }
        }
    }

    @EventHandler // 아이템 사용
    public void onItemUse(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("항생제")) {
            if (Human.hasPlayer(p) && Bukkit.getScheduler().isQueued(InfectTask)) {
                Bukkit.getScheduler().cancelTask(InfectTask);
                p.sendMessage("§b당신은 몸이 정화되는 것을 느꼈습니다.");
            } else if (Human.hasPlayer(p)) {
                e.setCancelled(true);
                p.sendMessage("어째서인지, 당신은 이것을 사용하지 않으려는 강한 욕구를 느꼈습니다.");
            }
        } else if (ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("백신")) {
            if (Zombie.hasPlayer(p)) {
                Human.addPlayer(p);
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                p.sendMessage("§b당신은 몸에 생기가 도는 것을 느꼈습니다!");
                Bukkit.broadcastMessage("§b" + p.getName() + "(이)가 인간이 되었습니다!");
            } else {
                e.setCancelled(true);
                p.sendMessage("어째서인지, 당신은 이것을 사용하지 않으려는 강한 욕구를 느꼈습니다.");
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