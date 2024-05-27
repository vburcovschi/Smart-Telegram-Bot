package com.javarush.telegram;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "javarushtinder_bot"; //TODO: add the bot's name in quotes
    public static final String TELEGRAM_BOT_TOKEN = "7052624324:AAHGZXE1HnKBjzsT39ooEyHkII8fbLK1s84"; //TODO: add the bot token in quotes
    public static final String OPEN_AI_TOKEN = "sk-proj-BuYRH3kZaiA72RRo7CpWT3BlbkFJ97IWafSLtkvofOCodCsM"; //TODO: add the ChatGPT token in quotes

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);

    private DialogMode currentMode = null;

    private ArrayList<String> list = new ArrayList<>();

    private UserInfo me;
    private UserInfo she;
    private  int questionsCount;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();
        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);
            showMainMenu("Bot main menu","/start",
            "Tinder profile generation 😎","/profile",
            "Message for dating \uD83E\uDD70","/opener",
            "Correspondence on your behalf\uD83D\uDE08","/message",
            "Correspondence with the stars \uD83D\uDD25","/date",
            "Ask a question to GPT chat \uD83E\uDDE0","/gpt");
            return;
        }

        if (message.equals("/profile")){
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");
            me = new UserInfo();
            questionsCount = 1;
            sendTextMessage("How old are you?");
            return;
        }

        if (currentMode == DialogMode.PROFILE && !isMessageCommand()){
            switch (questionsCount){
                case 1:
                    me.age = message;
                    questionsCount = 2;
                    sendTextMessage("What is your occupation?");
                    return;
                case 2:
                    me.occupation = message;
                    questionsCount = 3;
                    sendTextMessage("What is your hobby?");
                    return;
                case 3:
                    me.hobby = message;
                    questionsCount = 4;
                    sendTextMessage("What do you not like in people?");
                    return;
                case 4:
                    me.annoys = message;
                    questionsCount = 5;
                    sendTextMessage("What is the purpose of dating?");
                    return;
                case 5:
                    me.goals = message;
                    String aboutMyself = me.toString();
                    Message msg = sendTextMessage("Wait a second, chatGPT \uD83E\uDDE0 is thinking...");
                    String prompt = loadPrompt("profile");
                    String answer = chatGPT.sendMessage(prompt,aboutMyself);
                    updateTextMessage(msg,answer);
                    return;
            }
        }

        if (message.equals("/opener")){
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");
            she = new UserInfo();
            questionsCount  = 1;
            sendTextMessage("What is girl name?");
            return;
        }

        if (currentMode == DialogMode.OPENER && !isMessageCommand()){
            switch (questionsCount) {
                case 1:
                    she.name = message;
                    questionsCount = 2;
                    sendTextMessage("How old is she?");
                    return;
                case 2:
                    she.age = message;
                    questionsCount = 3;
                    sendTextMessage("What is her hobby?");
                    return;
                case 3:
                    she.hobby = message;
                    questionsCount = 4;
                    sendTextMessage("What do her occupation?");
                    return;
                case 4:
                    she.occupation = message;
                    questionsCount = 5;
                    sendTextMessage("What is the purpose of dating?");
                    return;
                case 5:
                    she.goals = message;
                    String aboutFriend = she.toString();
                    String prompt = loadPrompt("opener");
                    Message msg = sendTextMessage("Wait a second, chatGPT \uD83E\uDDE0 is thinking...");
                    String answer = chatGPT.sendMessage(prompt, aboutFriend);
                    updateTextMessage(msg,answer);
                    return;
            }
            return;
        }

        if (message.equals("/message")){
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Put in the chat the messages",
                    "Next Message","message_next",
                    "Invite you on a date","message_date");
            return;
        }

        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()){
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")){
                String prompt = loadPrompt(query);
                String userChatMessage = String.join("\n\n",list);
                Message msg = sendTextMessage("Wait a second, the girl is texting...");
                String answer = chatGPT.sendMessage(prompt, userChatMessage);
                updateTextMessage(msg, answer);
                return;
            }
            list.add(message);
            return;
        }

        if (message.equals("/date")){
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text,
                    "Ariadna Grande","date_grande",
                    "Margo Robbie","date_robbie",
                    "Zendaya","date_zendaya",
                    "Rain Gosling","date_gosling",
                    "Tom Hardy","date_hardy");
            return;
        }

        if (currentMode == DialogMode.DATE && !isMessageCommand()){
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")){
                sendPhotoMessage(query);
                sendTextMessage("Good choice. You have to invite girl/boy ❤\uFE0F to date by 5 messages");
                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }
            String answer = chatGPT.addMessage(message);
            sendTextMessage(answer);
            return;
        }

        if (message.equals("/gpt")){
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            sendTextMessage("Ask ChatGPT:");
            return;
        }

        if (currentMode == DialogMode.GPT && !isMessageCommand()){
            String prompt = loadPrompt("gpt");
            Message msg = sendTextMessage("Wait a second, chatGPT \uD83E\uDDE0 is thinking...");
            String answer = chatGPT.sendMessage(prompt ,message);
            updateTextMessage(msg, answer);
            return;
        }

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
