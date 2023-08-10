package srimani7.apps.feedfly.ui

import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

fun SpannableStringBuilder.toAnnotatedString(): AnnotatedString {
    this.getSpans(0, length, ImageSpan::class.java).forEach {
        delete(getSpanStart(it), getSpanEnd(it))
    }
    val builder = AnnotatedString.Builder(toString())

    getSpans(0, length, Object::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is URLSpan -> {
                builder.fromURLSpan(span, start, end)
            }
        }
    }
    return builder.toAnnotatedString()
}


fun AnnotatedString.Builder.fromURLSpan(urlSpan: URLSpan, start: Int, end: Int) {
    addStringAnnotation("URL", urlSpan.url, start, end)
    addStyle(
        style = SpanStyle(textDecoration = TextDecoration.Underline),
        start = start, end
    )
}

fun fromHtml(
    html: String,
    imageGetter: Html.ImageGetter,
): Spanned = HtmlCompat.fromHtml(
    html,
    HtmlCompat.FROM_HTML_MODE_LEGACY or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
    imageGetter, null
)