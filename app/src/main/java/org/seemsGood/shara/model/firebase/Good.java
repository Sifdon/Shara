package org.seemsGood.shara.model.firebase;

import org.seemsGood.shara.model.realm.RealmGood;

import java.io.Serializable;

import io.realm.Realm;

public class Good implements Serializable {

	private String name;
	private float price;
	private String description;
	private String mainIdea;

	public RealmGood data;

	public Good() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMainIdea() {
		return mainIdea;
	}

	public void setMainIdea(String mainIdea) {
		this.mainIdea = mainIdea;
	}

	public String getImageRef() {
		return "companies/"+ data.getShopKey()+"/products/"+ data.getKey()+"/prod1x1.png";
	}

	public void setKeys(final String key, final String kindKey, final String shopKey) {

		Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {

				RealmGood rg = realm
						.where(RealmGood.class)
						.equalTo("key",key)
						.findFirst();

				if(rg==null){
					rg = realm.createObject(RealmGood.class,key);
					rg.setKindKey(kindKey);
					rg.setShopKey(shopKey);
				}

				data = rg;
			}
		});
	}
}
