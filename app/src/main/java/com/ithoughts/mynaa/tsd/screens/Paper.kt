package com.ithoughts.mynaa.tsd.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ithoughts.mynaa.tsd.ui.theme.dairyWriterStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Preview(showSystemUi = true)
@Composable
fun Paper() {
    val context = LocalContext.current
    var date by remember { mutableStateOf("12 May 2016") }
    var textFieldValue by remember { mutableStateOf("Type Something\nscepters") }
    Scaffold(
        topBar = {
            DisplayDate()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
//            LinedPaperTextField(
//                textFieldValue = textFieldValue,
//                onValueChange = { textFieldValue = it })
            AndroidView(factory = ::LinedEditText, modifier = Modifier.fillMaxSize()) {

            }
        }
    }

    LaunchedEffect(Unit) {
        val simpleDateFormat = SimpleDateFormat.getDateInstance()
        date = simpleDateFormat.format(Date())
    }

}

@Composable
fun DisplayDate() {
    var day by remember { mutableStateOf("Monday") }
    var date by remember { mutableStateOf("11") }
    var month by remember { mutableStateOf("May") }

    Column {
        Text(text = day)
        Text(text = date, style = MaterialTheme.typography.headlineSmall)
        Text(text = month)
    }

    LaunchedEffect(Unit) {
        val simpleDateFormat = SimpleDateFormat.getDateInstance()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        day = dateFormat.format(calendar.time)
        dateFormat.applyPattern("dd")
        date = dateFormat.format(calendar.time)
        dateFormat.applyPattern("MMMM")
        month = dateFormat.format(calendar.time)
    }
}

@Composable
fun LinedPaperTextField(
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Type something",
    textStyle: TextStyle = dairyWriterStyle,
    lineHeight: Dp = 34.dp
) {
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = onValueChange,
//            placeholder = {
//                Text(text = placeholder, style = textStyle, fontWeight = FontWeight.Light)
//            },
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = lineHeight)
                .drawWithContent {
                    val lineSpacing = lineHeight.toPx()
                    val startY = lineHeight.toPx()
                    val endY = size.height
                    var y = startY
                    while (y <= endY) {
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += lineSpacing
                    }
                    drawContent()
                }
        )
    }
}

class LinedEditText(context: Context) :
    EditText(context) {
    private val mRect: Rect = Rect()
    private val mPaint: Paint = Paint()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = android.graphics.Color.GRAY
        setText("type something\nsecrets")
    }

    override fun onDraw(canvas: Canvas) {
        val count = lineCount
        val r: Rect = mRect
        val paint: Paint = mPaint
        /*
             * Draws one line in the rectangle for every line of text in the EditText
             */for (i in 0 until count) {
            // Gets the baseline coordinates for the current line of text
            val baseline = getLineBounds(i, r)
            /*
                 * Draws a line in the background from the left of the rectangle to the right,
                 * at a vertical position one dip below the baseline, using the "paint" object
                 * for details.
                 */canvas.drawLine(
                r.left.toFloat(), (baseline + 1).toFloat(),
                r.right.toFloat(), (baseline + 1).toFloat(), paint
            )
        }
        // Finishes up by calling the parent method
        super.onDraw(canvas)
    }
}

@Composable
fun LinedEditText() {
    var text by remember { mutableStateOf("Type something\nsecrets") }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
        val count = text.lineSequence().count()
        val r = Rect()
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = android.graphics.Color.GRAY
        }

//        withDensity(LocalDensity.current) {
//            val lineHeightPx = lineHeight.toPx()
//
//            for (i in 0 until count) {
//                val baseline = i * lineSpacing.toPx() + lineHeightPx
//                drawLine(
//                    color = paint.color,
//                    start = Offset(0f, baseline),
//                    end = Offset(size.width, baseline),
//                    strokeWidth = paint.strokeWidth
//                )
//            }
//        }
    })

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxSize(),
        textStyle = TextStyle.Default.copy(fontSize = 18.sp),
        singleLine = false,
        onTextLayout = {
            
        }
    )
}
