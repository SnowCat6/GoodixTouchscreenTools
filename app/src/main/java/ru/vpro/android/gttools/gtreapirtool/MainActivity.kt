package ru.vpro.android.gttools.gtreapirtool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import ru.vpro.android.gttools.gtreapirtool.tools.GoodixTools
import ru.vpro.android.gttools.gtreapirtool.tools.SuperSU
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val su = SuperSU()
    private val gt = GoodixTools(su)
    private var configText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.subtitle = "GTXX touchscreen config read"

        fab?.visibility = View.GONE
        fab?.setOnClickListener { view ->
            sendReport(configText)
        }

        readConfig?.setOnClickListener {

            thread{
                if (checkSU()) {
                    configText = gt.readConfig().joinToString("\n")
                    if (configText.isEmpty()){
                        setResultValue("Chip config values FAILED", "No any data readed")
                    }else {
                        setResultValue("Chip config values", configText)
                        runOnUiThread {
                            fab?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun sendReport(configText: String)
    {
        with(AlertDialog.Builder(this))
        {
            if (reportMail(configText)) {
                setTitle("Send config to e-mail")
                setMessage("Wait to open e-mail program")
            }else{
                setTitle("Send config to e-mail FAILED")
                setMessage("No any e-mail program installed")
            }
            create()?.show()
        }
    }
    private fun reportMail(configText: String): Boolean {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/rfc822"
        intent.putExtra(Intent.EXTRA_SUBJECT, "GTXX config")
        intent.putExtra(Intent.EXTRA_TEXT, configText)

        if (intent.resolveActivity(packageManager) == null) return false

        startActivity(Intent.createChooser(intent, "Send Email"))
        return true
    }

    private fun checkSU(): Boolean
    {
        if (su.isAlive) return true
        if (su.open()) return true
        setResultValue("Error open SU", "No ADB or su exists!")
        return false
    }

    private fun setResultValue(title: String, value: String)
    {
        runOnUiThread {
            resultPanel?.text = title
            resultTextArea?.text = value
        }
    }
}
