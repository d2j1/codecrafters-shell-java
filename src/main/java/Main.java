import java.util.Scanner;

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


            String command = scanner.nextLine().trim();

            if(command.isEmpty()){

                System.out.println("This is valid command");
            }

            if(command.equalsIgnoreCase("exit")){
                break;
            }
            System.out.println(command + ": command not found");

        }

    }

}
