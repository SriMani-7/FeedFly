package srimani7.apps.feedfly.core.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import srimani7.apps.feedfly.core.ui.util.openInBrowser

@SuppressLint("ViewConstructor")
class DescriptionWebView(context: Context, description: String): WebView(context) {
    init {
        webChromeClient = WebChromeClient()
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let { openInBrowser(it, context) }
                return true
            }
        }
        settings.apply {
            allowFileAccess = false
            setSupportZoom(false)
            displayZoomControls = false
            javaScriptEnabled = false
            allowContentAccess = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        scrollBarStyle = SCROLLBARS_OUTSIDE_OVERLAY
        isScrollbarFadingEnabled = false
        load(description)
    }

    private fun load(description: String) {
        val html = """
        <!DOCTYPE html>
        <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Description</title>
                <style>
                   
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        border: 1px solid #ccc;
                        margin-bottom: 20px;
                    }

                    th, td {
                        border: 1px solid #ccc;
                        padding: 8px;
                        text-align: left;
                    }

                    * {
                        box-sizing: border-box;
                    }

                    h1, h2, h3, h4, h5, h6 {
                        font-family: "Helvetica Neue", Arial, sans-serif;
                        font-weight: bold;
                        margin-bottom: 10px;
                    }

                    img {
                        max-width: 100%;
                        height: auto;
                        margin: 0;
                        padding: 0;
                    }
                    
                    #desc-container {
                        padding-bottom: 16px
                    }
                </style>
            </head>
            <body>
                <section id="desc-container">${description.replace(Regex("style\\s*=\\s*\"[^\"]*\""), "")}</section>
            </body>
        </html>
    """.trimIndent()
        loadData(Uri.encode(html), "text/html", "utf-8")
    }
}

