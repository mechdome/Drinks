package fr.masciulli.drinks.net;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.masciulli.drinks.model.Drink;
import fr.masciulli.drinks.model.Liquor;

public class Client {

    private final Context mContext;

    public Client(Context context) {
        this(context, null);
    }

    Client(Context context, String baseUrl) {
        mContext = context;
    }

    public List<Drink> getDrinks() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory())
                    .create();

            return Arrays.asList(gson.fromJson(new InputStreamReader(mContext.getAssets().open("drinks.json"), "UTF-8"), Drink[].class));
        } catch (Exception e) {
            return null;
        }
    }

    public List<Liquor> getLiquors() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory())
                    .create();

            return Arrays.asList(gson.fromJson(new InputStreamReader(mContext.getAssets().open("liquors.json"), "UTF-8"), Liquor[].class));
        } catch (Exception e) {
            return null;
        }
    }
}
