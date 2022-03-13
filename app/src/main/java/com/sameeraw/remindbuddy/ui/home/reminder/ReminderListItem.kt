package com.sameeraw.remindbuddy.ui.home.reminder

import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sameeraw.remindbuddy.data.entity.Reminder
import com.skydoves.landscapist.glide.GlideImage
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ReminderListItem(
    reminder: Reminder, onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val reminderIcons = listOf<ImageVector>(
        Icons.Default.SupervisedUserCircle,
        Icons.Default.House,
        Icons.Default.CarRepair,
        Icons.Default.TravelExplore,
        Icons.Default.Medication,
        Icons.Default.Train,
        Icons.Default.Language,
        Icons.Default.Book,
        Icons.Default.Phone
    )


    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .clickable {
                onItemClick()
            }
            .fillMaxWidth()
            .height(80.dp),
    ) {
        val (divider, reminderTitle, reminderDescription, reminderDate, reminderIcon, reminderImage, deleteIcon, voice) = createRefs()
        Divider(
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            }
        )

        if (reminder.imageURL != null) {
            GlideImage(
                imageModel = Uri.parse(reminder.imageURL),
                Modifier
                    .fillMaxWidth()
                    .height(80.dp), alpha = 0.2f
            )
        }

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .size(40.dp)
            .padding(6.dp)
            .constrainAs(reminderIcon) {
                top.linkTo(parent.top, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
            }) {
            Icon(imageVector = reminderIcons.first {
                it.name == reminder.icon
            }, contentDescription = "Check", modifier = Modifier.size(50.dp))
        }

        Text(text = "${if (reminder.reminderSeen) "[DONE]" else ""}${reminder.title}",
            maxLines = 1,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.constrainAs(reminderTitle) {
                start.linkTo(reminderIcon.end)
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            })

        Text(text = reminder.message,
            maxLines = 1,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(reminderDescription) {
                top.linkTo(parent.top, margin = 15.dp)
                bottom.linkTo(reminderTitle.bottom)
                start.linkTo(reminderTitle.end, margin = 5.dp)
                width = Dimension.preferredWrapContent
            })
        //Date
        Text(
            text = when {
                reminder.reminderTime != null -> "On " + reminder.reminderTime.formatToString() + " At, " + reminder.reminderTime.formatToTimeString()
                else -> ""
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(reminderDate) {
                top.linkTo(reminderTitle.bottom)
                bottom.linkTo(parent.bottom, margin = 5.dp)
                start.linkTo(reminderIcon.end)
            }
        )

        IconButton(onClick = {


            var mTTS: TextToSpeech? = null
            mTTS = TextToSpeech(context, TextToSpeech.OnInitListener { i ->
                if (i == TextToSpeech.SUCCESS) {

                    val result = mTTS!!.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        Log.e("TTS", "Language Not Supported")
                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mTTS!!.speak(
                                reminder.title + " " + reminder.message,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                ""
                            )

                        } else {
                            @Suppress("DEPRECATION")
                            mTTS!!.speak(
                                reminder.title + " " + reminder.message,
                                TextToSpeech.QUEUE_FLUSH,
                                null
                            )
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization Failed")
                }
            })

        }, modifier = Modifier
            .size(50.dp)
            .padding(6.dp)
            .constrainAs(voice) {
                top.linkTo(parent.top, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                end.linkTo(deleteIcon.start)
            }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Check",
                tint = Color.Red
            )
        }

        IconButton(onClick = { onDeleteClick() }, modifier = Modifier
            .size(50.dp)
            .padding(6.dp)
            .constrainAs(deleteIcon) {
                top.linkTo(parent.top, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                end.linkTo(parent.end)
            }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Check", tint = Color.Red)
        }
    }
}

private fun Date.formatToString(): String {
    return SimpleDateFormat("MM dd,yyyy", Locale.getDefault()).format(this)
}

private fun Long.formatToString(): String {
    return SimpleDateFormat("MM dd,yyyy", Locale.getDefault()).format(this)
}

private fun Long.formatToTimeString(): String {
    return SimpleDateFormat("kk:mm", Locale.getDefault()).format(this)
}

