package fr.masciulli.drinks.ui.activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fr.masciulli.drinks.DrinksApplication;
import fr.masciulli.drinks.R;
import fr.masciulli.drinks.model.Drink;
import fr.masciulli.drinks.model.Liquor;
import fr.masciulli.drinks.net.Client;
import fr.masciulli.drinks.ui.EnterPostponeTransitionCallback;
import fr.masciulli.drinks.ui.adapter.LiquorRelatedAdapter;
import fr.masciulli.drinks.ui.adapter.holder.TileViewHolder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LiquorActivity extends AppCompatActivity {
    private static final String TAG = LiquorActivity.class.getSimpleName();

    private static final boolean TRANSITIONS_AVAILABLE = false;//Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final String EXTRA_LIQUOR = "extra_liquor";
    private static final String STATE_DRINKS = "state_drinks";

    private Liquor liquor;
    private Client client;
    private LiquorRelatedAdapter adapter;

    private RecyclerView recyclerView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TRANSITIONS_AVAILABLE) {
            postponeEnterTransition();
        }

        liquor = getIntent().getParcelableExtra(EXTRA_LIQUOR);
        client = DrinksApplication.get(this).getClient();

        setContentView(R.layout.activity_liquor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(liquor.name());

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Picasso.with(this)
                .load(liquor.imageUrl())
                .noFade()
                .into(imageView, new EnterPostponeTransitionCallback(this));

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        setupRecyclerView();

        if (savedInstanceState == null) {
            loadDrinks();
        } else {
            List<Drink> drinks = savedInstanceState.getParcelableArrayList(STATE_DRINKS);
            onDrinksRetrieved(drinks);
        }
    }

    private void setupRecyclerView() {
        adapter = new LiquorRelatedAdapter();
        adapter.setLiquor(liquor);
        adapter.setWikipediaClickListener((position, liquor) -> onWikipediaClick());

        adapter.setDrinkClickListener(this::onDrinkClick);

        recyclerView.setLayoutManager(adapter.craftLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void onWikipediaClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(liquor.wikipedia()));
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onDrinkClick(int position, Drink drink) {
        Intent intent = new Intent(this, DrinkActivity.class);
        intent.putExtra(DrinkActivity.EXTRA_DRINK, drink);
        if (TRANSITIONS_AVAILABLE) {
            TileViewHolder holder = (TileViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            String transition = getString(R.string.transition_drink);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, holder.getImageView(), transition);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void loadDrinks() {
        List<Drink> allDrinks = client.getDrinks();

        List<Drink> drinks = new ArrayList<>();
        for (Drink drink : allDrinks) {
            if (matches(drink)) {
                drinks.add(drink);
            }
        }
        this.onDrinksRetrieved(drinks);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Couldn't retrieve liquors", throwable);
    }

    private void onDrinksRetrieved(List<Drink> drinks) {
        adapter.setRelatedDrinks(drinks);
    }

    private boolean matches(Drink drink) {
        for (String ingredient : drink.ingredients()) {
            String lowerCaseIngredient = ingredient.toLowerCase(Locale.US);
            if (lowerCaseIngredient.contains(liquor.name().toLowerCase(Locale.US))) {
                return true;
            }
            for (String name : liquor.otherNames()) {
                if (lowerCaseIngredient.contains(name.toLowerCase(Locale.US))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_DRINKS, adapter.getDrinks());
    }
}
