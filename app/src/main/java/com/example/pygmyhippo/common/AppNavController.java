package com.example.pygmyhippo.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;

public class AppNavController {
    private boolean useNavigation;
    private NavController navController;

    public AppNavController(boolean useNavigation, NavController navController) {
        this.navController = navController;
        this.useNavigation = useNavigation;
    }

    public void navigate(int resId, @Nullable Bundle args) {
        if (useNavigation) {
            navController.navigate(resId, args);
        } else {
            // Nothing (useful in testing fragments).
        }
    }

    public void popBackStack() {
        if (useNavigation) {
            navController.popBackStack();
        } else {
            // Nothing (useful in testing fragments).
        }
    }

    public NavController getNavController() {
        return navController;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }
}
