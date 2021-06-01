package com.coreyhorn.mvpiframeworkexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframeworkexample.fragment.ExampleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MVPISettings.loggingEnabled = true
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            val fragment = ExampleFragment()

            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }
}
