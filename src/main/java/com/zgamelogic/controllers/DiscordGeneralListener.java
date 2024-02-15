package com.zgamelogic.controllers;

import com.zgamelogic.annotations.Bot;
import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DiscordController
@RestController
public class DiscordGeneralListener {

    /**
     * The @Bot annotation allows this field to be automatically populated by the JDA when it's logged in
     */
    @Bot
    private JDA bot;

    /**
     * This method gets called when the bot is logged in and ready.
     * This specifically updates global commands
     * @param event Ready Event from JDA
     */
    @DiscordMapping
    private void onReady(ReadyEvent event){
        /*
        This here is used to update the commands for your bot.
        You should only be updating commands in one place as if you do it in different requests they will overwrite each other.
         */
        bot.updateCommands().addCommands(
                Commands.slash("ping", "Sends a ping to the bot"), // Slash command, so when the user types "/ping" in discord, this command and its description comes up
                Commands.user("Name user"), // User command, right-clicking a user and going to app > "Name user" will activate this command
                Commands.message("Count words") // Message command, right-clicking a message and going to app > "Count words" will activate this command
        ).queue(); // JDA requires its actions to be queued (or completed, more on that later)
    }

    /**
     * This method listens for a message received event.
     * If that message is ping, the bot replies with pong
     * @param event Message Received Event from JDA
     */
    @DiscordMapping
    private void pingPong(MessageReceivedEvent event){
        if(event.getMessage().getContentRaw().equals("ping")){
            event.getChannel().sendMessage("pong").queue();
        }
    }

    /**
     * Simple slash command method mapping.
     * This will only proc on a slash command with an id of ping
     * @param event Slash Command Interaction Event from JDA
     */
    @DiscordMapping(Id = "ping")
    private void pingPongSlash(SlashCommandInteractionEvent event){
        event.reply("pong").queue();
    }

    /**
     * Simple user context interaction command method mapping.
     * This will only proc on a user context interaction command with an id of Name user
     * @param event User Context Interaction Event from JDA
     */
    @DiscordMapping(Id = "Name user")
    private void nameUser(UserContextInteractionEvent event){
        event.reply(event.getTargetMember().getEffectiveName()).queue(); // Sends the name of the user that got clicked on
    }

    /**
     * Simple message context interaction command method mapping.
     * This will only proc on a user message interaction command with an id of Name user
     * @param event Message Context Interaction Event from JDA
     */
    @DiscordMapping(Id = "Count words")
    private void countWords(MessageContextInteractionEvent event){
        event.reply(String.valueOf(
                event.getTarget().getContentRaw().trim().split("[ \n]").length
        )).queue(); // Sends the count of words in the message that was clicked on
    }

    /**
     * This is a rest api mapping. On a get rest request it'll post pong to every guilds default text channel
     */
    @GetMapping("ping")
    private void restPingPong(){
        bot.getGuilds().forEach(guild -> guild.getDefaultChannel().asTextChannel().sendMessage("pong").queue());
    }
}
