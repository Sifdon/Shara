package org.seemsGood.shara.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmShop extends RealmObject {

    @PrimaryKey
    private String key;

    private boolean isFavorite;
    private int cartCount;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void changeFavorite(){
        isFavorite = !isFavorite;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

}
