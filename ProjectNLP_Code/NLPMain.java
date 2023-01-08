import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;


public class NLPMain {

	static final String MYSTERY_FILENAME = "mystery.txt";
	
	/**
	 * Display usage information of the program to user screen
	 */
	public static void printUsage() {
		System.out.println("Usage: java NLPMain <n> <local_folder>\n");
		System.out.println("where the arguments are as below:");
		System.out.println("n              value of n for n-gram model");
		System.out.println("local_folder   path of folder containing mystery.txt and language data files.\n");
	}
	
	/**
	 * Update a given language model from words read from a file.
	 * @param filename path of input file
	 * @param model given NGram model instance
	 */
	public static void updateModelFromFile(NGram model, String filename) throws Exception {
		Scanner sc = new Scanner(new File(filename));
		sc.tokens().forEach(word -> {
			word = word.replaceAll("\\p{Punct}", "").toLowerCase();
			model.update(word);
		});
		sc.close();
	}
	
	public static void main(String[] args) {
		// Check proper invocation of the program
		if(args.length != 2) {
			printUsage();
			return;
		}
		// Save the value of n from first command line argument
		int n = Integer.parseInt(args[0]);
		
		// Save the value of folder path from second command line argument
		String folderPath = args[1];
		File path = new File(folderPath);
		if(!path.exists()) {
			System.out.println("folder path does not exist!");
			return;
		}
		if(!path.isDirectory()) {
			System.out.println("folder path is not a directory!");
			return;
		}		
		
		// create the language model from file mystery.txt
		NGram mysteryModel = new NGram("unknown", n);
		String mysteryFilename = path.getAbsolutePath()+"/" + MYSTERY_FILENAME; 
		try {
			updateModelFromFile(mysteryModel, mysteryFilename);
			// System.out.println(mysteryModel.toString());
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			return;
		}
		
		// parallelly process all language sub-folders from source folder
		// using stream and lambda
		try {
			Path srcFolderPath = Paths.get(folderPath);
			// get the stream of language folder path
			Stream<Path> langPaths =  Files.list(srcFolderPath).filter(entryPath -> {
				String entryName = entryPath.toString();
				File f = new File(entryName);				
				return (f.isDirectory() && f.getName().matches("lang\\-[a-z]{2}"));
			});
			
			// process each language folder parallelly and get a stream of the
			// language models
			Stream<NGram> langModels = langPaths.parallel().map( p -> {
				// extract the language from path
				String langFolderPath = p.toString();
				int index = langFolderPath.indexOf('-');
				String language = langFolderPath.substring(index + 1);
				NGram model = new NGram(language, n);
				// parallelly process all .txt files from language folder
				// and update the model
				try {
					Stream<Path> textFilePaths =  Files.list(p)
							.filter(Files::isRegularFile)
							.filter( fPath -> fPath.getFileName().toString().endsWith(".txt"));
					textFilePaths.parallel()
					.forEach(textFilePath -> {
						try {
							//System.out.println("<" + textFilePath.toFile().toString() + ">");
							updateModelFromFile(model, textFilePath.toFile().toString());
						} catch (Exception e) {
							System.out.println("ERROR: " + e.getMessage());
						}
					});
				} catch (IOException e) {
					System.out.println("ERROR: " + e.getMessage());
				}
				return model;
			});
			// Process all language models and compute distance from mystery model
			Stream<ModelDistance> modelDistance = langModels.map(lModel -> {
				double dist = mysteryModel.distanceFrom(lModel);
				return new ModelDistance(lModel, dist);
			});
			
			// Get the best matched model (with max distance value)
			ModelDistance bestMatch = modelDistance.max(Comparator.comparing(ModelDistance::getDistance)).get();
			
			// Display the best matched model language
			System.out.println("Best matched language of mystery.txt: " + bestMatch.getModel().getLangauge());
			
		}catch(Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		
	}

}
