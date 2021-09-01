package com.alcachofra.events;

import com.alcachofra.utils.Friend;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OnMessage extends ListenerAdapter {

    private static final String HELP = """
            **-friends add <nome1,nome2,nome3...>** para configurar os participantes.
            **-friends view** para verificar os participantes.
            **-friends number <number>** para configurar quantos amigos secretos cada participante terá.
            **-friends start** para começar.
            """;

    private final Set<Friend> friendSet = new LinkedHashSet<>();
    private int FRIEND_NUM = 0;

    /**
     * 1. Receive bot commands.
     *      Commands with the following format:
     *          -pack ...
     * 2. Receive attachments from users, verify and send to Dropbox.
     *      Files will be of type:
     *          PNG - "image/png"
     *          PROPERTIES - "text/plain; charset=utf-8"
     *
     * @param event event of received message
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) // If author is the bot itself:
            return;
        String message = event.getMessage().getContentRaw();
        String[] words = message.split(" ");
        if (words.length == 0) return;
        if (words[0].equals("-friends")) {
            if (words.length == 1) {
                event.getChannel().sendMessage("Experimenta **-friends help**").queue();
                return;
            }
            switch(words[1]) {
                case "add":
                    for (String name : words[2].split(",")) {
                        friendSet.add(new Friend(name));
                    }
                case "view":
                    StringBuilder sb = new StringBuilder("Participantes configurados: ");
                    for (Friend friend : friendSet) {
                        sb.append(friend.getName()).append(", ");
                    }
                    if (friendSet.isEmpty()) {
                        sb.append("Nenhum");
                    }
                    else {
                        sb.deleteCharAt(sb.length() - 1);
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    event.getChannel().sendMessage(sb.toString()).queue();
                    break;
                case "number":
                    FRIEND_NUM = Integer.parseInt(words[2]);
                    event.getChannel().sendMessage("Cada participante terá " + FRIEND_NUM + " amigos secretos.").queue();
                    break;
                case "start":
                    if (!friendSet.isEmpty()) {
                        if (FRIEND_NUM > 0) {
                            List<Friend> friends = new ArrayList<>(friendSet);
                            do {
                                Friend.randomiseSecretFriends(friends, FRIEND_NUM);
                            }
                            while (!Friend.validateSecretFriends(friends, FRIEND_NUM));

                            // Output secret friends:
                            StringBuilder result = new StringBuilder();
                            for (Friend friend : friends) {
                                result.append("**").append(friend.getName()).append("**\n||");
                                for (int i = 0; i < FRIEND_NUM; i++) {
                                    result.append(friend.getSecretFriends().get(i).getName()).append(", ");
                                }
                                result.deleteCharAt(result.length() - 1);
                                result.deleteCharAt(result.length() - 1);
                                result.append("||\n");
                                result.append("**............................**\n");
                            }
                            event.getChannel().sendMessage(result.toString()).queue();
                            friends.clear();
                            FRIEND_NUM = 0;
                        }
                        else event.getChannel().sendMessage(
                                "Ainda não configuraste o número de amigos secretos para cada participante. Utiliza o comando **-friends help**"
                        ).queue();
                    }
                    else event.getChannel().sendMessage(
                            "Ainda não configuraste os participantes. Utiliza o comando **-friends help**"
                    ).queue();
                    break;
                case "help":
                    event.getChannel().sendMessage(HELP).queue();
                    break;
                default:
                    event.getChannel().sendMessage("Experimenta **-friends help**").queue();
                    break;
            }
        }

    }
}
