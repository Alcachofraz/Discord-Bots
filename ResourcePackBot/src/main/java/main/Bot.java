package main;

import dropbox.DropBoxUtils;
import events.OnMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    public static final String TOKEN = "Your Discord Developer Token";

    private static JDA jda;

    public static void main(String[] args) {

        // Initialise bot:
        DropBoxUtils.build();
        try {
            jda = JDABuilder.createDefault(TOKEN).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        // Bot Listeners:
        jda.addEventListener(new OnMessage());
    }
}
