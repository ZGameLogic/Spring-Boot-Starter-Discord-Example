package com.zgamelogic.controllers;

import com.zgamelogic.annotations.DiscordController;
import com.zgamelogic.annotations.DiscordMapping;
import com.zgamelogic.annotations.EventProperty;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.context.annotation.Bean;

@DiscordController // Discord controller annotation. You need this in all of your discord controllers so spring knows.
@Slf4j // Lombok annotation for ease of use.
public class FormListener {

    /**
     * Respond to the slash command with and id of form.
     */
    @DiscordMapping(Id = "form")
    private void formSlashCommand(SlashCommandInteractionEvent event){
        TextInput titleInput = TextInput.create(
                "title", // ID of input
                "Book Title",  // label of input
                TextInputStyle.SHORT) // Text input style
                .setPlaceholder("Too Like the Lightning") // Placeholder text, use this for an example
                .build(); // build the text input
        TextInput authorInput = TextInput.create("author", "Book Author", TextInputStyle.SHORT).setPlaceholder("Ada Palmer").build();
        TextInput shortDescription = TextInput.create("description", "Book Description", TextInputStyle.PARAGRAPH).setRequired(false).build();

        event.replyModal(
                Modal.create(
                "form_modal", // ID of the modal
                        "Create Book" // Title of the modal displayed to the user
                )
                .addActionRow(titleInput) // Add a title input row to your modal
                .addActionRow(authorInput) // Add an author input row to your modal
                .addActionRow(shortDescription) // Add a description input row to your modal
                .build() // Build your modal
        ).queue(); // Send the message back
    }

    /**
     * Method to handle form_modal submission
     * @param event Modal interaction event to trigger this method
     * @param title Title field from the modal
     * @param author Author field from the modal
     * @param description Description field from the modal
     */
    @DiscordMapping(Id = "form_modal")
    private void formModal(
            ModalInteractionEvent event, // Modal interaction event
            @EventProperty String title, // title field from the modal
            @EventProperty String author, // author field from the modal
            @EventProperty String description // description field from the modal
    ){
        event.reply("Thank you for your submission!").queue(); // Reply to the event with a simple message
        log.info("Boot title: {}", title); // print out the title to the log
        log.info("Boot author: {}", author); // print out the author to the log
        log.info("Boot description: {}", description); // print out the description to the log
    }


    /**
     * This is a slash command that gets automatically injected on startup.
     * It's important to note that I could have also returned a CommandData object instead of a SlashCommandData object.
     * @return Slash commands
     */
    @Bean
    private SlashCommandData slashCommandData(){
        return Commands.slash("form", "Fill out this form please.");
    }
}
