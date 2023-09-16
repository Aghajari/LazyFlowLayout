package com.aghajari.flexboxtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aghajari.compose.flexbox.FlexDirection
import com.aghajari.compose.flexbox.Flexbox
import com.aghajari.flexboxtest.ui.theme.ComposeFlexboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeFlexboxTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateBox()
                }
            }
        }
    }
}

@Composable
fun CreateBox(modifier: Modifier = Modifier) {
    var counter by remember { mutableStateOf(0) }

    val texts = remember {
        mutableStateListOf(*randomWords)
    }

    Flexbox(
        flexDirection = FlexDirection.Row,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        items(
            texts,
            key = { texts[it] }
        ) {
            RemovableItem(it) {
                texts.remove(it)
            }
        }
        item(Unit) {
            AddIcon {
                texts.add("${randomWords.random()} ${++counter}")
            }
        }
    }
}

@Composable
fun RemovableItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
    ) {
        Text(
            text = text,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AddIcon(
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = R.drawable.add),
        contentDescription = "Add",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    ComposeFlexboxTheme {
        CreateBox(Modifier.fillMaxWidth())
    }
}

val randomWords = arrayOf(
    "Hello",
    "World",
    "Test",
    "A",
    "B",
    "C",
    "D",
    "Flexbox",
    "Oops",
    "Awesome",
    "E"
)