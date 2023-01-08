package com.example.helpme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        initFields();
    }

    private void initFields() {
        ImageButton buttonGithubRepo = (ImageButton) findViewById(R.id.button_github_repo_link);

        buttonGithubRepo.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uo277516/HelpMe"))));
    }
}