package com.venson.versatile.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.venson.versatile.app.databinding.ActivityMainBinding
import com.venson.versatile.log.logD
import com.venson.versatile.log.logW

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action") {
                    val path:String = (applicationContext.externalCacheDir?.path
                        ?: applicationContext.cacheDir.path).let {
                        applicationContext.packageName.let { packageName ->
                            it.substring(0, it.indexOf(packageName) + packageName.length)
                        }
                    }
                    path.logW()
                }
                .show()
        }
    }
}