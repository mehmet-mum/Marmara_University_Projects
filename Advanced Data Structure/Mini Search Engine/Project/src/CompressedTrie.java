import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class CompressedTrie {
	static Node compressed_trie;
	static int frequency;
	static String[] frequent_words = new String[0];
	static Scanner scan;
	static BufferedReader reader;
	static File[] listOfFiles;
	static boolean found;
	public static void main(String[] args) throws IOException {
		compressed_trie = new Node("", false);
		
		scan = new Scanner(System.in);
		reader =new BufferedReader(new InputStreamReader(System.in));
		
		// get a valid directory from user that has contains at least 1 txt file
		while(true) {
			System.out.print("Enter a directory: ");
			String directory = scan.next();
			
			boolean contains = false;
			File folder = new File(directory);
			if (folder.isDirectory()) {
				listOfFiles = folder.listFiles();
				for (int i = 0; i < listOfFiles.length; i++) {
					  File file = listOfFiles[i];
					  if (file.isFile() && file.getName().endsWith(".txt")) {
					    process_txt_file(file);		// process the file
					    contains = true;
					  }
					  else {
						  listOfFiles[i] = null;
					  }
				}
			}
			else {
				System.out.println("This is not a valid directory.");
				break;
			}
			
			if (contains)
				break;
			
			System.out.println("This directory does not contain any txt file.");
		}
		
		// Trie is created
		System.out.println("Compressed Trie is created successfully!");
		
		// Compress the Trie
		compress_trie(compressed_trie);
		
		// Take query from user
		query();
	}
	
	
	// insert a node to Trie
	public static void insert_trie(Node trie, String text, String file_name, int word_index) {
		Node children[] = trie.getChildren();
		int num_of_children = children.length;
		for(int i = 0; i < num_of_children; i++) {
			
			// if the searching char is found at a node's child go that child and search next char
			if (children[i].getStr().charAt(0) == text.charAt(0)) {
				
				// if the char is end of the string then set accept value true and finish inserting chars of the string
				if(text.length() == 1) {
					children[i].setAccept(true);
					children[i].add_accepted_file(file_name, Integer.toString(word_index));		// save file name and words index
				}
				else {
					// if it is not end char then continue inserting with next char
					insert_trie(children[i], text.substring(1), file_name, word_index);
				}
				return;
			}
			
			
		}
		
		// node not found then insert the char as a new child of the node
		if(text.length() == 1) {
			trie.add_child(text.substring(0,1), true);
			trie.getChildren()[num_of_children].add_accepted_file(file_name, Integer.toString(word_index));
		}
		else {
			trie.add_child(text.substring(0,1), false);
			insert_trie(trie.getChildren()[num_of_children], text.substring(1), file_name, word_index);
		}
	}
	
	
	// Compress the Trie ( do it by DFS )
	// if node has only one child and it is not accepted node
	// remove the child
	public static void compress_trie(Node trie) {
		Node children[] = trie.getChildren();
		
		for (int i = 0; i < children.length; i++ )
			compress_trie(children[i]);
		
		boolean cond = trie.getAccept();
		if( children.length == 1 && !cond)
			trie.remove_child();
	}
	
	// process the txt file
	public static void process_txt_file(File file) throws IOException {
		// read content of file 
		String fileString = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
		 
		// remove \t \n \r \b \f \' \"
		fileString = fileString.replaceAll("[\t\n\b\f\r\'\"]", "");
		
		// replace punctuation with blank char
		fileString = fileString.replaceAll("\\p{Punct}", " ");
		
		// Split words with blank char
		String words[] = fileString.replace("  ", " ").split(" ");
		int number_of_words = words.length;
		int last_index = 0;
		int index;
		// insert each word into Trie
		for(int i = 0; i < number_of_words; i++) {
			if(words[i].length() != 0) {
				index = fileString.indexOf(words[i], last_index) + 1 ;
				insert_trie(compressed_trie,words[i].toLowerCase(Locale.ENGLISH), file.getName(), index);
				last_index = index;
			}
		}
	}
	
	
	// checks the given file is exist in the directory for query section
	public static boolean confirm_file(String file_name) {
		for (int i = 0; i < listOfFiles.length; i++) {
			if (file_name.equals(listOfFiles[i].getName()))
				return true;
		}
		
		return false;
	}
	
	// Possible queries
	public static void query() throws IOException {
		int choice = 0;
		String input_word, input_file;
		
		while(true) {
			System.out.println("\nSelect one of the given queries."
					+ "\n1. Search a word in a given text file."
					+ "\n2. Find most frequent word(s) in a given text file."
					+ "\n3. Find text file(s) include words starting with given pattern."
					+ "\n4. Find common word(s) in the given text files."
					+ "\n5. Exit");
			
			System.out.print("Select one of the choices: ");
			choice = scan.nextInt();
			
			switch(choice) {
				case 1:
					// take a word and a file from user
					System.out.print("Enter a word: ");
					input_word = reader.readLine(); 	
					
					System.out.print("Enter a text file: ");
					input_file = reader.readLine(); 
					
					if(!confirm_file(input_file)) {
						System.out.println("File is not valid.");
						break;
					}
					
					found = false;
					
					// search the given word in a given file
					search_word(compressed_trie,input_word.toLowerCase(Locale.ENGLISH), input_file, "");
					
					if(!found)
						System.out.println(input_file + " does not contain " + input_word + ".");
					break;
				case 2:
					// take a file from user
					System.out.print("Enter a text file: ");
					input_file = reader.readLine(); 
					
					if(!confirm_file(input_file)) {
						System.out.println("File is not valid.");
						break;
					}
					
					// number of frequency of most frequent word(s)
					frequency = 0;
					
					// find most frequent word in a given file
					find_most_frequent_word(compressed_trie ,input_file, "");
					
					// print the words
					System.out.println("Most frequent word(s) with frequency: " + frequency);
					for(int i = 0; i < frequent_words.length; i++) {
						System.out.print(frequent_words[i] + "	");
					}
					System.out.println("\n");

					break;
				case 3:
					// take a pattern from user
					System.out.print("Enter a pattern: ");
					input_word = reader.readLine();
					
					// find text files and words which starts with the given pattern
					find_text_files(compressed_trie, input_word.toLowerCase(Locale.ENGLISH), "");
					break;
				case 4:
					// take files from user
					System.out.print("Enter text files: ");
					input_file = reader.readLine();
					
					// separate files
					String files[] = input_file.split(" ");
					boolean not_valid = false;
					for(int i = 0; i < files.length; i++) {
						if (!confirm_file(files[i])) {
							not_valid = true;
							break;
						}
					}
					if(not_valid) {
						System.out.println("At least one of the files is not valid.");
						break;
					}
					
					found = false;
					
					// find common words in given files
					find_common_words(compressed_trie, files, "");
					
					
					if(!found)
						System.out.println(input_file + " do not have common words.");
					break;
				case 5:
					return;
			}
			
		}
		
		
	}
	
	// Search a word in a given text file.
	// Walk down trie until there is no match
	// if the ending node is accepted and word empty print it with it's index
	public static void search_word(Node trie, String word, String file, String result) {
		Node children[] = trie.getChildren();
		
		for(int i = 0; i < children.length; i++) {
			String child_str = children[i].getStr();
			
			// compare word's prefix with node's stored string
			if (child_str.length() <= word.length() && child_str.equals(word.subSequence(0, child_str.length()))) {
				
				// remove matched substring from word, and check it's length, if length is 0 this means word is found
				if( word.substring(child_str.length()).length() == 0) {
					
					// if the final node is accepting in given file print the word with it's index
					if(children[i].getAccept()) {
						String[][] accepted_files = children[i].getAcceptedFiles();

						for( int k = 0; k < accepted_files.length; k++) {
							if( accepted_files[k][0].equals(file)) {
								System.out.println(result + word.subSequence(0, child_str.length()) + " is found	word_index: " + accepted_files[k][1]);	
								found = true;
							}
						}
					}
					
				}
				else {
					search_word(children[i], word.substring(child_str.length()), file, result + word.subSequence(0, child_str.length()));
				}
			}
			
		}
	}
	
	
	// find most frequent word in a given file
	// Walk down with DFS in the trie
	// For accepting nodes check this file exist or not
	// If it is exist count and compare with global frequency value
	// if it is bigger than the frequency value clear frequent words array and add new word to the array then change global frequency value
	public static void find_most_frequent_word(Node trie, String file, String result) {
		Node children[] = trie.getChildren();
		
		for( int i = 0; i < children.length; i++) {
			if(children[i].getAccept()) {
				int no = 0;
				String accepted_files[][] = children[i].getAcceptedFiles();
				for(int j = 0; j < accepted_files.length; j++) {
					if(accepted_files[j][0].equals(file))
						no++;
				}
				
				if(no > frequency) {
					frequency = no;
					frequent_words = new String[0];
					add_frequent_word(result + children[i].getStr());
				}
				else if( no == frequency){
					add_frequent_word(result + children[i].getStr());
				}
			}
			
			find_most_frequent_word(children[i], file, result + children[i].getStr());
			
		}
		
	}
	
	// find words which starts with given pattern
	
	public static void find_text_files(Node trie, String pattern, String result) {
		Node children[] = trie.getChildren();
		
		String accept_files[][];
		for(int i = 0; i < children.length; i++) {
			
			// if pattern length is 0 and node is accepting then print the word with file_name and word index in that file
			if(pattern.length() == 0) {
				if(children[i].getAccept()) {
					accept_files = children[i].getAcceptedFiles();
					for(int j = 0; j < accept_files.length; j++) {
						System.out.println(result + children[i].getStr() + " is in file " + accept_files[j][0] + " with word index "
								+ accept_files[j][1]);
					}
				}
				
				find_text_files(children[i], pattern, result + children[i].getStr());
			}
			
			// if pattern length is no zero, that means we are still looking for pattern
			else {
				
				// if the pattern length is equal node's str length check they are equal or not
				// if they are equal and node is accepting print the word
				// continue searching with empty pattern
				// print all other accepted words under the node
				if(children[i].getStr().length() == pattern.length()) {
					if(children[i].getStr().equals(pattern)) {
						if(children[i].getAccept()) {
							accept_files = children[i].getAcceptedFiles();
							for(int j = 0; j < accept_files.length; j++) {
								System.out.println(result + children[i].getStr() + " is in file " + accept_files[j][0] + " with word index "
										+ accept_files[j][1]);
							}	
						}
						
						find_text_files(children[i], "", result + children[i].getStr());
					}
				}
				
				// if length of node's str is bigger than pattern length
				// check prefix of node's str with pattern
				// if they are equal then do the same process  above if statement 
				else if(children[i].getStr().length() > pattern.length()) {
					if(children[i].getStr().substring(0, pattern.length()).equals(pattern)) {
						if(children[i].getAccept()) {
							accept_files = children[i].getAcceptedFiles();
							for(int j = 0; j < accept_files.length; j++) {
								System.out.println(result + children[i].getStr() + " is in file " + accept_files[j][0] + " with word index "
										+ accept_files[j][1]);
							}	
						}
						find_text_files(children[i], "", result + children[i].getStr());
					}
				}
				
				// if length of node's str is less than pattern length
				// compare pattern's prefix with node's str if they are equal
				// continue search with deleting pattern's prefix
				else{
					if(children[i].getStr().equals(pattern.substring(0, children[i].getStr().length()))) {
						find_text_files(children[i], pattern.substring(children[i].getStr().length()), result + children[i].getStr());
					}
				}
			}
		}
	}
	
	
	// find common words in given text files
	// search for all node's
	public static void find_common_words(Node trie,String files[], String result) {
		Node children[] = trie.getChildren();
		
		for( int i = 0; i < children.length; i++) {
			// if node is accepting then search for files
			// if files is in the node that means this word is common for given files
			if( children[i].getAccept() ) {
				int no_files = 0;
				String accepted_files[][] = children[i].getAcceptedFiles();
				for(int j = 0; j < files.length; j++) {
					for(int k = 0; k < accepted_files.length; k++) {
						if(accepted_files[k][0].equals(files[j])) {
							no_files++;
							break;
						}
					}
				}
				
				if(no_files == files.length) {
					System.out.println(result + children[i].getStr() + " is common word.");
					found = true;
				}
			}
			
			find_common_words(children[i], files, result + children[i].getStr());
			
		}
	}
	
	
	
	public static void add_frequent_word(String word) {
		String temp[] = new String[frequent_words.length + 1];
		
		for(int i = 0; i < frequent_words.length; i++) {
			temp[i] = frequent_words[i];
		}
		
		temp[frequent_words.length] = word;
		
		frequent_words = temp;
	}
	
}
