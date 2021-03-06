package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.String;

public class Solution {
	private static HashMap<String, String> MORSEMAP = new HashMap<>(); // Maps every letter to a morse code sequence
	private static HashMap<String, String> CHARMAP = new HashMap<>(); // Maps every morse code sequence to a letter
	private static ArrayList<String> DICTIONARY = new ArrayList<>(); // Contains all the words in the dictionary file
	private static ArrayList<String> MORSE = new ArrayList<>(); // Contains all the morse code sequences
	private static ArrayList<String> WORD = new ArrayList<>(); // Holds possible words in backtracking part
	private static TrieNode TRIE = new TrieNode(); // Is the Trie of the Dictionary file

	// Loads Morse Code and Dictionary from files
	public static void loadFiles() {
		String filename = "";
		try {
			filename = "src/resources/morse.txt";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] letters = line.split(" ");
				MORSEMAP.put(letters[1], letters[0]);
				CHARMAP.put(letters[0], letters[1]);
				MORSE.add(letters[1]);
			}
			reader.close();
			filename = "src/resources/dictionary.txt";
			reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				DICTIONARY.add(line);
			}
			reader.close();
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
		}
		addDictionary();
	}

	// Finds word in Dictionary
	// Returns String "0" if not found
	public static String findWord(String morsed) {
		String[] split = morsed.split(" ");
		String word = "";
		for (String letter : split) {
			word += MORSEMAP.get(letter);
		}
		if (DICTIONARY.contains(word)) {
			return word;
		}
		return "0";
	}

	// Part 1
	// Checks to see if morsed word is in Dictionary
	public static void handleSpacedLetters(String morsed) {
		String result = findWord(morsed);
		if (!result.equals("0")) {
			System.out.println(result);
		}
	}

	// Returns ArrayList starting with given string
	public static ArrayList<String> filter(ArrayList<String> w, String s) {
		w.removeIf(a -> !a.startsWith(s));
		return w;
	}

	// Part 2
	// Uses Backtracking to find words
	public static void findWords(String morse, String current) {
		ArrayList<String> possibilities = filter(new ArrayList<>(DICTIONARY), current);
		if (possibilities.size() == 0) {
			return;
		}
		if (morse.equals("") && DICTIONARY.contains(current)) {
			WORD.add(current);
		}
		for (String key : MORSE) {
			if (morse.startsWith(key)) {
				findWords(morse.substring(key.length()), current + MORSEMAP.get(key));
			}
		}
	}

	// Main function for part 2 that prints out words
	public static void handleWord(String morsed) {
		findWords(morsed, "");
		for (String word : WORD) {
			System.out.println(word);
		}
	}

	// Returns an equivalent of the word in morse code
	// Used for adding dictionary to TRIE
	public static String charToMorse(String word) {
		String result = "";
		for (char letter : word.toCharArray()) {
			result += CHARMAP.get(Character.toString(letter));
		}
		return result;
	}

	// Used for part 3
	// Adds all words to the TRIE
	public static void addDictionary() {
		for (String word : DICTIONARY) {
			TrieNode.addWord(TRIE, word);
		}
	}

	// Recursive methods that creates all
	// possible phrases for sets of words
	// Helper function for part 3
	public static ArrayList<String> createPhrases(ArrayList<ArrayList<String>> arr) {
		if (arr.size() == 1) {
			return arr.get(0);
		} else {
			ArrayList<String> result = new ArrayList<>();
			ArrayList<String> begin = arr.remove(0);
			ArrayList<String> follow = arr.remove(0);
			for (String b : begin) {
				for (String a : follow) {
					result.add(b + " " + a);
				}
			}
			arr.add(0, result);
			return createPhrases(arr);
		}
	}

	// Part 3 - Uses a trie to print out
	// spaced word possibilities
	public static void handleSpacedWords(String morsed) {
		ArrayList<ArrayList<String>> options = new ArrayList<>();
		String[] words = morsed.split(" ");
		for (String word : words) {
			ArrayList<String> parts = TrieNode.autoComplete(TRIE, word);
			options.add(parts);
		}
		ArrayList<String> phrases = createPhrases(options);
		Collections.sort(phrases);
		for (String p : phrases) {
			p = p.trim();
			System.out.println(p);
		}
	}

	
	//Function for part 4
	//Using backtracking, the function finds all possible
	//combinations of words that can be made from the 
	//morse equivalent
	//Innermost ArrayLists are the possible words for a certain section of morse code
		//Following ArrayLists are possible words that may follow
	//Inner Middle ArrayLists are the all possible phrases that can be made with combinations of innermost ArrayLists
	//Outer ArrayList contains all of the possible phrases ArrayLists
	public static ArrayList<ArrayList<ArrayList<String>>> findSentence(String morse) {
		ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<>();
		for (int i = 0; i < morse.length(); i++) {
			ArrayList<String> temp = TrieNode.autoComplete(TRIE, morse.substring(0, morse.length() - i));
			if (temp.isEmpty()) {
				continue;
			} else {
				ArrayList<ArrayList<ArrayList<String>>> temp2 = findSentence(morse.substring(morse.length() - i));
				if (!temp2.isEmpty() || morse.substring(morse.length() - i).length() == 0) {
					if (!temp2.isEmpty()) {
						for (ArrayList<ArrayList<String>> arr : temp2) {
							arr.add(0, temp);
						}
						result.addAll(temp2);
					}
					else {
						ArrayList<ArrayList<String>> empt = new ArrayList<>();
						empt.add(temp);
						result.add(empt);
					}
				}
			}
		}
		return result;
	}

	//Main Part 4 Function
	//Calls find Sentence
	public static void handleSentence(String morsed) {
		ArrayList<ArrayList<ArrayList<String>>> sentences = findSentence(morsed);
		int less = 0;
		for (int i = 0; i < sentences.size(); i++) {
			if(sentences.get(i).size() < sentences.get(less).size()) {
				less = i;
			}
		}
		int size = sentences.get(less).size();
		ArrayList<String> phrases = new ArrayList<>();
		for (ArrayList<ArrayList<String>> arr : sentences) {
			if (arr.size() == size) {
				phrases.addAll(createPhrases(arr));
			}
		}
		Collections.sort(phrases);
		for (String p : phrases) {
			p = p.trim();
			System.out.println(p);
		}
	}

	public static void main(String[] args) {
		loadFiles();
		// Get input from stdin
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		// Parse the style and morsed code value
		scanner.close();
		String[] parts = command.split(":");
		String style = parts[0].trim();
		String morsed = parts[1].trim();

		switch (style) {
		case "Spaced Letters":
			handleSpacedLetters(morsed);
			break;
		case "Word":
			handleWord(morsed);
			break;
		case "Spaced Words":
			handleSpacedWords(morsed);
			break;
		case "Sentence":
			handleSentence(morsed);
			break;
		}

	}

	static class TrieNode {

		HashMap<Character, TrieNode> children = new HashMap<>();
		ArrayList<String> words = new ArrayList<>();

		public static void addWord(TrieNode root, String newWord) {
			TrieNode current = root;
			String morsed = charToMorse(newWord);
			for (char letter : morsed.toCharArray()) {
				if (!current.children.containsKey(letter)) {
					current.children.put(letter, new TrieNode());
				}
				current = current.children.get(letter);
			}
			current.words.add(newWord);
		}

		public static boolean hasWord(TrieNode root, String goal) {
			TrieNode current = root;
			for (char letter : goal.toCharArray()) {
				if (current.children.containsKey(letter)) {
					current = current.children.get(letter);
				} else {
					return false;
				}
			}
			return true;
		}

		public static ArrayList<String> getAllWords(TrieNode root) {
			ArrayList<String> result = new ArrayList<>();
			if (!root.words.isEmpty()) {
				result.addAll(root.words);
			}
			Collections.sort(result);
			return result;
		}

		public static ArrayList<String> autoComplete(TrieNode root, String start) {
			TrieNode current = root;
			for (char letter : start.toCharArray()) {
				if (current.children.containsKey(letter)) {
					current = current.children.get(letter);
				} else {
					return new ArrayList<String>();
				}
			}
			return getAllWords(current);
		}
	}
}
