package jr.brian.issaaiapp.util

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issaaiapp.R
import jr.brian.issaaiapp.model.local.Chat
import jr.brian.issaaiapp.view.ui.pages.LottieLoading
import jr.brian.issaaiapp.view.ui.theme.AIChatBoxColor
import jr.brian.issaaiapp.view.ui.theme.HumanChatBoxColor
import jr.brian.issaaiapp.view.ui.theme.TextWhite

@Composable
fun ChatHeader(modifier: Modifier, isChatGptTyping: MutableState<Boolean>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (isChatGptTyping.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "ChatGPT is typing",
                    color = MaterialTheme.colors.primary,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                LottieLoading()
            }
        }
        if (isChatGptTyping.value.not()) {
            Text(
                "Issa AI App x ChatGPT",
                color = MaterialTheme.colors.primary,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun ChatSection(modifier: Modifier, chats: MutableList<Chat>, listState: LazyListState) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val copyToastMsgs = listOf(
        "Your copy is ready for pasta!",
        "What are you waiting for? Paste!",
        "Your clipboard has been blessed.",
        "Chat copied!",
        "Copied, the chat has been."
    )
    LazyColumn(modifier = modifier, state = listState) {
        items(chats.size) { index ->
            ChatBox(
                text = chats[index].text,
                senderLabel = chats[index].senderLabel,
                timeStamp = chats[index].timeStamp,
                isHumanChatBox = chats[index].senderLabel == SenderLabel.HUMAN_SENDER_LABEL
            ) {
                clipboardManager.setText(AnnotatedString((chats[index].text)))
                Toast.makeText(
                    context,
                    copyToastMsgs.random(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@Composable
fun ChatTextFieldRow(
    promptText: String,
    conversationalContextText: MutableState<String>,
    sendOnClick: () -> Unit,
    textFieldOnValueChange: (String) -> Unit,
    convoFieldOnValueChange: (String) -> Unit,
    isConversationalContextShowing: MutableState<Boolean>,
    modifier: Modifier,
    textFieldModifier: Modifier,
    convoTextFieldModifier: Modifier,
    iconRowModifier: Modifier,
    sendIconModifier: Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = textFieldModifier,
            value = promptText,
            onValueChange = textFieldOnValueChange,
            label = {
                Text(
                    text = "Enter a prompt for ChatGPT",
                    style = TextStyle(
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.secondary,
                unfocusedIndicatorColor = MaterialTheme.colors.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { sendOnClick() })
        )

        Row(
            modifier = iconRowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_icon),
                tint = MaterialTheme.colors.primary,
                contentDescription = "Send Message",
                modifier = sendIconModifier
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(
                    id = if (isConversationalContextShowing.value)
                        R.drawable.baseline_keyboard_arrow_down_40
                    else R.drawable.baseline_keyboard_arrow_up_40
                ),
                tint = MaterialTheme.colors.primary,
                contentDescription = "Toggle Conversational Context",
                modifier = Modifier.clickable {
                    isConversationalContextShowing.value = !isConversationalContextShowing.value
                }
            )
        }
    }

    if (isConversationalContextShowing.value) {
        OutlinedTextField(
            modifier = convoTextFieldModifier,
            value = conversationalContextText.value,
            onValueChange = convoFieldOnValueChange ,
            label = {
                Text(
                    text = "Enter Conversational Context",
                    style = TextStyle(
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.secondary,
                unfocusedIndicatorColor = MaterialTheme.colors.primary
            ),
        )
    }
}

@Composable
private fun ChatBox(
    text: String,
    senderLabel: String,
    timeStamp: String,
    isHumanChatBox: Boolean,
    onLongCLick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (isHumanChatBox) {
        HumanChatBox(
            focusManager = focusManager,
            text = text,
            senderLabel = senderLabel,
            timeStamp = timeStamp,
            onLongCLick = onLongCLick
        )
    } else {
        AIChatBox(
            focusManager = focusManager,
            text = text,
            senderLabel = senderLabel,
            timeStamp = timeStamp,
            onLongCLick = onLongCLick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AIChatBox(
    focusManager: FocusManager,
    text: String,
    senderLabel: String,
    timeStamp: String,
    onLongCLick: () -> Unit
) {
    val color = AIChatBoxColor
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(color)
                .weight(.8f)
                .combinedClickable(
                    onClick = { focusManager.clearFocus() },
                    onLongClick = { onLongCLick() },
                    onDoubleClick = {},
                )
        ) {
            Text(
                text,
                style = TextStyle(color = TextWhite),
                modifier = Modifier.padding(15.dp),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(.2f)
        ) {
            val contextLabel = ChatConfig.randomChatGptAdjectiveLabel
            if (senderLabel != SenderLabel.GREETING_SENDER_LABEL && contextLabel.isNotEmpty()) {
                Text(
                    contextLabel,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }
            Text(
                senderLabel,
                style = senderAndTimeStyle(color)
            )
            Text(
                timeStamp,
                style = senderAndTimeStyle(color)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HumanChatBox(
    focusManager: FocusManager,
    text: String,
    senderLabel: String,
    timeStamp: String,
    onLongCLick: () -> Unit
) {
    val color = HumanChatBoxColor
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(.2f)
        ) {
            Text(
                senderLabel,
                style = senderAndTimeStyle(color),
            )
            Text(
                timeStamp,
                style = senderAndTimeStyle(color),
            )
        }
        Box(
            modifier = Modifier
                .weight(.8f)
                .fillMaxWidth()
                .padding(10.dp)
                .background(color)
                .combinedClickable(
                    onClick = { focusManager.clearFocus() },
                    onLongClick = { onLongCLick() },
                    onDoubleClick = {},
                )
        ) {
            Text(
                text,
                style = TextStyle(color = TextWhite),
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

private fun senderAndTimeStyle(color: Color) = TextStyle(
    fontSize = 15.sp,
    fontWeight = FontWeight.Bold,
    color = color
)

object SenderLabel {
    const val HUMAN_SENDER_LABEL = "Me"
    const val CHATGPT_SENDER_LABEL = "ChatGPT"
    const val GREETING_SENDER_LABEL = "Greetings"
}

object ChatConfig {
    const val GPT_3_5_TURBO = "gpt-3.5-turbo"

    private val aiAdjectives = listOf(
        "Sarcastic",
        "Helpful",
        "Unhelpful",
        "Optimistic",
        "Pessimistic",
        "Excited",
        "Joyful",
        "Charming",
        "Inspiring",
        "Nonchalant",
        "Relaxed",
        "Loud",
        "Witty"
    )

    val randomChatGptAdjective = aiAdjectives.random()
    var randomChatGptAdjectiveLabel = "( $randomChatGptAdjective )"

    val conversationalContext = listOf(
        "Be as ${randomChatGptAdjective.lowercase()} as possible.",
        "You are my ${randomChatGptAdjective.lowercase()} assistant",
        "Play the role of the ${randomChatGptAdjective.lowercase()} bot",
        "Act as if you are the ${randomChatGptAdjective.lowercase()} cat"
    )

    val greetings = listOf(
        "What's good my boy?",
        "You? Again? \uD83D\uDE43", // Upside down face emoji
        "How are you doing today?",
        "How may I help you today?",
        "Assuhh dude \uD83D\uDE0E", // Cool emoji; black shades
        "Hi Human."
    )
}