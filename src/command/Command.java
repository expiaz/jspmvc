package command;

/**
 * defines a Command on the CLI
 */
public interface Command {
    CommandRequirements requirements();
    void execute(String args[]);
}
