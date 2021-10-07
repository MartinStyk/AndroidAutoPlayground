package com.example.androidautoplayground

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.navigation.model.NavigationTemplate

class PlaygroundScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {

        return MessageTemplate.Builder("Test action")
            .addAction(
                Action.Builder()
                    .setOnClickListener { }
                    .setTitle("Action")
                    .build()
                )
            .setTitle("Playground")
            .build()
    }

}