/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertpusher

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import com.example.android.dessertpusher.databinding.ActivityMainBinding
import timber.log.Timber

const val REVENUE = "revenue"
const val DESSERSTS_SOLD = "dessertsSold"
const val SECONDS_COUNT = "secondsCount"

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private var revenue = 0
    private var dessertsSold = 0

    private lateinit var dessertTimer: DessertTimer
    private lateinit var binding: ActivityMainBinding

    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    private val allDesserts = listOf(
            Dessert(R.drawable.cupcake, 5, 0),
            Dessert(R.drawable.donut, 10, 5),
            Dessert(R.drawable.eclair, 15, 20),
            Dessert(R.drawable.froyo, 30, 50),
            Dessert(R.drawable.gingerbread, 50, 100),
            Dessert(R.drawable.honeycomb, 100, 200),
            Dessert(R.drawable.icecreamsandwich, 500, 500),
            Dessert(R.drawable.jellybean, 1000, 1000),
            Dessert(R.drawable.kitkat, 2000, 2000),
            Dessert(R.drawable.lollipop, 3000, 4000),
            Dessert(R.drawable.marshmallow, 4000, 8000),
            Dessert(R.drawable.nougat, 5000, 16000),
            Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Timber.i("OnCreate called")

        dessertTimer = DessertTimer(this.lifecycle)

        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(REVENUE)
            dessertsSold = savedInstanceState.getInt(DESSERSTS_SOLD)
            dessertTimer.secondsCount = savedInstanceState.getInt(SECONDS_COUNT)
        }

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("OnSaveInstanceState called")

        outState.putInt(REVENUE, revenue)
        outState.putInt(DESSERSTS_SOLD, dessertsSold)
        outState.putInt(SECONDS_COUNT, dessertTimer.secondsCount)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Timber.i("OnRestoreInstanceState called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("OnResume called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("OnPause called")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("OnStart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("OnDestroy called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("OnRestart called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("OnStop called")
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text, dessertsSold, revenue))
                .setType("text/plain")
                .intent
        try {
            startActivity(shareIntent)
        }
        catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}
