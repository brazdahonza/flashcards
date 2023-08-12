package flashcards;

import java.io.*;
import java.util.*;

public class Main {

    public static Scanner scannerInput = new Scanner(System.in);

    public static Map<String, Integer> wrongAnswers = new HashMap<>();
    public static PrintWriter printWriter;

    public static void main(String[] args) {

        File file = new File(".//log.txt");
        try {
            printWriter = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean logCalled = false;

        Map<String, String> cards = new LinkedHashMap<>();
        boolean isRunning = true;
        boolean exportAfterExit = false;
        String exportFileName = "file.txt";
        for (int i = 0; i< args.length; i++) {
            if (args[i].equals("-import")) {
                importCard(cards, args[i+1]);
            }
            if (args[i].equals("-export")) {
                exportAfterExit = true;
                exportFileName = args[i+1];
            }
        }

        while (isRunning) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            printWriter.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String command = scannerInput.nextLine();
            printWriter.println(command);
            switch (command) {
                case "add" -> addCard(cards);
                case "remove" -> removeCard(cards);
                case "import" -> importCard(cards);
                case "export" -> exportCard(cards);
                case "ask" -> ask(cards);
                case "log" -> logCalled = cardsLog(file);
                case "hardest card" -> hardestCard();
                case "reset stats" -> resetStats();
                case "exit" -> {
                    isRunning = false;
                    if (!logCalled) {
                        file.delete();
                    }
                    printWriter.println("Bye bye");
                    System.out.println("Bye bye");
                    if (exportAfterExit) {
                        exportCard(cards, exportFileName);
                    }
                }
            }
        }
    }

    private static void resetStats() {
        for (var wrong : wrongAnswers.entrySet()) {
            wrongAnswers.put(wrong.getKey(), 0);
        }
        System.out.println("Card statistics have been reset.");
        printWriter.println("Card statistics have been reset.");
    }

    private static boolean hardestCard() {
        int mostMistake = 0;
        List<Map.Entry<String, Integer>> badTerms = new LinkedList();
        for (var wrongAnswer : wrongAnswers.entrySet()) {
            if (wrongAnswer.getValue() > mostMistake) {
                mostMistake = wrongAnswer.getValue();
            }
        }
        if (mostMistake == 0) {
            System.out.println("There are no cards with errors.");
            printWriter.println("There are no cards with errors.");
            return false;
        }
        for (var wrongAnswer : wrongAnswers.entrySet()) {
            if (wrongAnswer.getValue() == mostMistake) {
                badTerms.add(wrongAnswer);
            }
        }
        if (badTerms.size() > 1) {
            String output = "The hardest cards are \""+ badTerms.get(0).getKey() +"\"";
            for (int i = 1; i<badTerms.size(); i++) {
                output += ",\"" + badTerms.get(i).getKey() + "\"";
            }
            output += ". You have "+badTerms.get(0).getValue()+" errors answering them";
            System.out.println(output);
            printWriter.println(output);
        } else {
            System.out.printf("The hardest card is \""+ badTerms.get(0).getKey() +"\". You have "+badTerms.get(0).getValue()+" errors answering it.\n");
            printWriter.printf("The hardest card is \""+ badTerms.get(0).getKey() +"\". You have "+badTerms.get(0).getValue()+" errors answering it.\n");
        }
        return true;
    }

    private static boolean cardsLog(File file) {
        System.out.println("File name:");
        printWriter.println("File name:");
        String fileName = scannerInput.nextLine();
        printWriter.println(fileName);
        file.renameTo(new File(".//" + fileName));
        System.out.println("The log has been saved.");
        printWriter.println("The log has been saved.");
        printWriter.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        printWriter.close();
        return true;
    }

    private static void importCard(Map<String, String> cards, String fileName) {
        int cardsAdded = 0;
        File file = new File(".//"+fileName);
        try {
            Scanner scannerOutput = new Scanner(file);
            while(scannerOutput.hasNextLine()) {
                String[] cardPair =  scannerOutput.nextLine().split(",");
                cardsAdded++;
                cards.put(cardPair[0], cardPair[1]);
                wrongAnswers.put(cardPair[0], Integer.valueOf(cardPair[2]));
            }
            System.out.printf("%d cards have been loaded.\n", cardsAdded);
            printWriter.printf("%d cards have been loaded.\n", cardsAdded);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            printWriter.println("File not found.");
        }
    }
    private static void importCard(Map<String, String> cards) {
        System.out.println("File name:");
        printWriter.println("File name:");
        String fileName = scannerInput.nextLine();
        printWriter.println(fileName);
        int cardsAdded = 0;
        File file = new File(".//"+fileName);
        try {
            Scanner scannerOutput = new Scanner(file);
            while(scannerOutput.hasNextLine()) {
                String[] cardPair =  scannerOutput.nextLine().split(",");
                cardsAdded++;
                cards.put(cardPair[0], cardPair[1]);
                wrongAnswers.put(cardPair[0], Integer.valueOf(cardPair[2]));
            }
            System.out.printf("%d cards have been loaded.\n", cardsAdded);
            printWriter.printf("%d cards have been loaded.\n", cardsAdded);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            printWriter.println("File not found.");
        }
    }

    private static void exportCard(Map<String, String> cards, String fileName) {
        File file = new File(".//"+fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (var card : cards.entrySet()) {
                int wrongCount = 0;

                for (var wrong : wrongAnswers.entrySet()) {
                    if (wrong.getKey() == card.getKey()) {
                        wrongCount = wrong.getValue();
                    }
                }
                fileWriter.write(card.getKey()+","+card.getValue()+","+wrongCount+"\n");
            }
            fileWriter.close();
            System.out.printf("%d cards have been saved.\n", cards.size());
            printWriter.printf("%d cards have been saved.\n", cards.size());

        } catch (IOException e) {
            System.out.println("Something went wrong.");
            printWriter.println("Something went wrong.");
        }
    }

    private static void exportCard(Map<String, String> cards) {
        System.out.println("File name:");
        printWriter.println("File name:");
        String fileName = scannerInput.nextLine();
        printWriter.println(fileName);
        File file = new File(".//"+fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (var card : cards.entrySet()) {
                int wrongCount = 0;

                for (var wrong : wrongAnswers.entrySet()) {
                    if (wrong.getKey() == card.getKey()) {
                        wrongCount = wrong.getValue();
                    }
                }
                fileWriter.write(card.getKey()+","+card.getValue()+","+wrongCount+"\n");
            }
            fileWriter.close();
            System.out.printf("%d cards have been saved.\n", cards.size());
            printWriter.printf("%d cards have been saved.\n", cards.size());

        } catch (IOException e) {
            System.out.println("Something went wrong.");
            printWriter.println("Something went wrong.");
        }
    }

    private static void ask(Map<String, String> cards) {
        System.out.println("How many times to ask?");
        printWriter.println("How many times to ask?");
        int times = scannerInput.nextInt();
        printWriter.println(times);
        scannerInput.nextLine();
        int count = 0;
        while (count < times) {
            for (var test : cards.entrySet()) {
                if (count >= times) break;
                System.out.println("Print the definition of \"" + test.getKey() + "\":");
                printWriter.println("Print the definition of \"" + test.getKey() + "\":");
                String answer = scannerInput.nextLine();
                printWriter.println(answer);
                if (answer.equals(test.getValue())) {
                    System.out.println("Correct!");
                    printWriter.println("Correct!");
                } else {
                    if (wrongAnswers.containsKey(test.getKey())) {
                        for (var wrongAnswer : wrongAnswers.entrySet()) {
                            if (wrongAnswer.getKey().equals(test.getKey())) {
                                int countWrong = wrongAnswer.getValue() + 1;
                                wrongAnswers.put(wrongAnswer.getKey(), countWrong);
                            }
                        }
                    } else {
                        wrongAnswers.put(test.getKey(), 1);
                    }

                    if (cards.containsValue(answer)) {
                        for (var correct : cards.entrySet()) {
                            if (correct.getValue().equals(answer)) {
                                System.out.println("Wrong. The right answer is \"" + test.getValue() + "\", but your definition is correct for \"" + correct.getKey() + "\".");
                                printWriter.println("Wrong. The right answer is \"" + test.getValue() + "\", but your definition is correct for \"" + correct.getKey() + "\".");
                            }
                        }
                    } else {
                        System.out.println("Wrong. The right answer is \"" + test.getValue() + "\".");
                        printWriter.println("Wrong. The right answer is \"" + test.getValue() + "\".");

                    }
                }
                count++;
            }
        }
    }

    public static boolean addCard(Map<String, String> cards) {
        System.out.println("The card:");
        printWriter.println("The card:");
        String term = scannerInput.nextLine();
        printWriter.println(term);
        if (cards.containsKey(term)) {
            System.out.println("The card \"" + term + "\" already exists.");
            printWriter.println("The card \"" + term + "\" already exists.");
            return false;
        }
        System.out.println("The definition of the card:");
        printWriter.println("The definition of the card:");
        String definition = scannerInput.nextLine();
        printWriter.println(definition);
        if (cards.containsValue(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists.");
            printWriter.println("The definition \"" + definition + "\" already exists.");
            return false;
        }
        cards.put(term, definition);
        System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
        printWriter.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
        return true;
    }

    public static void removeCard(Map<String, String> cards) {
        System.out.println("Which card?");
        printWriter.println("Which card?");
        String term = scannerInput.nextLine();
        printWriter.println(term);
        if (!cards.containsKey(term)) {
            System.out.printf("Can't remove \"%s\": there is no such card.\n", term);
            printWriter.printf("Can't remove \"%s\": there is no such card.\n", term);
        } else {
            cards.remove(term);
            System.out.println("The card has been removed.");
            printWriter.println("The card has been removed.");
        }
    }
}
