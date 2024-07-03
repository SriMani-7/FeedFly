package srimani7.apps.feedfly.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Html
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat


// https://developer.android.com/reference/android/text/style/package-summary

fun SpannableStringBuilder.toAnnotatedString(fontSize: TextUnit = 12.sp): AnnotatedString {
    val builder = AnnotatedString.Builder(toString())

    getSpans(0, length, Object::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        val style: Any = when (span) {
            is URLSpan -> {
                builder.addStringAnnotation("URL", span.url, start, end)
                SpanStyle(textDecoration = TextDecoration.Underline)
            }
            is AlignmentSpan -> {
                ParagraphStyle(
                    textAlign = when (span.alignment) {
                        Layout.Alignment.ALIGN_NORMAL -> TextAlign.Start
                        Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
                        Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
                        null -> TextAlign.Start
                    }
                )
            }

            is RelativeSizeSpan -> SpanStyle(
                fontSize = fontSize * span.sizeChange
            )

            is StyleSpan -> {
                val isItalic = when (span.style) {
                    Typeface.ITALIC, Typeface.BOLD_ITALIC -> true
                    else -> false
                }
                SpanStyle(
                    fontWeight = when (span.style) {
                        Typeface.BOLD, Typeface.BOLD_ITALIC -> FontWeight.Medium
                        Typeface.NORMAL -> FontWeight.Normal
                        else -> FontWeight.Normal
                    }, fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                )
            }

            is ImageSpan -> {
                span.source?.let {
                    builder.addStringAnnotation("IMAGE", it, start, end)
                }
                Unit
            }
            else -> Unit
        }
        when(style) {
            is SpanStyle -> builder.addStyle(style, start, end)
            is ParagraphStyle -> builder.addStyle(style, start, end)
            else -> builder.addStyle(SpanStyle(), start, end)
        }

    }
    return builder.toAnnotatedString()
}


fun fromHtml(
    html: String,
    imageGetter: Html.ImageGetter,
): Spanned = HtmlCompat.fromHtml(
    html,
    HtmlCompat.FROM_HTML_MODE_LEGACY or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
    imageGetter, null
)

fun shareText(text: String, context: Context) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, text)
    sendIntent.type = "text/plain"

    val shareIntent = Intent.createChooser(sendIntent, "Share link")
    ContextCompat.startActivity(context, shareIntent, ActivityOptionsCompat.makeBasic().toBundle())
}