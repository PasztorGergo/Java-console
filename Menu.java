import java.util.Scanner;

public class Menu {
    public Menu(){
        while(true){
            String[] commandAndParams = ProcessCommand();
            MenuCases(commandAndParams[0]);
        }
    }

    private String[] ProcessCommand(){
        Scanner scanner = new Scanner(System.in);

        String[] commandAndParameters = scanner.nextLine().split(" ");
        return commandAndParameters;
    }

    private void MenuCases(String command){
        switch (command) {
            case "exit":
                System.exit(0);
                break;
        
            default:
                break;
        }
    }
}
