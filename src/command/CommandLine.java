package command;

import command.crud.Crud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * CLI manager
 */
public class CommandLine {

    private static Command[] commands = new Command[]{
        new Crud()
    };

    public static void main(String[] args) {

        Map<String, Command> instances = new HashMap<>();
        Map<String, CommandRequirements> infos = new HashMap<>();

        StringBuilder help = new StringBuilder();

        CommandRequirements rq;
        for(Command cmd : commands) {
            rq = cmd.requirements();
            instances.put(rq.getName(), cmd);
            infos.put(rq.getName(), rq);

            help.append("\t- '");
            help.append(rq.getName());
            help.append(" ");
            help.append("<" + String.join("> <", rq.getArgs()) + "> : " + rq.getHelp());
            help.append("' ");
            help.append(rq.getHelp());
            help.append("\n");
        }

        if(0 == args.length || args[0].equals("-h")) {
            System.out.println("This is the command line helper for the Symfonee project.");
            System.out.println("Available commands are :");
            System.out.println(help.toString());
            System.exit(0);
        }

        String commandName = args[0];

        if(! instances.containsKey(commandName)) {
            System.out.println(commandName + " command not found.");
            System.out.println("Available commands are :");
            System.out.println(help.toString());
            System.exit(0);
        }

        if(args.length - 1 < infos.get(commandName).getArgs().length) {
            System.out.println(
                "Invalid number of arguments for " + commandName +
                " expected " + infos.get(commandName).getArgs().length +
                " got " + (args.length - 1)
            );
            System.out.println(
                "'<" + String.join("> <", infos.get(commandName).getArgs()) + ">' : " +
                infos.get(commandName).getHelp()
            );
            System.exit(0);
        }

        // slice the command name (first arg) and give the others to the executed command
        instances.get(commandName).execute(Arrays.copyOfRange(args, 1, args.length));
    }

}
