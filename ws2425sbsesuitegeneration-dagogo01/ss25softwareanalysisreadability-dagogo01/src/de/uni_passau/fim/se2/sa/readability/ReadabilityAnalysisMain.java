package de.uni_passau.fim.se2.sa.readability;

import de.uni_passau.fim.se2.sa.readability.features.HalsteadVolumeFeature;
import de.uni_passau.fim.se2.sa.readability.subcommands.SubcommandClassify;
import de.uni_passau.fim.se2.sa.readability.subcommands.SubcommandPreprocess;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.io.PrintStream;

@Command(
    name = "readability",
    description = "Analyze readability of Java code snippets",
    mixinStandardHelpOptions = true,
    version = "1.0",
    subcommands = {SubcommandPreprocess.class, SubcommandClassify.class}
)
public class ReadabilityAnalysisMain implements Callable<Integer> {

    /**
     * Main entry point for the application.
     * This method will exit the JVM with the returned status code.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String code = """

        protected synchronized String evalToString(
            Object self,
            String expr,
            String sep)
    	throws ExpansionException {

        _scratchBindings.put("self", self);
        java.util.List values = eval(_scratchBindings, expr);
        _strBuf.setLength(0);
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            Object v = iter.next();
            if (Model.getFacade().isAModelElement(v)) {
                v = Model.getFacade().getName(v);
                if ("".equals(v)) {
                    v = Translator.localize("misc.name.anon");
                }
            }
            if (Model.getFacade().isAExpression(v)) {
                v = Model.getFacade().getBody(v);
                if ("".equals(v)) {
                    v = "(unspecified)";
                }
            }
            if (!"".equals(v)) {
                _strBuf.append(v);
                if (iter.hasNext()) {
                    _strBuf.append(sep);
                }
            }
        }
        return _strBuf.toString();
    }
        """;
        System.out.println(new HalsteadVolumeFeature().computeMetric(code));
        int exitCode = runCommand(args);
        System.exit(exitCode);
    }

    /**
     * Test-safe entry point that executes the command without terminating the JVM.
     * This method should be used in tests instead of main().
     *
     * @param args command line arguments
     * @return the exit code that would have been used to terminate the JVM
     */
    public static int runCommand(String[] args) {
        CommandLine cmd = new CommandLine(new ReadabilityAnalysisMain())
            .setUsageHelpAutoWidth(true);

        // Set error handler to print to stderr and show usage for invalid commands
        cmd.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            PrintStream err = System.err;
            if (ex instanceof CommandLine.UnmatchedArgumentException) {
                err.println("Unknown subcommand: " + ex.getMessage());
            } else {
                err.println(ex.getMessage());
            }
            err.println("Usage:");
            CommandLine.usage(commandLine.getCommand(), err);
            return 1;
        });

        // Set parameter exception handler for validation errors
        cmd.setParameterExceptionHandler((ex, args1) -> {
            PrintStream err = System.err;
            err.println(ex.getMessage());
            err.println("Usage:");
            CommandLine.usage(ex.getCommandLine().getCommand(), err);
            return 1;
        });

        try {
            if (args.length == 0) {
                System.err.println("Usage:");
                CommandLine.usage(cmd.getCommand(), System.err);
                return 1;
            }
            
            // Check for invalid subcommands before execution
            if (!cmd.getSubcommands().containsKey(args[0]) && !args[0].startsWith("-")) {
                System.err.println("Unknown subcommand: " + args[0]);
                System.err.println("Usage:");
                CommandLine.usage(cmd.getCommand(), System.err);
                return 1;
            }
            
            return cmd.execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Usage:");
            CommandLine.usage(cmd.getCommand(), System.err);
            return 1;
        }
    }

    @Override
    public Integer call() {
        // Show usage when no subcommand is specified
        CommandLine.usage(this, System.out);
        return 1;
    }
}