package com.avatarworld.game;

import com.avatarworld.db.DatabaseManager;
import com.avatarworld.model.Item;
import com.avatarworld.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EconomyManager {
    private static EconomyManager instance;
    private DatabaseManager db = DatabaseManager.getInstance();

    private EconomyManager() {}

    public static synchronized EconomyManager getInstance() {
        if (instance == null) instance = new EconomyManager();
        return instance;
    }

    public boolean buyItem(User user, int itemId) {
        Item item = db.getItemById(itemId);
        if (item == null) return false;
        if (user.getCoins() < item.getPrice()) return false;
        if (db.hasItem(user.getId(), itemId)) return false;

        if (!user.removeCoins(item.getPrice())) return false;
        if (!db.updateUserCoins(user.getId(), user.getCoins())) return false;
        if (!db.addItemToInventory(user.getId(), itemId)) {
            user.addCoins(item.getPrice());
            db.updateUserCoins(user.getId(), user.getCoins());
            return false;
        }

        db.addTransaction(null, user.getId(), itemId, item.getPrice(), "shop_buy");
        db.addLog("SHOP_BUY", user.getUsername() + " bought " + item.getName() + " for " + item.getPrice(), "system");
        return true;
    }

    public boolean claimDaily(User user) {
        if (user != null && user.getLastDaily() != null) {
            java.sql.Timestamp last = (java.sql.Timestamp) user.getLastDaily();
            if (last.toLocalDateTime().toLocalDate().equals(LocalDate.now())) {
                return false;
            }
        }

        int dailyCoins = 200;
        user.addCoins(dailyCoins);
        db.updateUserCoins(user.getId(), user.getCoins());
        db.updateLastDaily(user.getId());
        db.addTransaction(null, user.getId(), null, dailyCoins, "daily");
        db.addLog("DAILY_BONUS", user.getUsername() + " claimed " + dailyCoins + " daily coins", "system");
        return true;
    }

    public boolean giftCoins(User from, User to, int amount) {
        if (from.getCoins() < amount || amount <= 0) return false;
        if (!from.removeCoins(amount)) return false;
        to.addCoins(amount);
        db.updateUserCoins(from.getId(), from.getCoins());
        db.updateUserCoins(to.getId(), to.getCoins());
        db.addTransaction(from.getId(), to.getId(), null, amount, "gift");
        db.addLog("COIN_GIFT", from.getUsername() + " gifted " + amount + " coins to " + to.getUsername(), "system");
        return true;
    }

    public boolean giftItem(User from, User to, int itemId) {
        if (!db.hasItem(from.getId(), itemId)) return false;
        db.addItemToInventory(to.getId(), itemId);
        db.addTransaction(from.getId(), to.getId(), itemId, 0, "gift_item");
        db.addLog("ITEM_GIFT", from.getUsername() + " gifted item " + itemId + " to " + to.getUsername(), "system");
        return true;
    }
}
