package com.jmsgvn.util;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.staff.Report;
import com.jmsgvn.util.gui.Button;
import com.jmsgvn.util.gui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    private static Connection connection;
    private final OmniSCS plugin;

    public DataSource(OmniSCS plugin) {
        this.plugin = plugin;

        registerSQL();
    }

    private void registerSQL() {
        plugin.getLogger().info("Loading mySQL database...");
        String url = plugin.getConfig().getString("mysqlurl");
        String user = plugin.getConfig().getString("mysqluser");
        String password = plugin.getConfig().getString("mysqlpassword");

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    connection = DriverManager.getConnection(url, user, password);
                    if (DataSource.getConnection() != null) {
                        plugin.getLogger().info("Connection to mySQL database successful.");
                        loadReports();
                    } else {
                        plugin.getLogger().info("Failed to connect to mySQL database.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public static void loadReports() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Statement statement = DataSource.getConnection().createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM reports");

                    List<Report> reports = new ArrayList<>();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String playerName = rs.getString("player_name");
                        long reportTimestamp = rs.getLong("report_timestamp");
                        String server = rs.getString("server");
                        String playerReported = rs.getString("player_reported");
                        String message = rs.getString("message");
                        String solver = rs.getString("solver");
                        boolean solved = rs.getBoolean("solved");
                        long solvedTimestamp = rs.getLong("solved_timestamp");

                        reports.add(new Report(id, playerName, reportTimestamp, server, playerReported, message, solved, solver, solvedTimestamp));
                    }
                    Report.getReports().clear();
                    Report.getReports().addAll(reports);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(OmniSCS.getInstance());
    }

    public static void loadReportsMenu(Player player, boolean solved) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Statement statement = DataSource.getConnection().createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM reports");

                    List<Report> reports = new ArrayList<>();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String playerName = rs.getString("player_name");
                        long reportTimestamp = rs.getLong("report_timestamp");
                        String server = rs.getString("server");
                        String playerReported = rs.getString("player_reported");
                        String message = rs.getString("message");
                        String solver = rs.getString("solver");
                        boolean solved = rs.getBoolean("solved");
                        long solvedTimestamp = rs.getLong("solved_timestamp");

                        reports.add(new Report(id, playerName, reportTimestamp, server, playerReported, message, solved, solver, solvedTimestamp));
                    }
                    Report.getReports().clear();
                    Report.getReports().addAll(reports);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Menu reportsMenu = new Menu() {

                    @Override
                    public String getTitle(Player player) {
                        return ChatColor.GRAY + "Reports";
                    }

                    @Override
                    public boolean isAutoUpdate() {
                        return true;
                    }

                    @Override
                    public boolean isUpdateAfterClick() {
                        return true;
                    }

                    @Override
                    public Map<Integer, Button> getButtons(Player var1) {
                        Map<Integer, Button> buttonMap = new HashMap<>();
                        List<Report> reports = Report.getReports();


                        for (Report report : reports) {
                            if (report.isSovled() == solved) {
                                buttonMap.put(buttonMap.size(), new Button() {
                                    @Override
                                    public String getName(Player player) {
                                        return CC.translate("&c" + report.getPlayerName());
                                    }

                                    @Override
                                    public List<String> getDescription(Player player) {
                                        List<String> toReturn = new ArrayList<>();

                                        toReturn.add(CC.translate("&7&m--------------------------"));
                                        toReturn.add(CC.translate("&cReported&7: &f" + TimeUtils.formatIntoCalendarString((new java.util.Date(report.getReportTimestamp())))));
                                        toReturn.add(CC.translate("&cServer&7: &f" + report.getServer()));
                                        if (report.getPlayerReported().equalsIgnoreCase("helpop")) {
                                            toReturn.add(CC.translate("&cType&7: &fHelpop"));
                                        } else {
                                            toReturn.add(CC.translate("&cUser reported&7: &f" + report.getPlayerReported()));
                                        }
                                        toReturn.add(CC.translate("&cMessage&7: "));

                                        toReturn.add(CC.translate("&f" + report.getMessage()));
                                        if (report.isSovled()) {
                                            toReturn.add(CC.translate(""));
                                            toReturn.add(CC.translate("&cSolved&7: " + (report.isSovled() ? "&ayes" : "&cno")));
                                            toReturn.add(CC.translate("&cSolver&7: &f" + report.getSolver()));
                                            toReturn.add(CC.translate("&cSolved Timestamp&7: &f") + TimeUtils.formatIntoCalendarString((new Date(report.getSolvedTimestamp()))));
                                        }
                                        toReturn.add(CC.translate("&7&m--------------------------"));
                                        return toReturn;
                                    }

                                    @Override
                                    public Material getMaterial(Player player) {
                                        return Material.BOOK;
                                    }

                                    @Override
                                    public void clicked(Player player, int slot, ClickType clickType) {
                                        if (clickType.isShiftClick()) {
                                            if (!report.isSovled()) {
                                                report.setSovled(true);
                                                report.setSolver(player.getName());
                                                report.setSolvedTimestamp(System.currentTimeMillis());
                                            }
                                        } else {
                                            if (Bukkit.getPlayer(report.getPlayerName()) != null) {
                                                player.teleport(Bukkit.getPlayer(report.getPlayerName()));
                                            }
                                        }
                                    }
                                });
                            }
                            }
                        return buttonMap;
                    }

                };

                reportsMenu.openMenu(player);

            }
        }.runTaskAsynchronously(OmniSCS.getInstance());
    }

    public static void UPDATE_STRING(int id, String location, String update) {
        String query = "update reports set " + location + " = ? where ID = ?";

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    PreparedStatement statement = DataSource.getConnection().prepareStatement(query);
                    statement.setString(1, update);
                    statement.setInt(2, id);
                    statement.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }.runTaskAsynchronously(OmniSCS.getInstance());

    }

    public static void UPDATE_LONG(int id, String location, long update) {
        String query = "update reports set " + location + " = ? where ID = ?";

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    PreparedStatement statement = DataSource.getConnection().prepareStatement(query);
                    statement.setLong(1, update);
                    statement.setInt(2, id);

                    statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(OmniSCS.getInstance());

    }

    public static void UPDATE_INTEGER(int id, String location, int update) {
        String query = "update reports set " + location + " = ? where ID = ?";

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    PreparedStatement statement = DataSource.getConnection().prepareStatement(query);
                    statement.setInt(1, update);
                    statement.setInt(2, id);

                    statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(OmniSCS.getInstance());
    }

    public static void UPDATE_BOOLEAN(int id, String location, boolean update) {
        String query = "update reports set " + location + " = ? where ID = ?";

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    PreparedStatement statement = DataSource.getConnection().prepareStatement(query);
                    statement.setBoolean(1, update);
                    statement.setInt(2, id);

                    statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(OmniSCS.getInstance());
    }


    public static Connection getConnection(){
        return connection;
    }
}
