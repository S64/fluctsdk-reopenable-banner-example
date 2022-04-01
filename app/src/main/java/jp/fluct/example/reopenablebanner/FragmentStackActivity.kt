package jp.fluct.example.reopenablebanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import jp.fluct.example.reopenablebanner.databinding.FragmentStackActivityBinding
import jp.fluct.example.reopenablebanner.databinding.FragmentStackFragmentBinding
import jp.fluct.fluctsdk.FluctAdView
import jp.fluct.fluctsdk.FluctErrorCode
import java.lang.IllegalStateException
import kotlin.reflect.KClass

abstract class AbsFragmentStackActivity<T : AbsFragmentStackActivity.AbsMyFragment> : AppCompatActivity() {

    private val BACKSTACK_NAME = null
    private lateinit var binding: FragmentStackActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentStackActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        binding.append.setOnClickListener {
            appendLog("onClick")

            supportFragmentManager.commit {
                replace(binding.container.id, getFragmentClass().java, null)
                addToBackStack(BACKSTACK_NAME)
            }
        }
    }

    abstract fun getFragmentClass(): KClass<T>

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
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

    abstract class AbsMyFragment : Fragment() {

        private val TAG = "MyFragment"

        private lateinit var binding: FragmentStackFragmentBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            appendLog("onCreateView")
            return FragmentStackFragmentBinding.inflate(inflater, container, false)
                .apply {
                    binding = this
                }.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            appendLog("onViewCreated")

            if (binding.container.childCount > 0) {
                throw IllegalStateException("Already attached")
            }

            binding.container.addView(
                createBanner()
            )
        }

        abstract fun createBanner(): View

        protected fun appendLog(msg: String) {
            (activity as? AbsFragmentStackActivity<*>)
                ?.appendLog(msg)
                ?: Log.w(TAG, msg)
        }

    }

}


class FragmentStackActivity : AbsFragmentStackActivity<FragmentStackActivity.MyFragment>() {

    class MyFragment : AbsMyFragment() {

        override fun createBanner(): View {
            return FluctAdView(
                requireContext(),
                Consts.GROUP_ID,
                Consts.UNIT_ID,
                Consts.AD_SIZE,
                null,
                listener
            ).apply {
                loadAd()
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

    override fun getFragmentClass(): KClass<MyFragment> {
        return MyFragment::class
    }

}
