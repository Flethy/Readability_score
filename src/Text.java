import java.io.IOException;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Text {

    private int wordsCount;
    private int sentencesCount;
    private int charactersCount;
    private int syllablesCount;
    private int polysyllablesCount;
    private double ageSum = 0;

    public static void main(String[] args) {

        System.out.println("The text is:");
        String str = readFileAsString(args[0]);
        System.out.println(str);

        Text text = new Text();
        text.analizeString(str);

    }

    private void analizeString(String str) {

        getWords(str);
        getSentences(str);
        getCharacters(str);
        getSyllablesAndPolysyllables(str);

        System.out.printf("\nWords: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d\n", wordsCount, sentencesCount, charactersCount, syllablesCount, polysyllablesCount);

        findScore();

    }

    // handle the user input
    private void findScore() {
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String input = new Scanner(System.in).next();
        System.out.println();

        switch (input) {
            case "ARI":
                findAutomatedReadabilityIndex();
                break;
            case "FK":
                findFleschKincaidReadabilityTests();
                break;
            case "SMOG":
                findSimpleMeasureOfGobbledygook();
                break;
            case "CL" :
                findColemanLiauIndex();
                break;
            case "all":
                findAutomatedReadabilityIndex();
                findFleschKincaidReadabilityTests();
                findSimpleMeasureOfGobbledygook();
                findColemanLiauIndex();
                System.out.printf("\nThis text should be understood in average by %.2f year olds.", ageSum/4);
                break;
            default:
                System.out.println("Unexpected input.");
        }
    }

    // sentences count
    private void getSentences(String str) {
        this.sentencesCount = str.trim().split("[.!?]").length;
    }

    // words count
    private void getWords(String str) {
        this.wordsCount = str.trim().split("\\s").length;
    }

    // characters count
    private void getCharacters(String str) {
        this.charactersCount = str.trim().replaceAll("\\s+", "").split("").length;
    }

    // syllables and polysyllables count
    private void getSyllablesAndPolysyllables(String str) {
        this.syllablesCount = 0;

        for (String word : str.trim().replaceAll("[.,!?]", "").split("\\s+")) {
            word = word.toLowerCase();
            if (word.endsWith("e")) {
                word = word.substring(0, word.length()-1);
            }
            if (!word.matches(".*[aeiouy]+.*")) {
                syllablesCount++;
                continue;
            }

            int numOfPolysyllables = 0;
            while (word.matches("\\b\\w*[aeiouy]+\\w*\\b")) {
                syllablesCount++;
                word = word.replaceFirst("[aeiouy]+", "");
                numOfPolysyllables++;
            }
            this.polysyllablesCount += numOfPolysyllables > 2 ? 1 : 0;
        }
    }

    // read file and convert it to string
    private static String readFileAsString(String path) {
        String str = "";
        try {
            str = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Cannot read file: " + e.getMessage());
        }
        return str;
    }

    // Automated Readability Index
    private void findAutomatedReadabilityIndex() {
        double score = 4.71 * (this.charactersCount*1.0 / this.wordsCount) + 0.5 * (this.wordsCount*1.0 / this.sentencesCount) - 21.43;
        String year = getAge(Math.round(score));
        System.out.printf("Automated Readability Index: %.2f (about %s year olds).\n", score, year);
    }

    // Flesch–Kincaid readability tests
    private void findFleschKincaidReadabilityTests() {
        double score = 0.39 * (this.wordsCount*1.0 / this.sentencesCount) + 11.8 * (this.syllablesCount*1.0 / this.wordsCount) - 15.59;
        String year = getAge(Math.round(score));
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds).\n", score, year);
    }

    // Simple Measure of Gobbledygook
    private void findSimpleMeasureOfGobbledygook() {
        double score = 1.043 * Math.sqrt(polysyllablesCount * (30*1.0 / sentencesCount)) + 3.1291;
        String year = getAge(Math.round(score));
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds).\n", score, year);
    }

    // Coleman–Liau index
    private void findColemanLiauIndex() {
        // L is the average number of characters per 100 words
        double L = charactersCount*1.0 / (wordsCount*1.0 / 100);
        // S is the average number of sentences per 100 words
        double S = sentencesCount*1.0 / (wordsCount*1.0 / 100);
        double score = 0.0588 * L - 0.296 * S - 15.8;
        String year = getAge(Math.round(score));
        System.out.printf("Coleman–Liau index: %.2f (about %s year olds)\n", score, year);
    }

    private String getAge(double score) {
        String[] ages = {"6", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "24", "24+"};
        ageSum += ages[(int) score-1].equals("24+") ? 24 : Integer.parseInt(ages[(int) score-1]);
        return ages[(int) score-1];
    }
}
