package com.sameeraw.remindbuddy.ui.home.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sameeraw.remindbuddy.data.entity.Reminder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListItem(reminder: Reminder) {

    ConstraintLayout(modifier = Modifier
        .clickable {

        }
        .fillMaxWidth()) {
        val (divider, paymentTitle, paymentCategory, icon, date) = createRefs()

        Divider(
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            }
        )
        Text(text = reminder.content,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(paymentTitle) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent

            })
        //category
        Text(text = reminder.category,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(paymentCategory) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 8.dp,
                    bias = 0f
                )
                top.linkTo(paymentTitle.bottom, margin = 6.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
                width = Dimension.preferredWrapContent

            })

        //Date
        Text(
            text = when {
                reminder.date != null -> {
                    reminder.date.formatToString()
                }
                else -> Date().formatToString()
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date) {
                linkTo(
                    start = paymentCategory.end,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp
                )
                top.linkTo(paymentTitle.bottom, margin = 6.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
            }
        )
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .size(50.dp)
            .padding(6.dp)
            .constrainAs(icon) {
                top.linkTo(parent.top, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                end.linkTo(parent.end)
            }) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Check")

        }
    }
}

private fun Date.formatToString(): String {
    return SimpleDateFormat("MM dd,yyyy", Locale.getDefault()).format(this)
}