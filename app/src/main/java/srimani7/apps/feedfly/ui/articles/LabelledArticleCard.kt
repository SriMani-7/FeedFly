package srimani7.apps.feedfly.ui.articles

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.ui.fromHtml
import srimani7.apps.rssparser.DateParser
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LabelledArticleCard(
    labelledArticle: LabelledArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(labelledArticle.publishedTime) ?: "",
    onLongClick: (Long) -> Unit,
    onOptionClick: (String) -> Unit,
    onChangeArticleLabel: (Long, Long?) -> Unit
) {
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var showOptions by rememberSaveable { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.combinedClickable(onLongClick = {
            onLongClick(labelledArticle.articleId)
        }, onClick = { scope.launch { articleModalState.partialExpand() }}),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column {
            if (descriptionUri != null && !labelledArticle.isImage) {
                ArticleImage(descriptionUri!!)

            } else if (labelledArticle.mediaType != null && labelledArticle.mediaSrc != null) {
                ArticleMediaHeader(labelledArticle.mediaType!!, labelledArticle.mediaSrc!!)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 16.dp)
            ) {
                ArticleTitle(title = labelledArticle.title)
                Text(
                    text = pubTime,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light
                )
            }

            if (descriptionUri == null && !labelledArticle.isImage) Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
            ) {
                ArticleDescription(description = labelledArticle.description)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onChangeArticleLabel(
                            labelledArticle.articleId,
                            labelledArticle.labelId
                        )
                    }, modifier = Modifier.padding(4.dp), colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_label_24),
                        null,
                        modifier = Modifier.size(
                            ButtonDefaults.IconSize
                        )
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = labelledArticle.label ?: "Add label")
                }

                IconButton(onClick = { showOptions = true }) {
                    Icon(Icons.Filled.MoreVert, null)
                }
            }
            if(showOptions) Popup(
                alignment = Alignment.BottomEnd,
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                ), onDismissRequest = { showOptions = false }
            ) {
                Surface(
                    shadowElevation = 6.dp,
                    tonalElevation = 1.5.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                        IconButton(onClick = { onOptionClick("private" )}) {
                            Icon(Icons.Outlined.Lock, null)
                        }
                        IconButton(onClick = { onOptionClick("delete") }) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                        IconButton(onClick = { showOptions = false }) {
                            Icon(Icons.Filled.Clear, null)
                        }
                    }
                }
            }

        }
        if (articleModalState.isVisible) {
            ArticleViewScreen(
                description = labelledArticle.description,
                link = labelledArticle.articleLink,
                sheetState = articleModalState,
                onDismiss = {
                    scope.launch { articleModalState.hide() }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        fromHtml(labelledArticle.description ?: "") {
            if (descriptionUri == null) descriptionUri = it
            null
        }.toString()
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun RssItemCardPreview() {
    TheSecretDairyTheme(true) {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(8.dp, 16.dp),
            ) {
                items(1) {
                    LabelledArticleCard(labelledArticle = LabelledArticle(
                        articleId = 2,
                        title = "Can Movie The Conclusion beat the first part collections in pakistan",
                        description = "Movie, the conclusion most Most anticipated movie in the universe releases today Let's see if it breaks the records of Movie, the beginning movie in the Pakistan",
                        articleLink = "",
                        publishedTime = Date(),
                        mediaType = null,
                        mediaSrc = null,
                        label = "Review",
                        labelId = null
                    ), onLongClick = {}, onOptionClick = {}, onChangeArticleLabel = { _, _ -> })
                }
            }

        }
    }
}