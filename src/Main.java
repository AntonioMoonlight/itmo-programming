import client.ClientMain;
import server.ServerMain;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        String[] modeArgs = new String[args.length - 1];
        System.arraycopy(args, 1, modeArgs, 0, args.length - 1);

        switch (mode) {
            case "server":
                ServerMain.main(modeArgs);
                break;
            case "client":
                ClientMain.main(modeArgs);
                break;
            default:
                System.err.println("Unknown mode: " + mode);
                printUsage();
                System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Music Band Collection - Client-Server Application");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java Main server [port]");
        System.out.println("    Start server on specified port (default: 8080)");
        System.out.println();
        System.out.println("  java Main client [host] [port]");
        System.out.println("    Start client connecting to specified host:port");
        System.out.println("    (default: localhost:8080)");
        System.out.println();
        System.out.println("Environment Variables:");
        System.out.println("  DATA_FILE - Path to the collection data file (required for server)");
    }
}