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
        Meta.setDisplayName("??b?????????");
        Antibiotic.setItemMeta(Meta);
        PotionMeta PotionMeta = (PotionMeta) Vaccine.getItemMeta();
        PotionMeta.setColor(Color.AQUA);
        PotionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        PotionMeta.setDisplayName("??b??????");
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
                        Bukkit.broadcastMessage("??c" + p.getName() + "???(???) ????????? ???????????????!");
                        Human.removePlayer(p);
                        Zombie.addPlayer(p);
                        zEffect(p);
                    } else {
                        sender.sendMessage("??c" + p.getName() + "???(???) ?????? ???????????????.");
                    }
                } else {
                    sender.sendMessage("??c????????? ????????? ????????? ?????????!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Zombie.hasPlayer(p)) {
                    Bukkit.broadcastMessage("??c" + p.getName() + "???(???) ????????? ???????????????!");
                    Human.removePlayer(p);
                    Zombie.addPlayer(p);
                    zEffect(p);
                } else {
                    sender.sendMessage("??c" + p.getName() + "???(???) ?????? ???????????????.");
                }
            } else {
                sender.sendMessage("??c????????? ????????? ????????? ?????????!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("human")) {
            if (args.length != 0) {
                if (args[0].length() != 0 && Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (!Human.hasPlayer(p)) {
                        Bukkit.broadcastMessage("??b" + p.getName() + "???(???) ????????? ???????????????!");
                        Zombie.removePlayer(p);
                        Human.addPlayer(p);
                        for (PotionEffect e : p.getActivePotionEffects()) {
                            p.removePotionEffect(e.getType());
                        }
                    } else {
                        sender.sendMessage("??b" + p.getName() + "???(???) ?????? ???????????????.");
                    }
                } else {
                    sender.sendMessage("??c????????? ????????? ????????? ?????????!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Human.hasPlayer(p)) {
                    Bukkit.broadcastMessage("??b" + p.getName() + "???(???) ????????? ???????????????!");
                    Zombie.removePlayer(p);
                    Human.addPlayer(p);
                    for (PotionEffect e : p.getActivePotionEffects()) {
                        p.removePotionEffect(e.getType());
                    }
                } else {
                    sender.sendMessage("??b" + p.getName() + "???(???) ?????? ???????????????.");
                }
            } else {
                sender.sendMessage("??c????????? ????????? ????????? ?????????!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("zremove")) {
            if (args.length != 0) {
                if (args[0].length() != 0 && Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    Human.removePlayer(p);
                    Zombie.removePlayer(p);
                    sender.sendMessage(p.getName() + "???(???) ?????? ???????????? ????????????.");
                } else {
                    sender.sendMessage("??c????????? ????????? ????????? ?????????!");
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                Human.removePlayer(p);
                Zombie.removePlayer(p);
                sender.sendMessage(p.getName() + "???(???) ?????? ???????????? ????????????.");
            } else {
                sender.sendMessage("??c????????? ????????? ????????? ?????????!");
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
            sender.sendMessage("??c?????? ?????? ???: " + ZombieList.size());
            for (Player p : ZombieList) {
                sender.sendMessage("??c" + p);
            }
            sender.sendMessage("??b?????? ?????? ???: " + HumanList.size());
            for (Player p : HumanList) {
                sender.sendMessage("??b" + p);
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

    @EventHandler // ?????? ?????? ??????
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
                        p.sendMessage("??c????????? ????????? ????????????.");
                    }
                }
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    if (i == Material.FLINT_AND_STEEL || i == Material.FIREWORK_ROCKET || i == Material.FIRE_CHARGE || i == Material.ENDER_EYE) {
                        e.setCancelled(true);
                        p.sendMessage("??c????????? ????????? ????????????.");
                    }
                } else if (a == Action.RIGHT_CLICK_AIR) {
                    if (i == Material.ENDER_EYE) {
                        e.setCancelled(true);
                        p.sendMessage("??c????????? ????????? ????????????.");
                    }
                }
            }
        }
    }

    @EventHandler // ?????? ?????? ??????
    public void onZombieComsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            if (!ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals("??????")) {
                e.setCancelled(true);
                p.sendMessage("??c????????? ?????? ???????????? ????????? ??? ????????????.");
            }
        }
    }

    int InfectTask;

    @EventHandler // ?????? ??????
    public void onZombieInfect(EntityDamageByEntityEvent e) {
        Entity Victim = e.getEntity();
        Entity Damager = e.getDamager();
            if (Victim instanceof Player && Damager instanceof Player) {
                if (Zombie.hasPlayer((Player) Damager) && Human.hasPlayer((Player) Victim)) {
                    Player p = (Player) Victim;
                    if (!Bukkit.getScheduler().isQueued(InfectTask)) {
                        if (Rand.nextInt(5) == 0) {
                            Victim.sendMessage("??c?????? ????????? ???????????? ????????????...");
                            Victim.sendMessage("??c?????? ???????????? ?????? ????????? 5??? ????????? ????????? ????????? ??? ????????????.");
                            InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                                @Override
                                public void run() {
                                    Bukkit.broadcastMessage("??c" + p.getName() + "(???)??? ????????? ???????????????!");
                                    Zombie.addPlayer(p);
                                    zEffect(p);
                                }
                            }, 6000L);
                    }
                }
            }
        }
    }

    @EventHandler // ?????? ??????2222
    public void onZombieInfectbyMob(EntityDamageByEntityEvent e) {
        Entity Damager = e.getDamager();
        if (Damager instanceof Zombie) {
            if (e.getEntity() instanceof Player && Human.hasPlayer((Player) e.getEntity())) {
                Player p = (Player) e.getEntity();
                if (!Bukkit.getScheduler().isQueued(InfectTask)) {
                    if (Rand.nextInt(20) == 0) {
                        p.sendMessage("??c?????? ????????? ???????????? ????????????...");
                        p.sendMessage("??c?????? ???????????? ?????? ????????? 20??? ????????? ????????? ????????? ??? ????????????.");
                        InfectTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.broadcastMessage("??c" + p.getName() + "(???)??? ????????? ???????????????!");
                                Zombie.addPlayer(p);
                                zEffect(p);
                            }
                        }, 24000L);
                    }
                }
            }
        }
    }

    @EventHandler // ????????? ??????
    public void onItemUse(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("?????????")) {
            if (Human.hasPlayer(p) && Bukkit.getScheduler().isQueued(InfectTask)) {
                Bukkit.getScheduler().cancelTask(InfectTask);
                p.sendMessage("??b????????? ?????? ???????????? ?????? ???????????????.");
            } else if (Human.hasPlayer(p)) {
                e.setCancelled(true);
                p.sendMessage("???????????????, ????????? ????????? ???????????? ???????????? ?????? ????????? ???????????????.");
            }
        } else if (ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).equals("??????")) {
            if (Zombie.hasPlayer(p)) {
                Human.addPlayer(p);
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                p.sendMessage("??b????????? ?????? ????????? ?????? ?????? ???????????????!");
                Bukkit.broadcastMessage("??b" + p.getName() + "(???)??? ????????? ???????????????!");
            } else {
                e.setCancelled(true);
                p.sendMessage("???????????????, ????????? ????????? ???????????? ???????????? ?????? ????????? ???????????????.");
            }
        }
    }

    @EventHandler // ?????? ?????????
    public void onZombieRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (Zombie.hasPlayer(p)) {
            zEffect(p);
        }
    }

    public Player getRandomPlayer(Team team, Player p) { // ??? ?????? ?????? ???????????? ??????
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

    @EventHandler // ????????? ????????? ??????
    public void onZombieUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack i = p.getInventory().getItemInMainHand();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            if (Zombie.hasPlayer(p)) {
                if (i.getType() == Material.DIAMOND) {
                    Player o = getRandomPlayer(Zombie, p);
                    if (p.getCooldown(Material.DIAMOND) != 0) {
                        p.sendMessage((p.getCooldown(Material.DIAMOND) / 20 + 1) + "??? ??????");
                    } else if (o == null) {
                        p.sendMessage("????????? ????????? ????????????!");
                    } else {
                        o.sendMessage("??d" + p.getName() + "???(???) ????????? ??????????????????!");
                        o.teleport(p);
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND));
                        p.setCooldown(Material.DIAMOND, 2000);
                    }
                } else if (i.getType() == Material.HEART_OF_THE_SEA) {
                    Player o = getRandomPlayer(Human, p);
                    if (p.getCooldown(Material.HEART_OF_THE_SEA) != 0) {
                        p.sendMessage((p.getCooldown(Material.HEART_OF_THE_SEA) / 20) + "??? ??????");
                    } else if (o == null) {
                        p.sendMessage("????????? ????????? ????????????!");
                    } else {
                        int oX = o.getLocation().getBlockX();
                        int oY = o.getLocation().getBlockY();
                        int oZ = o.getLocation().getBlockZ();
                        o.sendMessage("????????? ??????????????? ????????? ????????????.");
                        p.sendMessage("??d????????? ??????????????? " + o.getName() + "??? ?????? ????????? ??????????????????!");
                        p.sendMessage(o.getName() + "??? ?????? - X: " + oX + " Y: " + oY + " Z: " + oZ);
                        p.setCooldown(Material.HEART_OF_THE_SEA, 2000);
                    }
                }
            }
        }
    }
}