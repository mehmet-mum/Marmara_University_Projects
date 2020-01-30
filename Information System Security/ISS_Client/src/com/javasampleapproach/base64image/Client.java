package com.javasampleapproach.base64image;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client implements ActionListener{
	
    /* main frame has
     * 4 buttons ( register, login, logout, exit )
     * 2 text fields for username and password
     * 2 labes for username and password
     * 1 error label
     */
    static JFrame mainMenuFrame;
    static MainMenuPanel mainMenuPanel;
    static JButton registerButton;
    static JButton loginButton;
    static JButton logoutMainMenu;
    static JButton exitMainMenu;
    static RoundJTextField usernameText;
    static JLabel usernameLabel;
    static RoundJTextField passwordText;
    static JLabel passwordLabel;
    static JLabel mainMenuErrorLabel;
    
    /* Application Frame has 
     * 7 buttons ( upload , download, logout, exit )
     * ( download location select button , file select button )
     * ( notificaiton button  ( it is visible when notification comes ) )
     * text field uneditable shows selected file's name
     * error label
     * userlabel left top corner shows username 
     */
    static JFrame appFrame;
    static AppPanel appPanel;
    static JButton uploadButton;
	static JButton downloadButton;
	static JButton fileSelector;
	static JButton downloadLocation;
	static JButton logoutAppFrame;
	static JButton exitAppFrame;
	static JButton notificationButton;
	static JTextField imageText;
	static JLabel appErrorLabel;
	static JLabel userLabel;
	
	/* Download diaglog
	 * It becomes visible if user clicks on download button
	 * Scroll Pane for downloadable files
	 * Download button and cancel button 
	 */
	static JDialog downloadDialog;
	static JPanel downloadPanel;
	static JLabel downloadLabel;
	static JButton downloadImageButton;
	static JButton downloadCancelButton;
	static JList downloadList;
	static JScrollPane downloadScroller;
	
	/* Post dialog 
	 * It becomes visible if user clicks on download button
	 * Scroll Pane for registered users
	 * Download button and cancel button
	 */
	static JDialog postDialog;
	static JPanel postPanel;
	static JScrollPane postScrollPane;
	static JLabel postLabel;
	static JButton postCancelButton;
	static JButton postButton;
	static JCheckBox checkBoxes[];
	static Box box;
	
	/* Notification dialog 
	 * notifications in a scroll pane
	 */
	static JDialog notificationDialog;
	static DefaultListModel model;
	static JPanel notificationPanel;
	static JList notificList;
	static JScrollPane notificationScroll;
	static JButton closeButton;
	static int modelIndex;
    
	
    PublicKey serverPublicKey = null;	// server's public key
    PublicKey pubKey = null;			// user's public key 
    PrivateKey privKey = null;			// user's private key
    
    ObjectOutputStream oos;		// write to socket
	ObjectInputStream ois;		// read from socket
	InetAddress host;			// to get local ip
	Socket s;					// socket
	Boolean success;			// login succes control boolean variable
	String uploadImagePath;		// upload file's absolute path
	String downloadPath;		// download file's absolute path
	static int seconds = 0;		// timer 
	static TimerTask task;		
	static Timer timer;
	static Boolean runningTimer = false;
	static Boolean registerControl; // register success control
	
	
	static int numberOfClients;				// number of clients in the server
	static int numberOfImages;				// number of downloadable files in the server
	static int numberOfNotifications;		// number of notifications in the server
	static String[] clientNames;			// user array
	static String[] imageList;				// downloadable files array
	static String[] notificationList;		// notification array
    
	public Client() {
    	/*-------------- Main Menu Frame ---------------*/
    	
    	mainMenuFrame = new JFrame("ImShar");
    	
    	mainMenuFrame.setBounds(400, 200, 600, 400);
    	mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainMenuFrame.setResizable(false);
        
    	mainMenuPanel = new MainMenuPanel();
    	mainMenuPanel.setLayout(null);
        mainMenuFrame.add(mainMenuPanel);
        
        registerButton = new JButton("REGISTER");
        registerButton.setBounds(155, 120, 100, 30);
        registerButton.setBackground(Color.ORANGE);
        registerButton.addActionListener(this);
        registerButton.setFont(new Font(null, Font.BOLD, 13));
        
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(50, 120, 100, 30);
        loginButton.setBackground(Color.ORANGE);
        loginButton.addActionListener(this);
        loginButton.setFont(new Font(null, Font.BOLD, 13));
        
        logoutMainMenu = new JButton("LOGOUT");
        logoutMainMenu.setBounds(50, 155, 100, 30);
        logoutMainMenu.setBackground(Color.ORANGE);
        logoutMainMenu.addActionListener(this);
        logoutMainMenu.setFont(new Font(null, Font.BOLD, 13));
        
        exitMainMenu = new JButton("EXIT");
        exitMainMenu.setBounds(155, 155, 100, 30);
        exitMainMenu.setBackground(Color.ORANGE);
        exitMainMenu.addActionListener(this);
        exitMainMenu.setFont(new Font(null, Font.BOLD, 13));
        
        usernameText = new RoundJTextField(20);
        usernameText.setBounds(135, 40, 120, 20);
        usernameText.setFont(new Font(null, Font.BOLD, 12));
        
        usernameLabel = new JLabel();
        usernameLabel.setText("Username");
        usernameLabel.setBounds(50, 40, 100, 20);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font(null, Font.BOLD, 15));
        
        
        passwordText = new RoundJTextField(20);
        passwordText.setBounds(135, 65, 120, 20);
        passwordText.setFont(new Font(null, Font.BOLD, 12));
        
        passwordLabel = new JLabel();
        passwordLabel.setText("Password");
        passwordLabel.setBounds(50, 65, 100, 20);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font(null, Font.BOLD, 15));
        
        mainMenuErrorLabel = new JLabel();
        mainMenuErrorLabel.setBounds(50, 90, 400, 20);
        mainMenuErrorLabel.setForeground(Color.WHITE);
        mainMenuErrorLabel.setFont(new Font(null, Font.BOLD, 13));
        
        mainMenuPanel.add(usernameLabel);
        mainMenuPanel.add(usernameText);
        mainMenuPanel.add(registerButton);
        mainMenuPanel.add(loginButton);
        mainMenuPanel.add(logoutMainMenu);
        mainMenuPanel.add(exitMainMenu);
        mainMenuPanel.add(passwordLabel);
        mainMenuPanel.add(passwordText);
        mainMenuPanel.add(mainMenuErrorLabel);
        mainMenuFrame.setVisible(true);
        
        /*----------------- APPLICATION FRAME -----------------*/
        appFrame = new JFrame("ImShar");
        appFrame.setBounds(400, 200, 600, 400);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setResizable(false);
        
        appPanel = new AppPanel();
        appPanel.setLayout(null);
        appFrame.add(appPanel);
        
        uploadButton = new JButton("UPLOAD");
		uploadButton.setBounds(200, 150, 150, 40);
        uploadButton.setBackground(Color.LIGHT_GRAY);
        uploadButton.setForeground(Color.blue);
        uploadButton.addActionListener(this);
        uploadButton.setFont(new Font(null, Font.BOLD, 15));
        
        downloadButton = new JButton("DOWNLOAD");
        downloadButton.setBounds(200, 200, 150, 40);
		downloadButton.setBackground(Color.LIGHT_GRAY);
		downloadButton.setForeground(Color.blue);
		downloadButton.setFont(new Font(null, Font.BOLD, 15));
		downloadButton.addActionListener(this);
        
		fileSelector = new JButton("...");
		fileSelector.setBounds(360, 150, 40, 40);
		fileSelector.setBackground(Color.LIGHT_GRAY);
		fileSelector.setForeground(Color.blue);
		fileSelector.setFont(new Font(null, Font.BOLD, 15));
		fileSelector.addActionListener(this);
		
		downloadLocation = new JButton("...");
		downloadLocation.setBounds(360, 200, 40, 40);
		downloadLocation.setBackground(Color.LIGHT_GRAY);
		downloadLocation.setForeground(Color.blue);
		downloadLocation.setFont(new Font(null, Font.BOLD, 15));
		downloadLocation.addActionListener(this);
		
		logoutAppFrame = new JButton("LOGOUT");
		logoutAppFrame.setBounds(200, 250, 200, 40);
		logoutAppFrame.setBackground(Color.LIGHT_GRAY);
		logoutAppFrame.setForeground(Color.blue);
		logoutAppFrame.setFont(new Font(null, Font.BOLD, 15));
		logoutAppFrame.addActionListener(this);
		
		exitAppFrame = new JButton("EXIT");
		exitAppFrame.setBounds(200, 300, 200, 40);
		exitAppFrame.setBackground(Color.LIGHT_GRAY);
		exitAppFrame.setForeground(Color.blue);
		exitAppFrame.setFont(new Font(null, Font.BOLD, 15));
		exitAppFrame.addActionListener(this);
		
		notificationButton = new JButton("",new ImageIcon("notification.png"));
		notificationButton.setBounds(550, 5, 35, 35);
		notificationButton.addActionListener(this);
		notificationButton.setBackground(Color.CYAN);
		notificationButton.setOpaque(false);
		notificationButton.setBorderPainted(false);
		notificationButton.setFocusPainted(false);
		notificationButton.setVisible(false);
		
		imageText = new JTextField(50);
		imageText.setBounds(200, 120, 200, 25);
		imageText.setFont(new Font(null, Font.BOLD, 12));
		imageText.setEditable(false);
		
		appErrorLabel = new JLabel();
		appErrorLabel.setBounds(200, 90, 300, 25);
		appErrorLabel.setFont(new Font(null, Font.BOLD, 12));
		appErrorLabel.setForeground(Color.YELLOW);
		
		userLabel = new JLabel();
		userLabel.setBounds(5, 5, 200, 25);
		userLabel.setFont(new Font(null, Font.BOLD, 15));
		userLabel.setForeground(Color.YELLOW);
		
		appPanel.add(appErrorLabel);
		appPanel.add(uploadButton);
		appPanel.add(downloadButton);
		appPanel.add(fileSelector);
		appPanel.add(downloadLocation);
		appPanel.add(logoutAppFrame);
		appPanel.add(exitAppFrame);
		appPanel.add(notificationButton);
		appPanel.add(imageText);
		appPanel.add(userLabel);
		appFrame.setVisible(false);
		
		
		/*---------------- Selecting Downloadable Image ------------------*/
		downloadDialog = new JDialog(downloadDialog,"Downloadable Images");
		downloadDialog.setBounds(570, 270, 270, 300);
        downloadDialog.setResizable(false);
        downloadDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        downloadPanel = new JPanel();
        downloadPanel.setLayout(null);
        downloadDialog.add(downloadPanel);
        
        downloadLabel = new JLabel("Select an image");
        downloadLabel.setBounds(5, 5, 200, 20);
        
        
        downloadCancelButton = new JButton("Cancel");
        downloadCancelButton.setBounds(100, 225, 90, 30);
        downloadCancelButton.addActionListener(this);
        
        
        downloadImageButton = new JButton("Download");
        downloadImageButton.setBounds(5, 225, 90, 30);
        downloadImageButton.addActionListener(this);
        
        
        downloadList = new JList();
        
        downloadScroller = new JScrollPane(downloadList);
        downloadScroller.setBounds(5, 30, 250, 188);
        
        downloadPanel.add(downloadLabel);
        downloadPanel.add(downloadCancelButton);
        downloadPanel.add(downloadImageButton);
        downloadPanel.add(downloadScroller);
        
        downloadDialog.setVisible(false);
        
        
        /*-------------- POST CLIENT SELECTION ----------------*/
        postDialog = new JDialog(postDialog,"Users");
		postDialog.setBounds(570, 270, 270, 300);
		postDialog.setResizable(false);
		postDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		postPanel = new JPanel(new GridLayout(0, 1));
		postPanel.setLayout(null);
		
		postDialog.add(postPanel);
		
		postLabel = new JLabel("Select users");
        postLabel.setBounds(5, 5, 200, 20);
        
        postCancelButton = new JButton("Cancel");
		postCancelButton.setBounds(100, 225, 90, 30);
		postCancelButton.addActionListener(this);
	    
		postButton = new JButton("Post");
		postButton.setBounds(5, 225, 90, 30);
		postButton.addActionListener(this);
		
		box = Box.createVerticalBox();
		
	    postScrollPane = new JScrollPane(box);
		postScrollPane.setBounds(5, 30, 250, 188);		
		
		postPanel.add(postButton);
		postPanel.add(postCancelButton);
		postPanel.add(postLabel);
		postPanel.add(postScrollPane);
		postDialog.setVisible(false);
		
		/*-------------- NOTIFICATION -------------*/
		
		model = new DefaultListModel();
		
		notificationDialog = new JDialog(notificationDialog,"Notifications");
		notificationDialog.setBounds(570, 270, 270, 300);
		notificationDialog.setResizable(false);
		notificationDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		notificationPanel = new JPanel();
		notificationPanel.setLayout(null);
		notificationDialog.add(notificationPanel);
		
		closeButton = new JButton("Close");
		closeButton.setBounds(165, 225, 90, 30);
		closeButton.addActionListener(this);
	    
		
		notificList = new JList(model);
		
		notificationScroll = new JScrollPane(notificList);
		notificationScroll.setBounds(5, 5, 250, 213);
		
		notificationPanel.add(notificationScroll);
		notificationPanel.add(closeButton);
		
		modelIndex = 0;
		notificationDialog.setVisible(false);
		
		/*-------------- Timer ----------------*/
		
		task = new TimerTask() {
	        public void run() {
	        	if ( runningTimer == true) {
		            if (seconds == 0) {
		            	seconds = 5;
		            	try{
		            		oos.writeObject("Notification");
		            		
		            		numberOfNotifications = (int) ois.readObject();
		            		notificationList = new String[numberOfNotifications];
		            		notificationList = (String[]) ois.readObject();
		            		
		            		
		            		if ( numberOfNotifications != 0 ) {
		            			for (int i = 0; i<numberOfNotifications; i++) {
		            				model.add(modelIndex, notificationList[i]);
		            				modelIndex++;
		            			}
		            			notificationButton.setVisible(true);
		            			
		            		}
		            	}
		            	catch (Exception e){
		            		
		            	}
		            }
		            else {
		            	seconds--;
		            	
		            }
	        	}
	        }
	    };
	    
	    timer = new Timer("Timer");
	    timer.scheduleAtFixedRate(task,1000,1000);
    }
    
    
    public void run() throws GeneralSecurityException, IOException {
    	serverPublicKey = loadPublicKey(getServerPublicKey()); // load server's public key
    	existenceUser();
    	String ip = null;
    	
    	// take server's ip address from user  
    	ip = JOptionPane.showInputDialog(
                new JFrame("IP adress"),
                "Enter IP Address of the Server:","",
                JOptionPane.INFORMATION_MESSAGE);
    	
    	// if ip address does not given local host
    	if ( ip == null ) {
    		host = InetAddress.getLocalHost();
        	s = new Socket(host.getHostName(),10750);
    	}
    	else {
        	s = new Socket(ip,10750);
    	}
    	
    	oos = new ObjectOutputStream(s.getOutputStream());
    	ois = new ObjectInputStream(s.getInputStream());
    	
    }
    public static void main(String[] args) throws Exception {
    	
    	Client client = new Client();
    	client.run();
    	
    }
    
    // the function converts file to string
    public static String encoder(String imagePath) {
		String base64Image = "";
		File file = new File(imagePath);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			base64Image = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
		return base64Image;
	}
	
	// the function converts string to file
	public static void decoder(String base64Image, String pathFile) {
		try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
			// Converting a Base64 String into Image byte array
			byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
			imageOutFile.write(imageByteArray);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
	}
	
	// encrypt plaintext with given Key and IV
	public static byte[] encrypt(String plainText, String encryptionKey, String IV) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	    return cipher.doFinal(plainText.getBytes("UTF-8"));
	  }
	
	// decrypt ciphertext with given key and IV
	public static String decrypt(byte[] cipherText, String encryptionKey, String IV) throws Exception{
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	    return new String(cipher.doFinal(cipherText),"UTF-8");
	  }
	
	// the function that converts string to private key
	public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.getDecoder().decode(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

	// the function that converts private key to string
    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    // the function that converts private key to string  ( so we can save PrivateKey in string )
    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = Base64.getEncoder().encodeToString(packed);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }

    // the function that converts public key to string  ( so we can save PublicKey in string )
    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return Base64.getEncoder().encodeToString(spec.getEncoded());
    }
    
    // the function encrypts plain text with public key
    public static byte[] encryptWithPubKey(byte[] input, PublicKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    // the function encrypts plain text with private key
    public static byte[] encryptWithPrivKey(byte[] input, PrivateKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
 // the function decrypts plain text with public key
    public static byte[] decryptWithPubKey(byte[] input, PublicKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    // the function decrypts plain text with private key
    public static byte[] decryptWithPrivKey(byte[] input, PrivateKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }	
    
    // get hash code of plain text ( SHA-256 )
    public static String getSHA(String input) 
    { 
  
        try { 
  
            // Static getInstance method is called with hashing SHA 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
            // digest() method called 
            // to calculate message digest of an input 
            // and return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            System.out.println("Exception thrown"
                               + " for incorrect algorithm: " + e); 
  
            return null; 
        } 
    }
    
    // the function gets server public key from txt file
    public String getServerPublicKey() throws FileNotFoundException {
    	File f = new File("server key\\publicKey.txt");
    	Scanner scan = new Scanner(f);
        
        String pubK = scan.next();
        scan.close();
        
        return pubK;
    }
    
    // check if there is a registered user at application does not let register again
    public void existenceUser() throws FileNotFoundException {
    	Scanner control = new Scanner(new File("user\\username.txt"));
		if (control.hasNext()) {
			registerButton.setEnabled(false);
			loginButton.setEnabled(true);
			
			// write the user to the username text field and dont let edit it
			usernameText.setText(control.next());
			usernameText.setEditable(false);
			
		}
		else {
			registerButton.setEnabled(true);
			loginButton.setEnabled(false);
		}
    }
    
    
    
    public void actionPerformed(ActionEvent e) {
    	// user want to register
		if(e.getSource() == registerButton) {
			
			try {
				String username = usernameText.getText();		// take username from text field
				String password = passwordText.getText();		// take password from text field
				registerControl = true;
				
				// username should be between 6 and 20 character
				if (username.length() >= 6 && username.length() <= 20) {
					for(int i = 0; i< username.length(); i++) {
						// also username should not contain space character
						if ( (int) username.charAt(i) == 32 ) {
							mainMenuErrorLabel.setText("Username should not contain space character!");
							registerControl = false;
							break;
						}
					}
				}
				
				// if password is less then 6 character or bigger than 20 character show error message
				if (password.length() < 6 || password.length() > 20) {
					registerControl = false;
					mainMenuErrorLabel.setText("Password should be between 6 and 20 characters!");
				}
				
				// if username is less then 6 character or bigger than 20 character show error message
				if(username.length() < 6 || username.length() > 20) {
					mainMenuErrorLabel.setText("Username should be between 6 and 20 characters!");
					registerControl = false;
				}
				
				// if username and password is appropriate then continue process
				if ( registerControl == true ) {
					
					// send to server register message 
					oos.writeObject("register");
					
					// take hash code of password
					password = getSHA(password);
		    		
					// send username and password to server
		    		oos.writeObject(username);
					oos.writeObject(password);
					
					// generate public and private key for the user
		    		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		        	keyPairGenerator.initialize(2048);
		        	KeyPair kp = keyPairGenerator.genKeyPair();// 2048 bit RSA; might take a second to generate keys
		            PublicKey pubKey = kp.getPublic();
		            PrivateKey privKey = kp.getPrivate();
		            
		            // server controls username if there is no registered user with same name then continue process
		            success = (Boolean) ois.readObject();
		            
		            
		            if ( success == true) {
		            	// send public key to server
						oos.writeObject(pubKey);
						
						// server certificates the public key take certificate 
						String certificate = (String) ois.readObject();	    		
			    		
						// extract username and public key from certificate 
						byte[] bytes = decryptWithPubKey(Base64.getDecoder().decode(certificate), serverPublicKey);
			    		certificate = new String(bytes) ;
			    		
			    		
			    		String certificateName = certificate.substring(0, username.length());
			    		certificate = certificate.substring(username.length()+1, certificate.length());    		
			    		
			    		// control username and public key
			    		if(pubKey.equals(loadPublicKey(certificate)) && certificateName.equals(username)){
			    			
			    			// save the username on the clien't pc   in username.txt file
			    			File file = new File("user\\username.txt");
			    			PrintWriter writer = new PrintWriter(file);
			    			writer.write(username);
			    			writer.close();
			    			
			    			// save public key 
			    			file = new File("user\\public_key.txt");
			    			writer = new PrintWriter(file);
			    			writer.write(savePublicKey(pubKey));
			    			writer.close();
			    			
			    			// save private key
			    			file = new File("user\\private_key.txt");
			    			writer = new PrintWriter(file);
			    			writer.write(savePrivateKey(privKey));
			    			writer.close();
			    			
			    			// enable login button  disable register button
			    			registerButton.setEnabled(false);
			    			loginButton.setEnabled(true);
			    			
			    			// write the username to username text field and set it uneditable
			    			usernameText.setText(username);
			    			usernameText.setEditable(false);
			    			passwordText.setText("");
			    			
			    			mainMenuErrorLabel.setText("Register is successful!");
			    			
			    		}
		            }
				
					else if ( success == false ) {
						mainMenuErrorLabel.setText("Username has already taken!");
						
					}
				}
			} catch (IOException | ClassNotFoundException | GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		// user wants to login
		else if (e.getSource() == loginButton) {
			
			try {
				
				// send login request message to server
				oos.writeObject("login");
				
				// take username and password from text fields
				String username = usernameText.getText();
				String password = passwordText.getText();
				
				// take hash code of password
				password = getSHA(password);
				
				// send username and password to server
				oos.writeObject(username);
				oos.writeObject(password);
				
				// if username and password is correct then server sends  TRUE
				success = (Boolean) ois.readObject();
				
				if ( success == true ) {
					
					// if username and password is correct 
					// then server check the user online or not
					// false means user is not online so user can login
					success = (Boolean) ois.readObject();
					if ( success == false) {
						
						// load public key from text field 
						Scanner fileScanner = new Scanner(new File("user\\public_key.txt"));
						pubKey = loadPublicKey(fileScanner.next());
						fileScanner.close();
						
						// load private key from text field
						fileScanner = new Scanner(new File("user\\private_key.txt"));
						privKey = loadPrivateKey(fileScanner.next());
			    		fileScanner.close();
			    		
			    		
			    		mainMenuErrorLabel.setText("Login is succesful!");
			    		
			    		// start timer
			    		runningTimer = true;
			    		
			    		// set userLabel to username
			    		userLabel.setText(username);
			    		
			    		// close main menu frame and open app frame
			    		mainMenuFrame.setVisible(false);
			    		appFrame.setVisible(true);
					}
					else {
						mainMenuErrorLabel.setText("User is online!"); // if user is online show error
					}
				}
				else {
					mainMenuErrorLabel.setText("Incorrect password!");	// if password is wrong show error
				}
			}
			catch (IOException | ClassNotFoundException | GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		// post process has two step
		
		/* -------------------- FIRST STEP -----------------*/
		// on the app frame there is a button called UPLOAD  it does this
		else if ( e.getSource() == uploadButton) {
			try {
				// stop timer
				runningTimer = false;
				appErrorLabel.setText("");  // clear error label
				
				// send server to get list message
				oos.writeObject("Get_List");
				
				// remove box from scroll pane and scroll pane from panel
				postPanel.remove(postScrollPane);
				postScrollPane.remove(box);
				
				// server sends to user  number of clients and client list
				numberOfClients = (int) ois.readObject();
				
				clientNames = new String[numberOfClients];
				
				clientNames = (String[]) ois.readObject();
				
				
				// create checkboxes for each client
				checkBoxes = new JCheckBox[numberOfClients];
				
				// create box
				box = Box.createVerticalBox();
				for (int i = 0; i < numberOfClients; i++) {
					// add each clickboxes to box
					checkBoxes[i] = new JCheckBox(clientNames[i]);
					box.add(checkBoxes[i]);
				}
				
				// create a scroll pane from box
				postScrollPane = new JScrollPane(box);
				postScrollPane.setBounds(5, 30, 250, 188);
				
				// add scroll pane to panel
				postPanel.add(postScrollPane);
				
				// send file name to server checks there is same file or not 
				oos.writeObject(imageText.getText());
				Boolean available = (Boolean) ois.readObject();
				
				// if file is selected and name is appropriate then open new frame for next step
				if ( uploadImagePath != null && available == true) {
					appFrame.setEnabled(false);
					postDialog.setVisible(true);
				}
				
				// if file is not selected show error and start timer
				else if (uploadImagePath == null ) {
					appErrorLabel.setText("Select a file!");
					seconds = 5;		
					runningTimer = true;
					
				// if name is not appropriate show error and start timer
				}else if (available == false) {
					seconds = 5;
					runningTimer = true;
					appErrorLabel.setText("Image name is no appropriate!");
				}
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		/* ------------------- SECOND STEP ----------------*/
		
		// opened frame there is a post button it does this
		else if ( e.getSource() == postButton ) {
			try {
				
				appErrorLabel.setText(""); // clear error label
				
				// send post image message to server
				oos.writeObject("Post_Image");
				
				// take image name from text field
				String imageName = imageText.getText();
				
				
				SecureRandom random = new SecureRandom();
				// 128 bits are converted to 16 bytes but it generated 24 bytes
				// 12 bytes become 16 bytes i do not know why 
				// so i use 12 
				byte randomBytes[] = new byte[12]; 
				
				// generate random encryption key
				random.nextBytes(randomBytes);
				String encryptionKey = Base64.getEncoder().encodeToString(randomBytes);
				
				// generate random IV
				random.nextBytes(randomBytes);
				String IV = Base64.getEncoder().encodeToString(randomBytes);
				
				// file to string
				String imageString = encoder(uploadImagePath);  
				
				/*--------------- Encrypt Image ---------------*/
				
				// encrypt image ( AES )
				byte[] cipher = encrypt(imageString, encryptionKey, IV);
				
				// encrypted image to string
				String cipherImage = Base64.getEncoder().encodeToString(cipher);
				
				
				/*--------------- DIGITAL SIGNATURE ------------------*/
				// take hash function of imageString
				String digitalSignature = getSHA(imageString);
				
				// sign hash function
				byte[] sign = encryptWithPrivKey(digitalSignature.getBytes("UTF-8"), privKey);
				
				// digital signature ( string )
				digitalSignature = Base64.getEncoder().encodeToString(sign);
				
				
				/* send to server
				 * image name 
				 * cipher file 
				 * digital signature
				 */
				oos.writeObject(imageName);
				oos.writeObject(cipherImage);
				oos.writeObject(digitalSignature);
				
				// check each  check boxes 
				for ( int i = 0 ; i < numberOfClients; i++) {
					
					// if check box is selected returns true
					if ( checkBoxes[i].isSelected() == true ) {
						
						// send selected user's name to server
						oos.writeObject(clientNames[i]);
						String certificate = new String();
						
						// server sends back his/her certificate
						certificate = (String) ois.readObject();
						
						// extract certificate get name and public key
			    		byte[] bytes = decryptWithPubKey(Base64.getDecoder().decode(certificate), serverPublicKey);
			    		certificate = new String(bytes) ;
			    		
			    		String certificateName = certificate.substring(0, clientNames[i].length());
			    		certificate = certificate.substring(clientNames[i].length()+1, certificate.length());
			    		
			    		if ( certificateName.equals(clientNames[i])) {
				    		// create public key variable from extracted certificate
				    		PublicKey clientPk = loadPublicKey(certificate);
				    		
				    		/*--------------- Encrypt AES key with Client ( downloader ) public key ----------------*/
							
							// encrypt aes key with selected user's public key
							byte[] encryptAesKey = encryptWithPubKey(encryptionKey.getBytes("UTF-8"), clientPk);
				
							// encrypted aes key ( string form )
							String encryptedAesKey = Base64.getEncoder().encodeToString(encryptAesKey);
							
							/*--------------- Encrypt IV with Client ( downloader ) public key ----------------*/
							
							// encrypt IV key with selected user's public key
							byte[] encryptIV = encryptWithPubKey(IV.getBytes("UTF-8"), clientPk);
				
							// encrypted IV ( string form )
							String encryptedIV = Base64.getEncoder().encodeToString(encryptIV);
							
							
							/*--------------------- UPLOAD IMAGE INFORMATIONS ------------------*/
							
							/* send to server 
							 * encrypted aes key
							 * IV
							 */
							
							oos.writeObject(encryptedAesKey);
							oos.writeObject(encryptedIV);
			    		}
			    		else {
			    			JOptionPane.showMessageDialog(new JFrame(),"Certificate is wrong.");
			    		}
					}
					
				}
				
				// when checkbox control is done clear imageText and upload file
				imageText.setText(null);
				uploadImagePath = null;
				
				// send server the message upload is completed
				oos.writeObject("Completed");
				
				JOptionPane.showMessageDialog(new JFrame(), imageName + " has been posted succesfully.");
				
				// app frame is editable
				appFrame.setEnabled(true);
				
				// posting file frame is invisible
				postDialog.setVisible(false);
				
				// start timer
				seconds = 5;
				runningTimer = true;
				
				
				
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//download process has two steps
		
		/* ----------------- FIRST STEP ---------------*/
		else if ( e.getSource() == downloadButton ) {
			try {
				runningTimer = false; // stop timer
				appErrorLabel.setText(""); // clear error label
				
				// send server to get image message
				oos.writeObject("Get_Images");
				
				// remove list from scroller and scroller from panel
				downloadScroller.remove(downloadList);
				downloadPanel.remove(downloadScroller);
				
				// server sends number of downloadable file and file list
				numberOfImages = (int) ois.readObject();
				
				imageList = new String[numberOfImages];
				
				imageList = (String[]) ois.readObject();
				
				// create a new list from file list
				downloadList = new JList(imageList);
				
				// create a new scroller from list 
				downloadScroller = new JScrollPane(downloadList);
				downloadScroller.setBounds(5, 30, 250, 188);
		        
				// add scroller to panel
				downloadPanel.add(downloadScroller);
		        
				
		        // if download path is not empty open next frame
		        if ( downloadPath != null) {
		        	appFrame.setEnabled(false);
		        	downloadDialog.setVisible(true);
		        }
		        
		        // if download path is empty start timer and show error message
				else if ( downloadPath == null ) {
					seconds = 5;
					runningTimer = true;
					appErrorLabel.setText("Select an downlaoad location!");
				}
				
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		/* -------------------- SECOND STEP -----------------*/
		
		// opened frame there is a button called Download it does this
		else if ( e.getSource() == downloadImageButton ) {
			try {
				appErrorLabel.setText("");	// clear error label
				appFrame.setEnabled(true);
				
				// send server the Download image message
				oos.writeObject("Download_Image");
				
				// take selected file name
				String imageName = (String) downloadList.getSelectedValue();
				
				/* send image name which is wanted to be downloaded */
				
				oos.writeObject(imageName);
				
				String ownerName = new String();
    	    	String cipherImage = new String();
    	    	String digitalSignature = new String();
    	    	String encryptedAesKey = new String();
    	    	String IV = new String();
    	    	String ownerCertificate = new String();
				
    	    	
    	    	/*take from server
    	    	 * owner name of image file
    	    	 * encrypted image 
    	    	 * digital signature
    	    	 * encrypted aes key ( with downloader public key )
    	    	 * encrypted IV key ( with downloader public key )
    	    	 * owner certificate
    	    	 */
				ownerName = (String) ois.readObject();
				cipherImage = (String) ois.readObject();
				digitalSignature = (String) ois.readObject();
				encryptedAesKey = (String) ois.readObject();
				IV = (String) ois.readObject();
				ownerCertificate = (String) ois.readObject();
				
				
				/*------------------ Extract encrypted aes key ------------------*/
				byte[] encryptAesKey = decryptWithPrivKey(Base64.getDecoder().decode(encryptedAesKey), privKey);
				encryptedAesKey = new String(encryptAesKey) ;
	    		
				/*------------------ Extract encrypted IV -----------------------*/
				byte[] encryptIV = decryptWithPrivKey(Base64.getDecoder().decode(IV), privKey);
				IV = new String(encryptIV) ;
				
				/*------------------ Decrypt image ------------------------*/
				String image = decrypt(Base64.getDecoder().decode(cipherImage), encryptedAesKey, IV);
				
				
				/*------------------ Check Authentication and Message Integrity -----------------*/
				
				// dectrypt certificate
				byte[] certificateCheck = decryptWithPubKey(Base64.getDecoder().decode(ownerCertificate), serverPublicKey);
				
				
				String verificationOwnerPublicKey = new String(certificateCheck) ;
				// take certificate owner name
				// take certificate public key
				String verificationOwnerName = verificationOwnerPublicKey.substring(0, ownerName.length());
	    		verificationOwnerPublicKey = verificationOwnerPublicKey.substring(ownerName.length()+1, verificationOwnerPublicKey.length());
				byte[] signature = decryptWithPubKey(Base64.getDecoder().decode(digitalSignature), loadPublicKey(verificationOwnerPublicKey));
	    		
				// signatured hash(image)
				digitalSignature = new String(signature);
	    		
	    		// take hash function of decrypted image 
	    		String checkDS = getSHA(image);
	    		
	    		
	    		// check authentication and message integrity
				if( digitalSignature.equals(checkDS) && ownerName.equals(verificationOwnerName) ) {
					decoder(image, downloadPath + "\\" + imageName );
					JOptionPane.showMessageDialog(new JFrame(), imageName + " has been downloaded succesfully.");
				}
				else {
					JOptionPane.showMessageDialog(new JFrame(), imageName + " is not reliable.");
				}
				
				// clear contents and  start timer again
				imageText.setText(null);
				downloadPath = null;
				downloadDialog.setVisible(false);
				seconds = 5;
				runningTimer = true;
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		// file selector for uploading
		else if ( e.getSource() == fileSelector) {
			appErrorLabel.setText("");
			JFileChooser chooser = new JFileChooser();
		 
		    
			int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	uploadImagePath = chooser.getSelectedFile().getAbsolutePath();
		    	imageText.setText(chooser.getSelectedFile().getName());
		    }
		}
		// location selector for downloading
		else if ( e.getSource() == downloadLocation) {
			appErrorLabel.setText("");
			JFileChooser chooser = new JFileChooser();
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
			int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	downloadPath = chooser.getSelectedFile().getAbsolutePath();
		    	imageText.setText(chooser.getSelectedFile().getPath());
		    }
		}
		
		// download cancel button
		else if ( e.getSource() == downloadCancelButton) {
			// clear contents and start timer
			appFrame.setEnabled(true);
			imageText.setText(null);
			downloadPath = null;
			downloadDialog.setVisible(false);
			seconds = 5;
			runningTimer = true;
		}
		
		// post cancel button
		else if ( e.getSource() == postCancelButton ) {
			try {
				// if user give up to posting  send server cancel post message 
				// and server removes the imagename from incoming post array
				oos.writeObject("Cancel_Post");
				oos.writeObject(imageText.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// clear contents and start timer
			appFrame.setEnabled(true);
			imageText.setText(null);
			uploadImagePath = null;
			postDialog.setVisible(false);
			seconds = 5;
			runningTimer = true;
		}
		
		// notification button appears when a notification comes  it shows notifications
		else if( e.getSource() == notificationButton ) {
			notificationDialog.setVisible(true);
		}
		
		// closes notification frame
		else if ( e.getSource() == closeButton ) {
			notificationDialog.setVisible(false);
			model.removeAllElements();
			modelIndex = 0;
			notificationButton.setVisible(false);
		}
		
		// user logs out on the app frame returns main menu 
		// if wants to login again he/she should be register with different username
		else if ( e.getSource() == logoutAppFrame ) {
			FileWriter fw;
			try {
				// clear username.txt file
				fw = new FileWriter(new File("user\\username.txt"));
				fw.write("");
		    	fw.close();
		    	appFrame.setVisible(false);
		    	runningTimer = false;
		    	
		    	// server removes user from online list
		    	oos.writeObject("Log_out");
		    	existenceUser();
		    	usernameText.setText(null);
		    	usernameText.setEditable(true);
		    	passwordText.setText(null);
		    	mainMenuErrorLabel.setText(null);
		    	mainMenuFrame.setVisible(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
		}
		
		// log out main menu frame
		else if ( e.getSource() == logoutMainMenu ) {
			FileWriter fw;
			try {
				// delete from username.txt file 
				fw = new FileWriter(new File("user\\username.txt"));
				fw.write("");
		    	fw.close();
		    	existenceUser();
		    	usernameText.setText(null);
		    	usernameText.setEditable(true);
		    	passwordText.setText(null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// exit ( on the app frame button )
		else if ( e.getSource() == exitAppFrame) {
			try {
				oos.writeObject("Exit");
				System.exit(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		// exit ( on the main menu button )
		else if ( e.getSource() == exitMainMenu ) {
			System.exit(0);
		}
	}
}

// special panel for main menu panel
class MainMenuPanel extends JPanel{
	
	
	public MainMenuPanel(){
		super();
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(new ImageIcon("foto.jpg").getImage(), 0, 0, 600, 400, null);
	}
	
}


// special panel for app frame panel
class AppPanel extends JPanel{
	
	
	public AppPanel(){
		super();
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(new ImageIcon("p2.jpg").getImage(), 0, 0, 600, 400, null);
	}
	
}

// special rounded text field
class RoundJTextField extends JTextField {
    private Shape shape;
    public RoundJTextField(int size) {
        super(size);
        setOpaque(false); // As suggested by @AVD in comment.
    }
    protected void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
         super.paintComponent(g);
    }
    protected void paintBorder(Graphics g) {
         g.setColor(getForeground());
         g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
    }
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8);
         }
         return shape.contains(x, y);
    }
}