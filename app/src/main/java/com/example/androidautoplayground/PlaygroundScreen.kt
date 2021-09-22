package com.example.androidautoplayground

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate

class PlaygroundScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setIcon(CarIcon.ALERT)
                    .setOnClickListener { carContext.finishCarApp() }
                    .build())
            .build()

        return NavigationTemplate.Builder().setActionStrip(actionStrip).build()
    }

}