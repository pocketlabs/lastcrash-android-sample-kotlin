package io.lastcrash.sample_kotlin

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.lastcrash.sample_kotlin.ui.theme.LastCrashSampleAppKotlinTheme
import io.lastcrash.sdk.LastCrash
import io.lastcrash.sdk.LastCrashListener

class MainActivity : ComponentActivity(), LastCrashListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LastCrash.setListener(this)
        LastCrash.configure("8c1ebdd74fd64190b41ddd93e8e3ec48", this, true)
        LastCrash.applicationInitialized()

        setContent {
            LastCrashSampleAppKotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    Button(
                        onClick = {
                            val test: View? = null
                            test!!.alpha
                        },
                        modifier = Modifier.size(width = 100.dp, height = 20.dp)
                    ) {
                        Text(text = "Test Crash")
                    }
                }
            }
        }
    }

    override fun lastCrashDidCrash() {
        LastCrash.send()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LastCrashSampleAppKotlinTheme {
        Greeting("Android")
    }
}