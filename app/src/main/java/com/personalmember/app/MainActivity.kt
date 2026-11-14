package com.VR Investigation Management.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var status: TextView
    private var uploadCallback: ValueCallback<Array<Uri>>? = null

    private val homeUrl =
        "https://script.google.com/macros/s/AKfycbw74L9p6BSivgxWYpKX1wRNSemAbsIX7reU1MbSbVrDvJXhy5kbI4jkxMjEmOAdaCbCaA/exec"

    companion object {
        private const val FILE_CHOOSER_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 10, 12, 10)
            setBackgroundColor(Color.rgb(9, 36, 95))
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.app_logo)
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = "PERSONAL MEMBER logo"
        }
        toolbar.addView(logo, LinearLayout.LayoutParams(42, 42).apply {
            rightMargin = 12
        })

        val title = TextView(this).apply {
            text = "PERSONAL MEMBER"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
        }
        toolbar.addView(title, LinearLayout.LayoutParams(0, 48, 1f))

        val refresh = Button(this).apply {
            text = "↻"
            textSize = 23f
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(25, 67, 145))
            setOnClickListener { webView.reload() }
            contentDescription = "Refresh"
        }
        toolbar.addView(refresh, LinearLayout.LayoutParams(52, 48))

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
        }

        status = TextView(this).apply {
            text = "ইন্টারনেট সংযোগ পরীক্ষা করা হচ্ছে..."
            textSize = 14f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(12, 8, 12, 8)
            visibility = View.GONE
        }

        webView = WebView(this)
        webView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        )

        root.addView(toolbar)
        root.addView(progress, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 5
        ))
        root.addView(status)
        root.addView(webView)
        setContentView(root)

        configureWebView()
        if (savedInstanceState == null) {
            webView.loadUrl(homeUrl)
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    private fun configureWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setSupportMultipleWindows(false)
        settings.allowFileAccess = false
        settings.allowContentAccess = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme?.lowercase() ?: return false

                if (scheme == "tel" || scheme == "mailto" ||
                    scheme == "sms" || scheme == "whatsapp") {
                    openExternal(uri)
                    return true
                }

                if (scheme == "http" || scheme == "https") {
                    return false
                }

                openExternal(uri)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progress.visibility = View.VISIBLE
                progress.progress = 5
                status.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progress.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    status.text = "ইন্টারনেট সংযোগ নেই অথবা পেজ লোড হয়নি। আবার চেষ্টা করুন।"
                    status.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                uploadCallback?.onReceiveValue(null)
                uploadCallback = filePathCallback
                return try {
                    val intent = fileChooserParams?.createIntent()
                        ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST)
                    true
                } catch (e: Exception) {
                    uploadCallback?.onReceiveValue(null)
                    uploadCallback = null
                    Toast.makeText(this@MainActivity, "ফাইল নির্বাচন করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }

        webView.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(this, "ডাউনলোড লিংক খোলা যাচ্ছে না", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openExternal(uri: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: Exception) {
            Toast.makeText(this, "এই লিংক খোলার উপযুক্ত অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Android API; retained for broad compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_REQUEST) {
            val result = if (resultCode == RESULT_OK) {
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            } else null
            uploadCallback?.onReceiveValue(result)
            uploadCallback = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = 14f
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
