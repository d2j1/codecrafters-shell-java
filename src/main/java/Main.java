import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage

        Scanner scanner = new Scanner(System.in);

        while(true){

            System.out.print("$ ");

            //block untill user press enter
            if(!scanner.hasNextLine()){
                break;
            }

            String line = scanner.nextLine().trim();

            if(line.isEmpty()){
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0];
            String[] cmdArgs = Arrays.copyOfRange(parts, 1, parts.length);

            switch (command){
                case "exit":
                    return;

                case "echo":
                    handleEcho(cmdArgs);
                    break;

                case "type":
                    handleType(cmdArgs);
                    break;

                default:
                    System.out.println(command + ": command not found");

            }
        }

        scanner.close();

    }

    private static void handleEcho(String[] args){
        System.out.println(String.join(" ",args));
    }

    private static void handleType(String[] args){
        if(args.length == 0){
            return;
        }

        Set<String> builtIns = new HashSet<>(Arrays.asList("echo","exit","type"));
        String target = args[0];

        if(builtIns.contains(target)){
            System.out.println(target+" is a shell builtin");
            return;
        }

        String pathEnv = System.getenv("PATH");

        if(pathEnv != null){
            String[] paths = pathEnv.split(":");

            for(String dir : paths){
                File file = new File(dir, target);

                if(file.exists() && file.canExecute()){
                    System.out.println(target+ " is "+ file.getAbsolutePath());
                    return;
                }
            }
        }

            System.out.println(target+ ": not found");

    }
}
