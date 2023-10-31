package com.aghajari.compose.flexbox

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Preview(
    backgroundColor = 0xFFFFFFFF,
    showBackground = true
)
@Composable
private fun PreviewLazyFlowColumn(
    @PreviewParameter(FlowLayoutPreviewParamProvider::class)
    param: FlowLayoutPreviewParam
) {

    LazyFlowColumn(
        modifier = Modifier
            .padding(16.dp)
            .height(200.dp),
        content = param,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterVertically
        ),
        itemInlineAlignment = Alignment.Center
    )
}

@Preview(
    backgroundColor = 0xFFFFFFFF,
    showBackground = true
)
@Composable
private fun PreviewLazyFlowRow(
    @PreviewParameter(FlowLayoutPreviewParamProvider::class)
    param: FlowLayoutPreviewParam
) {

    LazyFlowRow(
        modifier = Modifier
            .padding(16.dp)
            .width(400.dp),
        content = param,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterVertically
        ),
        itemInlineAlignment = Alignment.Center
    )
}

@Composable
private fun SampleItem(text: String) {
    Text(
        text = text,
        maxLines = 2,
        textAlign = TextAlign.Center,
        color = Color.Black,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
    )
}

private typealias FlowLayoutPreviewParam = LazyFlowLayoutScope.() -> Unit

private class FlowLayoutPreviewParamProvider : PreviewParameterProvider<FlowLayoutPreviewParam> {
    override val values: Sequence<FlowLayoutPreviewParam> = sequenceOf(
        complexSample(),
        {
            items(2) {
                SampleItem("Row $it")
            }
        },
        complexSample(),
        {
            items(2) {
                SampleItem("Column $it")
            }
        }
    )

    private fun complexSample(): LazyFlowLayoutScope.() -> Unit {
        return {
            items(2) {
                SampleItem(" Hello \n$it")
            }
            item {
                SampleItem("ABCD")
            }

            val texts = listOf(
                "E", "F", "G", "H", "I", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y"
            )
            items(texts) {
                SampleItem(it)
            }
        }
    }
}