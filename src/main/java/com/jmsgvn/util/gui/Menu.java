package com.jmsgvn.util.gui;

import com.google.common.base.Preconditions;
import com.jmsgvn.OmniSCS;
import com.jmsgvn.util.CC;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {

    private static Method openInventoryMethod;
    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();
    private boolean autoUpdate = false;
    private boolean updateAfterClick = false;
    private final boolean placeHolder = false;
    private final boolean noncancellingInventory = false;
    private String staticTitle = null;
    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, BukkitRunnable> checkTasks;
    private int size = -1;

    private Inventory createInventory(Player player) {
        Map<Integer, Button> inventoryButtons = this.getButtons(player);
        Inventory inventory = Bukkit.createInventory(player, this.size(inventoryButtons), CC.translate(this.getTitle(player)));

        for (Map.Entry<Integer, Button> integerButtonEntry : inventoryButtons.entrySet()) {
            Map.Entry<Integer, Button> buttonEntry = (Map.Entry) integerButtonEntry;
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inventory.setItem((Integer) buttonEntry.getKey(), ((Button) buttonEntry.getValue()).getButtonItem(player));
        }

        if(this.isPlaceHolder()) {
            Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, new String[0]);

            for(int index = 0; index < this.size(inventoryButtons); ++index) {
                if (inventoryButtons.get(index) == null) {
                    this.buttons.put(index, placeholder);
                    inventory.setItem(index, placeholder.getButtonItem(player));
                }
            }
        }

        return inventory;
    }

    private static Method getOpenInventoryMethod() {
        if (openInventoryMethod == null) {
            try {
                (openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, Integer.TYPE)).setAccessible(true);
            } catch (NoSuchMethodException var1) {
                var1.printStackTrace();
            }
        }

        return openInventoryMethod;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = (String) Preconditions.checkNotNull(staticTitle);
    }

    public Menu(String staticTitle, int size) {
        this(staticTitle);
        this.size = size;
    }

    public void openMenu(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        Inventory inventory = this.createInventory(player);

        player.openInventory(inventory);
        this.update(player);

//        try {
//            getOpenInventoryMethod().invoke(player, inventory, entityPlayer, 0);
//            this.update(player);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void update(final Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline()) {
                    Menu.cancelCheck(player);
                    Menu.currentlyOpenedMenus.remove(player.getName());
                }

                if(Menu.this.isAutoUpdate()) {
                    player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
                }
            }
        };

        runnable.runTaskTimerAsynchronously(OmniSCS.getInstance(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public static void cancelCheck(Player player) {
        if(checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        Iterator iterator = buttons.keySet().iterator();

        while (iterator.hasNext()) {
            int buttonValue = (Integer) iterator.next();
            if(buttonValue > highest) {
                highest = buttonValue;
            }
        }

        if(this.size != -1) {
            highest = size;
        }

        return (int)(Math.ceil((double)(highest+1)/9.0D)*9.0D);
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player var1);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }


    static {
        OmniSCS.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), OmniSCS.getInstance());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

    public ConcurrentHashMap<Integer, Button> getButtons() {
        return buttons;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public boolean isUpdateAfterClick() {
        return updateAfterClick;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public boolean isNoncancellingInventory() {
        return noncancellingInventory;
    }

    public String getStaticTitle() {
        return staticTitle;
    }

    public static Map<String, Menu> getCurrentlyOpenedMenus() {
        return currentlyOpenedMenus;
    }

    public static Map<String, BukkitRunnable> getCheckTasks() {
        return checkTasks;
    }

    public int getSize() {
        return size;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }
}