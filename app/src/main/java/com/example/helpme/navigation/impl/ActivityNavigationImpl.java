package com.example.helpme.navigation.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.example.helpme.navigation.ActivityNavigation;

public class ActivityNavigationImpl implements ActivityNavigation {

    @Override
    public void go(Context context, Class toClass) {
        Intent intent = new Intent(context, toClass);
        context.startActivity(intent);
    }
}
