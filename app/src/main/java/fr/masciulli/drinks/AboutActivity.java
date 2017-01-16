package fr.masciulli.drinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_about);

        TextView versionView = (TextView) findViewById(R.id.version);
        versionView.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }
}
