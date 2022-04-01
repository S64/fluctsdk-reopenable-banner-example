package jp.fluct.example.reopenablebanner

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.fluct.example.reopenablebanner.databinding.BasicLifecycleActivityBinding
import jp.fluct.fluctsdk.FluctAdView
import jp.fluct.fluctsdk.FluctErrorCode

class BasicLifecycleActivity : AppCompatActivity() {

    private lateinit var binding: BasicLifecycleActivityBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicLifecycleActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        appendLog("onCreate")

        binding.add.setOnClickListener {
            appendLog("onClick")
            addBanner()
        }

        binding.finish.setOnClickListener {
            finish()
        }

        addBanner()

        binding.info.text = "API Level: ${Build.VERSION.SDK_INT}"
    }

    private fun addBanner() {
        appendLog("addBanner")

        binding.ads.addView(
            FluctAdView(
                this,
                Consts.GROUP_ID,
                Consts.UNIT_ID,
                Consts.AD_SIZE,
                null,
                listener
            ).apply {
                loadAd()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        appendLog("onResume")
    }

    override fun onStart() {
        super.onStart()
        appendLog("onStart")
    }

    override fun onPause() {
        super.onPause()
        appendLog("onPause")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        appendLog("onBackPressed")
    }

    private fun appendLog(msg: String) {
        binding.logs.apply {
            text = StringBuilder(text)
                .apply {
                    if (text.isNotEmpty()) {
                        append(System.lineSeparator())
                    }
                }
                .append(msg)
                .toString()
        }
    }

    private val listener = object : FluctAdView.Listener {

        override fun onLoaded() {
            appendLog("onLoaded")
        }

        override fun onFailedToLoad(p0: FluctErrorCode) {
            appendLog("onFailedToLoad: ${p0.label}")
        }

        override fun onLeftApplication() {
            appendLog("onLeftApplication")
        }

        override fun onUnloaded() {
            appendLog("onUnloaded")
        }

    }

}
