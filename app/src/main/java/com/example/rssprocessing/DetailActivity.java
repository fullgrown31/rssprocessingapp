package com.example.rssprocessing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    TextView tvTitle, tvPubDate, tvDescription;
    Button btnLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvPubDate = findViewById(R.id.tvPubDate);
        tvDescription = findViewById(R.id.tvDescription);
        btnLink = findViewById(R.id.btnLink);

        Intent data = getIntent();
        tvTitle.setText(data.getStringExtra("title"));
        tvPubDate.setText(data.getStringExtra("pubDate"));
        Spanned result = Html.fromHtml(data.getStringExtra("description"));
        tvDescription.setText(result);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Dwight", getIntent().getStringExtra("link"));
                Intent intent = new Intent(DetailActivity.this, WebViewActivity.class);
                intent.putExtra("link", getIntent().getStringExtra("link"));
                startActivity(intent);
            }
        });
    }

}
