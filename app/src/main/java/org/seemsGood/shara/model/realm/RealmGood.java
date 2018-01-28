package org.seemsGood.shara.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmGood extends RealmObject{

    @PrimaryKey
    private String key;

    private String kindKey;
    private String shopKey;

    private int count;
    private boolean isFavorite;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKindKey() {
        return kindKey;
    }

    public void setKindKey(String kindKey) {
        this.kindKey = kindKey;
    }

    public String getShopKey() {
        return shopKey;
    }

    public void setShopKey(String shopKey) {
        this.shopKey = shopKey;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}

