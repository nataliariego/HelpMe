package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FaqActivity extends AppCompatActivity {

    private ImageButton buttonGithubRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        initFields();
    }

    private void initFields(){
        buttonGithubRepo = (ImageButton) findViewById(R.id.button_github_repo_link);

        buttonGithubRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uo277516/HelpMe")));
            }
        });
    }
}