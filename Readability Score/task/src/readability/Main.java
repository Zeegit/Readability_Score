package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scn = new Scanner(System.in);
        String fileName = args[0];
        String fileText = readFileAsString(fileName);
        //String fileText = "This is the front page of the Simple English Wikipedia. Wikipedias are places where people work together to write encyclopedias in different languages. We use Simple English words and grammar here. The Simple English Wikipedia is for everyone! That includes children and adults who are learning English. There are 142,262 articles on the Simple English Wikipedia. All of the pages are free to use. They have all been published under both the Creative Commons License and the GNU Free Documentation License. You can help here! You may change these pages and make new pages. Read the help pages and other good pages to learn how to write pages here. If you need help, you may ask questions at Simple talk. Use Basic English vocabulary and shorter sentences. This allows people to understand normally complex terms or phrases.";


        List<String> lines = Arrays.stream(fileText.split("[ ,.!?\\t\\v\\r\\n\\f\\u00a0]"))
                .filter(x -> !x.isEmpty()).collect(Collectors.toList());


        long sentences = Arrays.stream(fileText.split("[.!?]"))
                .filter(x -> !x.isEmpty()).count();
        long words = Arrays.stream(fileText.split("[ .!?\\t\\v\\r\\n\\f\\u00a0]"))
                .filter(x -> !x.isEmpty()).count();


        List<String> collect = Arrays.stream(fileText.split("[ .!?\\t\\v\\r\\n\\f\\u00a0]"))
                .filter(x -> !x.isEmpty()).collect(Collectors.toList());
        int syllables = 0;
        int polysyllables = 0;
        Pattern pattern1 = Pattern.compile("[aeiouy]", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("[aeiouy][aeiouy]", Pattern.CASE_INSENSITIVE);
        Pattern pattern3 = Pattern.compile("[aeiouy][aeiouy][aeiouy]", Pattern.CASE_INSENSITIVE);
        for(String str : collect) {
            Matcher matcher1 = pattern1.matcher(str);
            Matcher matcher2 = pattern2.matcher(str);
            Matcher matcher3 = pattern3.matcher(str);

            int totalMatches = 0;
            while(matcher1.find()) { totalMatches++; }
            while(matcher2.find()) { totalMatches--; }
            while(matcher3.find()) { totalMatches--; }

            if (str.endsWith("e")) { totalMatches--; }

            if (totalMatches == 0) { totalMatches++; }
            System.out.println(str);
            System.out.println(totalMatches);
            syllables += totalMatches;
            if (totalMatches > 2) { polysyllables++; }
        }

        long characters = fileText
                .replace("\n", "")
                .replace("\t", "")
                .replace(" ", "")
                .length();

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String answ = scn.nextLine();
        //String answ = "all";


        double automated = 4.71 * characters / words + 0.5 * words / sentences - 21.43;
        int automatedAge  = getAge(automated);

        double flesch_kincaid = 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
        int flesch_kincaidAge  = getAge(flesch_kincaid);

        double simple_measure = 1.043 * Math.sqrt(polysyllables * 30.0 / sentences) + 3.1291;
        int simple_measureAge  = getAge(simple_measure);

        double L = (double) characters / words * 100;
        double S = (double) sentences / words * 100;
        double coleman_liau = 0.0588 * L - 0.296 * S - 15.8;
        int coleman_liauAge  = getAge(coleman_liau);

        System.out.println("The text is:");
        System.out.println(fileText);
        System.out.printf("Words: %d\n", words);
        System.out.printf("Sentences: %d\n", sentences);
        System.out.printf("Characters: %d\n", characters);
        System.out.printf("Syllables: %d\n", syllables);
        System.out.printf("Polysyllables: %d\n", polysyllables);

        if (answ.equals("ARI") || answ.equals("all")) {
            System.out.printf("Automated Readability Index: %.2f (about %d year olds).\n", automated, automatedAge);
        }
        if (answ.equals("FK") || answ.equals("all")) {
            System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n", flesch_kincaid, flesch_kincaidAge);
        }
        if (answ.equals("SMOG") || answ.equals("all")) {
            System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n", simple_measure, simple_measureAge);
        }
        if (answ.equals("CL") || answ.equals("all")) {
            System.out.printf("Coleman–Liau index: %.2f (about %d year olds).\n", coleman_liau, coleman_liauAge);
        }

        System.out.printf("\nThis text should be understood in average by %.2f year olds.\n",
                (automatedAge + flesch_kincaidAge + simple_measureAge + coleman_liauAge) / 4.0);

        scn.close();
    }

    private static int getAge(double coeff) {
        int age = 0;
        if (coeff < 1) { age = 5; }
        else if (coeff < 2) { age = 6; }
        else if (coeff < 3) { age = 7; }
        else if (coeff < 4) { age = 9; }
        else if (coeff < 5) { age = 10; }
        else if (coeff < 6) { age = 11; }
        else if (coeff < 7) { age = 12; }
        else if (coeff < 8) { age = 13; }
        else if (coeff < 9) { age = 14; }
        else if (coeff < 10) { age = 15; }
        else if (coeff < 11) { age = 17; }
        else if (coeff < 12) { age = 18; }
        else if (coeff < 13) { age = 18; }
        else { age = 24; }
       return age;
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
