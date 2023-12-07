package com.demo.coroutinestart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.demo.coroutinestart.databinding.ActivityMainBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
            binding.progress.isVisible = true
            binding.buttonLoad.isEnabled = false

            val deferredCity: Deferred<String> = lifecycleScope.async {
                val city = loadCity()
                binding.tvLocation.text = city
                city
            }
            val deferredTemp: Deferred<Int> = lifecycleScope.async {
                val temp = loadTemperature()
                binding.tvTemperature.text = temp.toString()
                temp
            }
            lifecycleScope.launch {
                val city = deferredCity.await()
                val temp = deferredTemp.await()
                Toast.makeText(
                    this@MainActivity,
                    "City: $city, Temp: $temp",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
            }
        }
    }

//    private suspend fun loadData() {
//        binding.progress.isVisible = true
//        binding.buttonLoad.isEnabled = false
//
//        val city = loadCity()
//        binding.tvLocation.text = city
//
//        val temp = loadTemperature(city)
//        binding.tvTemperature.text = temp.toString()
//
//        binding.progress.isVisible = false
//        binding.buttonLoad.isEnabled = true
//    }

    private suspend fun loadCity(): String {
        delay(2000)
        return "Minsk"
    }

    private suspend fun loadTemperature(): Int {
        delay(3000)
        return 17
    }

    private fun loadWithoutCoroutine(step: Int = 0, obj: Any? = null) {
        when (step) {
            0 -> {
                binding.progress.isVisible = true
                binding.buttonLoad.isEnabled = false
                loadCityWithoutCoroutine {
                    loadWithoutCoroutine(1, it)
                }
            }
            1 -> {
                val city = obj as String
                binding.tvLocation.text = city
                loadTemperatureWithoutCoroutine(city) {
                    loadWithoutCoroutine(2, it)
                }
            }
            2 -> {
                val temp = obj as Int
                binding.tvTemperature.text = temp.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
            }
        }
    }

    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
//        thread {
//            Thread.sleep(2000)
//            runOnUiThread {
//                callback.invoke("Moscow")
//            }
//        }
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke("Moscow")
        }, 2000)
    }

    private fun loadTemperatureWithoutCoroutine(city: String, callback: (Int) -> Unit) {
//        thread {
//            runOnUiThread {
//                Toast.makeText(
//                    this,
//                    getString(R.string.loading_temperature_toast, city),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            Thread.sleep(2000)
//            runOnUiThread {
//                callback.invoke(17)
//            }
//        }
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke(17)
        }, 2000)
    }
}