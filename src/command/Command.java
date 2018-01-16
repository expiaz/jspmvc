package command;

public interface Command {
    CommandRequirements requirements();
    void execute(String args[]);
}
