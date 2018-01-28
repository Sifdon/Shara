package org.seemsGood.shara.model.firebase;

import org.seemsGood.shara.model.realm.RealmShop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop implements Serializable {

	private String key;
	private String name;
	private String type;
	private String description;

	private float maxPointsPercentage;
	private float rating = 0.0f;
    private List<Integer> points;

	private List<String> categories;
	private Map<String,ShopAddress> addresses;
	private Map<String,List<String>> phones;

    private List<String> emails;
    private List<String> sites;

    private List<String> kindsValues;
    private List<String> kindsKeys;
    private Map<String,String> kinds;

    private List<List<String>> goodsKeys;
    private List<Map<String,Good>> goods;

    private Map<String, Map<String,String>> names;

    private List<String> reviewKeys;
    private Map<String,Review> reviews;

    public RealmShop data;

	public Shop() {
        points = new ArrayList<>();
        points.add(0);
        points.add(0);
        points.add(0);
        points.add(0);
        points.add(0);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public float getMaxPointsPercentage() {
        return maxPointsPercentage;
    }

    public void setMaxPointsPercentage(float maxPointsPercentage) {
        this.maxPointsPercentage = maxPointsPercentage;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
        this.points = points;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public ShopAddress getAddress(String key){
        return addresses.get(key);
    }

    public Map<String,ShopAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(Map<String,ShopAddress> addresses) {
        this.addresses = addresses;
    }

    public Map<String,List<String>> getPhones() {
        return phones;
    }

    public void setPhones(Map<String,List<String>> phones) {
        this.phones = phones;
    }

    public List<String> getKindsValues() {
        return kindsValues;
    }

    public void setKindsValues(List<String> kindsValues) {
        this.kindsValues = kindsValues;
    }

    public Map<String, String> getKinds() {
        return kinds;
    }

    public void setKinds(Map<String, String> kinds) {
        this.kinds = kinds;
    }

    public List<Map<String, Good>> getGoods() {
        return goods;
    }

    public void setGoods(List<Map<String, Good>> goods) {
        this.goods = goods;
    }

    public Map<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(Map<String, Review> reviews) {
        this.reviews = reviews;
    }

    public List<String> getReviewKeys() {
        return reviewKeys;
    }

    public void setReviewKeys(List<String> reviewKeys) {
        this.reviewKeys = reviewKeys;
    }

    public List<List<String>> getGoodsKeys() {
        return goodsKeys;
    }

    public void setGoodsKeys(List<List<String>> goodsKeys) {
        this.goodsKeys = goodsKeys;
    }

    public List<String> getKindsKeys() {
        return kindsKeys;
    }

    public void setKindsKeys(List<String> kindsKeys) {
        this.kindsKeys = kindsKeys;
    }

    public Map<String, Map<String, String>> getNames() {
        return names;
    }

    public void setNames(Map<String, Map<String, String>> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return key+"  "+name;
    }



    public void generateKinds() {
        kindsValues = new ArrayList<>(kinds.values());
        kindsKeys = new ArrayList<>(kinds.keySet());
        goodsKeys = new ArrayList<>();
        goods = new ArrayList<>();
        names = new HashMap<>();
        for (int i = 0; i < kindsValues.size(); i++) {
            goodsKeys.add(new ArrayList<String>());
            goods.add(new HashMap<String, Good>());
            names.put(String.valueOf(i),new HashMap<String, String>());
        }
    }

    public void setupReviews(){
        reviews = new HashMap<>();
        reviewKeys = new ArrayList<>();
    }

    public String getKindKey(int kind){
        return kindsKeys.get(kind);
    }

    public String getGoodKey(int kind, int key){
        return goodsKeys.get(kind).get(key);
    }

    public List<String> getGoodKeyList(int kind){
        return goodsKeys.get(kind);
    }

    public void addGoodKey(int kind, String key){
        goodsKeys.get(kind).add(key);
    }

    public Map<String,Good> getGoodsMap(int kind){
        return goods.get(kind);
    }

}

