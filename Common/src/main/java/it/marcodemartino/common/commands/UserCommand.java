package it.marcodemartino.common.commands;

public interface UserCommand extends Command {

    boolean isFormatCorrect(String input);
    String getCorrectFormat();

}
