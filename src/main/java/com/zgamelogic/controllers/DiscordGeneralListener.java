package com.zgamelogic.controllers;

import com.zgamelogic.annotations.Bot;
import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import com.zgamelogic.annotations.EventProperty;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@DiscordController
@RestController
@Slf4j
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
        log.info("Bot {} has finished loading", event.getJDA().getSelfUser().getName()); // Prints the name of the bot to the console
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
     * This is the auto complete response for the options slash command for the fruit option.
     * @param event CommandAutoCompleteInteractionEvent that triggers when a use focuses the fruit option.
     */
    @DiscordMapping(Id = "options", FocusedOption = "fruit")
    private void optionsAutoCompleteResponse(CommandAutoCompleteInteractionEvent event){
        List<String> validFruits = List.of("Apple", "Banana", "Pear"); // list of valid fruits
        String typedFruit = event.getFocusedOption().getValue(); // current value of the typed fruit on discord
        List<Command.Choice> choices = validFruits.stream() // get a stream of valid fruits
                .filter(fruit -> fruit.contains(typedFruit)) // filter the stream to only include fruits that contain the typed discord fruit
                .map(fruit -> new Command.Choice(fruit, fruit)).toList(); // convert the stream into command choices. First parameter is the displayed value, the second parameter is the value that gets sent to discord
        event.replyChoices(choices).queue();
    }

    /**
     * This is the slash command response for the options slash command
     * @param event SlashCommandInteractionEvent for options.
     * @param fruit Fruit value. Equivalent to event.getOption("fruit").getAsString();
     * @param user User value. Equivalent to event.getOption("user").getAsUser();
     */
    @DiscordMapping(Id = "options")
    private void optionsSlashCommand(
            SlashCommandInteractionEvent event,
            @EventProperty String fruit,
            @EventProperty User user
    ){
        log.info("Fruit: {} was selected", fruit);
        log.info("User: {} was selected", user.getName());
        event.reply(user.getName() + " wants " + fruit).queue();
    }

    /**
     * This is a rest api mapping. On a get rest request it'll post pong to every guilds default text channel
     */
    @GetMapping("ping")
    private void restPingPong(){
        bot.getGuilds().forEach(guild -> guild.getDefaultChannel().asTextChannel().sendMessage("pong").queue());
    }

    @Bean
    private List<CommandData> generalCommands(){
        /*
        This here is used to update the commands for your bot.
        You can set any number of beans with a list or single CommandData to be added to the global commands list on bot launch
         */
        return List.of(
                Commands.slash("ping", "Sends a ping to the bot"), // Slash command, so when the user types "/ping" in discord, this command and its description comes up
                Commands.user("Name user"), // User command, right-clicking a user and going to app > "Name user" will activate this command
                Commands.message("Count words"), // Message command, right-clicking a message and going to app > "Count words" will activate this command
                Commands.slash("options", "A command with some options").addOptions(
                        new OptionData(OptionType.STRING, "fruit", "Pick a fruit", true, true),
                        new OptionData(OptionType.USER, "user", "Pick a user", true)
                )
        );
    }
}
