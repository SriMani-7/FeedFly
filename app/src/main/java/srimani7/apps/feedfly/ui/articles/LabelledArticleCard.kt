package srimani7.apps.feedfly.ui.articles

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.LabelledArticle
import srimani7.apps.feedfly.ui.fromHtml
import srimani7.apps.rssparser.DateParser
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelledArticleCard(
    labelledArticle: LabelledArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(labelledArticle.publishedTime) ?: "",
    onChangeArticleLabel: (Long, Long?) -> Unit
) {
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Surface(
        onClick = { scope.launch { articleModalState.partialExpand() } },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column {
            if (descriptionUri != null && !labelledArticle.isImage) {
                ArticleImage(descriptionUri!!)

            } else if (labelledArticle.mediaType != null && labelledArticle.mediaSrc != null) {
                ArticleMediaHeader(labelledArticle.mediaType!!, labelledArticle.mediaSrc!!, {})
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 16.dp)
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
                    .padding(horizontal = 14.dp),
            ) {
                ArticleDescription(description = labelledArticle.description)
            }

            TextButton(onClick = {
                onChangeArticleLabel(
                    labelledArticle.articleId,
                    labelledArticle.labelId
                )
            }, modifier = Modifier.padding(4.dp)) {
                Icon(
                    painterResource(R.drawable.baseline_label_24), null, modifier = Modifier.size(
                        ButtonDefaults.IconSize
                    )
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = labelledArticle.label ?: "Add label")
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
            if(descriptionUri == null) descriptionUri = it
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
                items(3) {
                    LabelledArticleCard(labelledArticle = LabelledArticle(
                        articleId = 2,
                        title = "Can Bahubali The Conclusion beat the first part collections in pakistan",
                        description = "Baahubali, the conclusion most Most anticipated movie in the universe releases today Let's see if it breaks the records of Bahubali, the beginning movie in the Pakistan",
                        articleLink = "",
                        publishedTime = Date(),
                        mediaType = null,
                        mediaSrc = null,
                        label = "Review",
                        labelId = null
                    ), onChangeArticleLabel = { _, _ -> })
                }
            }

        }
    }
}