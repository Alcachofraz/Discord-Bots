package events;

import dropbox.DropBoxUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class OnMessage extends ListenerAdapter {

    private static String CHANNEL_ID = "0";
    private static final String PACK_NAME = "UniPack";
    private static final String DROPBOX_PATH = "/" + PACK_NAME + "/assets/minecraft/optifine/cit/";
    private static final String HELP = "**-> Comandos:**\n" +
            "       **-pack here** para escolher um canal onde serão depositadas as texturas customizadas.\n" +
            "       **-pack psh <enable/disable>** para ativar ou desativar a mensagem **Psh** sempre que alguém tentar falar no canal das texturas.\n" +
            "       **-pack delete <fileName>** para eliminar todos os ficheiros com o nome <fileName>.\n" +
            "\n" +
            "**-> Que ficheiros enviar?**\n" +
            "       Para adicionar texturas costumizadas, tens de enviar um ficheiro **.png** (16x16) com a tua textura desenhada, e um ficheiro **.properties**.\n" +
            "       Por exemplo, se quiseres que uma espada de diamante chamada ambrósia tenha o aspeto que desenhaste em **ambrósia.png**, o ficheiro **ambrósia.properties** terá de ter o seguinte conteúdo:\n" +
            "       **.   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .**\n" +
            "       *type=item*\n" +
            "       *matchItems=minecraft:diamond_sword*\n" +
            "       *texture=ambrósia.png*\n" +
            "       *nbt.display.Name=pattern:ambrósia*\n" +
            "       **.   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .   .**\n" +
            "       Se és burro e não percebeste, vê este vídeo:\n" +
            "       https://www.youtube.com/watch?v=zVtwfA77o0c&ab_channel=UncleJam";

    private boolean pshEnabled = true;

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

        List<Message.Attachment> attachments = event.getMessage().getAttachments(); // Get attachments:

        if (!attachments.isEmpty()) { // If message has attachments:
             if (isChannelIdSet() && event.getChannel().getId().equals(CHANNEL_ID) && verifyAttachments(event, attachments)) {
                for (Message.Attachment attachment : attachments) {
                    sendAttachment(attachment, DROPBOX_PATH);
                    event.getChannel().sendMessage("**" + attachment.getFileName() + "** foi adicionado.").queue();
                }

            }
        }
        else handleCommand(event, event.getMessage().getContentRaw());
    }

    /**
     * Handle commands of format "-pack ..."
     * @param event Event of received message.
     * @param message Received String message.
     */
    private void handleCommand(MessageReceivedEvent event, String message) {
        String[] words = message.split(" ");
        System.out.println(message);
        if (words.length == 0) return;
        if (words[0].equals("-pack")) {
            if (words.length == 1) {
                event.getChannel().sendMessage("Comando inválido. Tenta **-pack help** para veres os comandos existentes.").queue();
                return;
            }
            switch(words[1]) {
                case "help": // -pack help
                    event.getChannel().sendMessage(HELP).queue();

                    break;
                case "here":
                    CHANNEL_ID = event.getChannel().getId();
                    event.getChannel().sendMessage("Estou pronto para receber texturas customizadas neste canal! Não te esqueças, envia-me o ficheiro **.png** e o ficheiro **.properties** correspondente a cada textura.").queue();
                    break;
                case "psh": // -pack psh <enable/disable>
                    if (words.length == 3) {
                        switch (words[2]) {
                            case "enable":
                                pshEnabled = true;
                                event.getChannel().sendMessage("**Psh** ativado.").queue();
                                break;
                            case "disable":
                                pshEnabled = false;
                                event.getChannel().sendMessage("**Psh** desativado.").queue();
                                break;
                            default:
                                event.getChannel().sendMessage("Comando inválido. Tenta **-pack psh <enable/disable>**.").queue();
                                break;
                        }
                    }
                    else if (words.length == 2) event.getChannel().sendMessage("O **Psh** está " + (pshEnabled ? "ativado" : "desativado") + ".").queue();
                    else event.getChannel().sendMessage("Comando inválido. Tenta **-pack psh <enable/disable>**.").queue();
                    break;
                case "delete": // delete <fileName>
                    if (isChannelIdSet()) event.getChannel().sendMessage("Funcionalidade não implementada.").queue();
                    break;
                default:
                    event.getChannel().sendMessage("Comando inválido. Tenta **-pack help** para veres os comandos existentes.").queue();
                    break;
            }
        }
        else if (isChannelIdSet() && event.getChannel().getId().equals(CHANNEL_ID) && pshEnabled) event.getChannel().sendMessage("Psh! Não se fala neste canal!").queue();
    }

    /**
     * Check if a channel has been chosen.
     * @return True if a channel has been chosen. False otherwise.
     */
    private boolean isChannelIdSet() {
        return !CHANNEL_ID.equals("0");
    }

    /**
     * Verify attachments extension.
     * @param event Event of received message.
     * @param attachments Attachments.
     * @return True if attachments are valid. False otherwise.
     */
    private boolean verifyAttachments(MessageReceivedEvent event, List<Message.Attachment> attachments) {
        // Iterate and verify all attachments:
        for (Message.Attachment attachment : attachments) {
            // Get content type of attachment:
            String type = attachment.getContentType();
            String extension = attachment.getFileExtension();

            if (type == null || extension == null) {
                // If content type isn't provided:
                event.getChannel().sendMessage("Não reconheço esse tipo de ficheiros. De certeza que me estás a enviar os ficheiros corretos?").queue();
                return false;
            }
            if (type.equalsIgnoreCase("image/png") && extension.equalsIgnoreCase("png")) {
                // If content type is .png:
                continue;
            }
            if (type.contains("text/plain") && extension.equalsIgnoreCase("properties")) {
                // If content type is .properties:
                if (type.contains("charset=utf-8")) {
                    // If .properties has format utf-8:
                    continue;
                }
                else {
                    event.getChannel().sendMessage("File Encoding incorreto. O teu ficheiro **.properties** tem de estar em formato *UTF-8*.").queue();
                    return false;
                }
            }
            event.getChannel().sendMessage("Esses ficheiros não estão corretos. Tens de me enviar um ficheiro **.properties** e um ficheiro **.png**.").queue();
            return false;
        }
        return true;
    }

    /**
     * Send attachments to Dropbox, to designated path:
     * @param attachment Attachment to send.
     * @param dropboxPath Dropbox path.
     */
    public void sendAttachment(Message.Attachment attachment, String dropboxPath) {
        attachment.downloadToFile().thenAccept(file -> DropBoxUtils.uploadFile(file, dropboxPath));
    }
}
