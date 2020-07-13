
public class Node {
	
	// boolean value for node accepting or not
	private boolean accept;
	
	// stores node's string
	private String str;
	
	// stores accepting files and word's index
	private String[][] accepted_files = new String[0][0];
	
	// children
	private Node children[] = new Node[0];
	
	protected Node(String str, boolean accept) {
		this.str = str;
		this.accept = accept;
	}
	
	
	public void setStr(String str) {
		this.str = str;
	}
	
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	
	public void setChildren(Node children[]) {
		this.children = children;
	}
	
	public void setAcceptedFiles(String accepted_files[][]) {
		this.accepted_files = accepted_files;
	}
	
	public boolean getAccept() {
		return accept;
	}
	
	public String getStr() {
		return this.str;
	}

	public String[][] getAcceptedFiles() {
		return accepted_files;
	}
	
	public Node[] getChildren(){
		return this.children;
	}
	
	// add a accepted file to 2d array
	public void add_accepted_file(String file_no, String word_index) {
		int size = accepted_files.length;
		
		String temp[][] = new String[size + 1][2];
		for(int i = 0; i < size; i++) {
			temp[i][0] = accepted_files[i][0];	// first index stores file's name
			temp[i][1] = accepted_files[i][1];	// second index stores word's index
		}
		
		temp[size][0] = file_no;
		temp[size][1] = word_index;
		
		accepted_files = temp;
	}
	
	// add a child to trie
	public void add_child(String str, boolean accept) {
		int size = children.length;
		Node temp[] = new Node[size + 1];
		for(int i = 0; i < size; i++)
			temp[i] = children[i];
		
		temp[size] = new Node(str, accept);
		
		children = temp;
	}
	
	// remove a child from trie
	public void remove_child() {
		setStr(getStr() + children[0].getStr());
		setAccept(children[0].getAccept());
		try {
			setAcceptedFiles(children[0].getAcceptedFiles());
		}
		catch(Exception e) {
		}
		
		setChildren(children[0].getChildren());
	}
	
	
}