package srimani7.apps.feedfly.ui.articles

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import kotlinx.coroutines.launch
import srimani7.apps.feedfly.R
import srimani7.apps.feedfly.core.design.TheSecretDairyTheme
import srimani7.apps.feedfly.core.model.PrivateArticle
import srimani7.apps.feedfly.ui.fromHtml
import srimani7.apps.rssparser.DateParser
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateArticleCard(
    article: PrivateArticle,
    modifier: Modifier = Modifier,
    pubTime: String = DateParser.formatTime(article.publishedTime) ?: "",
    onUnLock: (Long) -> Unit
) {
    val articleModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var descriptionUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = {
            scope.launch { articleModalState.partialExpand() }
        }
    ) {
        Box {
            Column {
                if (descriptionUri != null && !article.isImage) {
                    ArticleImage(descriptionUri!!)

                } else if (article.mediaType != null && article.mediaSrc != null) {
                    ArticleMediaHeader(article.mediaType!!, article.mediaSrc!!)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 16.dp)
                ) {
                    ArticleTitle(title = article.title)
                    Text(
                        text = pubTime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light
                    )
                }

                if (descriptionUri == null && !article.isImage) Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    ArticleDescription(description = article.description)
                }
            }
            UnLockButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onUnLock(article.articleId) })
        }

        if (articleModalState.isVisible) {
            ArticleViewScreen(
                description = article.description,
                link = article.articleLink,
                sheetState = articleModalState,
                onDismiss = {
                    scope.launch { articleModalState.hide() }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        fromHtml(article.description ?: "") {
            if (descriptionUri == null) descriptionUri = it
            null
        }.toString()
    }
}

@Composable
fun UnLockButton(modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(bottomStartPercent = 95),
        color = IconButtonDefaults.filledTonalIconButtonColors().containerColor,
        modifier = modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                painterResource(R.drawable.rounded_lock_open_right_24), null, modifier = Modifier
                    .padding(5.dp)
                    .size(22.dp)
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun PrivateArticleCardPreview() {
    TheSecretDairyTheme(true) {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 15.dp),
            ) {
                items(3) {
                    PrivateArticleCard(article = PrivateArticle(
                        articleId = 2,
                        title = "Can Bahubali The Conclusion beat the first part collections in pakistan",
                        description = "Baahubali, the conclusion most Most anticipated movie in the universe releases today Let's see if it breaks the records of Bahubali, the beginning movie in the Pakistan",
                        articleLink = "",
                        publishedTime = Date(),
                        mediaType = "image",
                        mediaSrc = "https",
                        label = "Review",
                        feedTitle = "Comedy, News, Games",
                        feedId = 2
                    ), onUnLock = {})
                }
            }

        }
    }
}