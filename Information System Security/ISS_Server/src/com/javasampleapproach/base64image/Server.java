package com.javasampleapproach.base64image;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Server {
	
	
	private static HashSet<String> names = new HashSet<String>();  // keeps online users
	private static HashSet<String> posts = new HashSet<String>();	// keeps incoming file's names
	private static PublicKey serverPublicKey;	// server public key
	private static PrivateKey serverPrivateKey;	// server private key
	
	// server offers to user  notifications , user list , and downloadable images
	private static int numberOfClients;		// number of users
	private static String[] clientList;		// user list
	private static int numberOfImages;		// number of images to specific user download
	private static String[] imageList;		// image list
	private static int numberOfNotifications;	// number of notifications at the moment
	private static String[] notificationList;	// notifications
	
	
	// the function encrypts plain text with public key
    public static byte[] encryptWithPubKey(byte[] input, PublicKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    // the function encrypts plain text with private key
    public static byte[] encryptWithPrivKey(byte[] input, PrivateKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    // the function decrypts plain text with public key
    public static byte[] decryptWithPubKey(byte[] input, PublicKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    // the function decrypts plain text with private key
    public static byte[] decryptWithPrivKey(byte[] input, PrivateKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
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
    
    // the function checks username and login
    public static Boolean checkLogin(String username,String password) throws FileNotFoundException {
    	File f = new File("registered users\\" + username + "\\username.txt");
    	Scanner sc = new Scanner(f);
    	
    	String usr = sc.next();
    	String pswd = sc.next();
    	
    	if (usr.equals(username) && pswd.equals(password)) { 	// if password is correct return true
    		return true;
    	}
    	
    	sc.close();
    	return false; // otherwise, return false
    	
    }
    
    // the function that availability of username on the registerition part
    public static Boolean checkRegister(String username) throws FileNotFoundException {
    	File f = new File("registered users\\registers.txt");
    	Scanner sc = new Scanner(f);
    	
    	String usr = new String();
    	while(sc.hasNext()) {
    		usr = sc.next();
    		if (username.equals(usr)) { // if there is same username return false
    			sc.close();
    			return false;
    		}
    	
    	}
    	sc.close();
    	return true; // otherwise, username is appropriate return true
    }
    
    // the function that returns number of registered users
    public static int clients() throws FileNotFoundException {
    	File f = new File("registered users\\registers.txt");
    	Scanner sc = new Scanner(f);
    	int clientsNumber = 0;
    	String usr = new String();
    	while(sc.hasNext()) {
    		usr = sc.next();
    		clientsNumber = clientsNumber + 1;
    	
    	}
    	sc.close();
    	return clientsNumber;
    }
    
    // the function that fills the client list array
    public static void fillClientList() throws FileNotFoundException {
    	File f = new File("registered users\\registers.txt");
    	Scanner sc = new Scanner(f);
    	int clientsNumber = 0;
    	String usr = new String();
    	while(sc.hasNext()) {
    		usr = sc.next();
    		clientList[clientsNumber] = usr;
    		clientsNumber = clientsNumber + 1;
    	
    	}
    	sc.close();
    }
    
    // the function that returns number of downloadable images to specific user
    public static int images(String username) throws FileNotFoundException {
    	File f = new File("registered users\\"+ username + "\\images\\images.txt");
    	Scanner sc = new Scanner(f);
    	int imagesNumber = 0;
    	String usr = new String();
    	while(sc.hasNextLine()) {
    		usr = sc.nextLine();
    		imagesNumber = imagesNumber + 1;
    	
    	}
    	sc.close();
    	return imagesNumber;
    }
    
    // the function fills image list array
    public static void fillImageList(String username) throws FileNotFoundException {
    	File f = new File("registered users\\"+ username + "\\images\\images.txt");
    	Scanner sc = new Scanner(f);
    	int imagesNumber = 0;
    	String usr = new String();
    	while(sc.hasNextLine()) {
    		usr = sc.nextLine();
    		imageList[imagesNumber] = usr;
    		imagesNumber = imagesNumber + 1;
    	
    	}
    	sc.close();
    }
    
    // the function that returns number of notifications at the moment
    public static int notifications(String username) throws FileNotFoundException {
    	File f = new File("registered users\\"+ username + "\\notifications\\notification.txt");
    	Scanner sc = new Scanner(f);
    	int notificationNumber = 0;
    	String noti = new String();
    	while(sc.hasNextLine()) {
    		noti = sc.nextLine();
    		notificationNumber = notificationNumber + 1;
    	
    	}
    	sc.close();
    	return notificationNumber;
    }
    
    // the function that fills notification array list if there is any notification
    public static void fillNotificationList(String username) throws IOException {
    	File f = new File("registered users\\"+ username + "\\notifications\\notification.txt");
    	Scanner sc = new Scanner(f);
    	int notificationNumber = 0;
    	String notification = new String();
    	while(sc.hasNextLine()) {
    		notificationList[notificationNumber] = sc.nextLine();
    		notificationNumber++;
    	}
    	sc.close();
    	
    	FileWriter fw = new FileWriter(new File("registered users\\"+ username + "\\notifications\\notification.txt"));
    	fw.write("");
    	fw.close();
    }
    
    // checks availability of image name for uploading
    public static Boolean checkAvailable(String imageName) throws FileNotFoundException {
    	File f = new File("image\\image.txt");
    	Scanner sc = new Scanner(f);
    	
    	String img = new String();
    	while(sc.hasNextLine()) {
    		img = sc.nextLine();
    		// if there is a image with same name return false
    		if (imageName.equals(img) || imageName.equals("images") || imageName.equals("image") || 
    				imageName.equals("Images") || imageName.equals("IMAGES")
    				|| imageName.equals("Image") || imageName.equals("IMAGE") || posts.contains(imageName)) {
    			sc.close();
    			return false;
    		}
    	
    	}
    	sc.close();
    	return true; // otherwise, image name is appropriate return true
    }
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        
    	// take server's public key ( string mode ) from file 
    	File f = new File("keys\\publicKey.txt");
        Scanner s = new Scanner(f);
        
        String pubK = s.next();
       
        s.close();
        
        
        // take server's private key ( string mode ) from file 
        f = new File("keys\\privateKey.txt");
        s = new Scanner(f);
        String privK = s.next();
        s.close();
        
    	// convert public key string to PublicKey 
        PublicKey serverPublicKey = loadPublicKey(pubK); 
        // conver private key string to PublicKey
        PrivateKey serverPrivateKey = loadPrivateKey(privK);
        
        // server socket with port 10750   
    	ServerSocket listener = new ServerSocket(10750);
    	System.out.println("Server is ready");	// if server is ready print it
    	
    	
    	try {
    		// take requests from users
            while (true) {
                new Handler(listener.accept(),serverPublicKey,serverPrivateKey).start();
            }
        } finally {
            listener.close();
        }
    }
    
    // new thread for every users who want to connect
    private static class Handler extends Thread {
    	private Socket socket; // socket 
    	private PublicKey serverPublicKey;  // server Public Key
    	private PrivateKey serverPrivateKey; // server Private Key
    	private ObjectOutputStream oos;  // write object to socket
    	private ObjectInputStream ois; 	// read object from socket
    	boolean success; 
    	private String username;		// client's username
    	
    	
    	
    	public Handler(Socket socket,PublicKey serverPublicKey,PrivateKey serverPrivateKey) {
            this.socket = socket;
            this.serverPublicKey = serverPublicKey;
            this.serverPrivateKey = serverPrivateKey;
        }
    	
    	public void run() {
    		try {
    			ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                
                
                // log writer writes everything to log.txt 
                FileWriter logfw;
                BufferedWriter logbw;
                PrintWriter logWriter;
                
                // event is for what user want to do 
                String event = new String();
                while (true) {
                	
                	// read client wish and process the protocol below
	                event = (String) ois.readObject();
	                
	                // if client want to register process the register protocol
	                if (event.equals("register")) {
	                	
	                	
	                	// take username and password from client
	                	username = (String) ois.readObject();
	                	String password = (String) ois.readObject();
	                	
	                	
	                	success = checkRegister(username); // check availability of username 
	                	oos.writeObject(success);		//  send circumstance to client 
	                	
	                	// success true means client succesfully registered 
	                	if ( success == true) {
	                		FileWriter fw = new FileWriter("registered users\\registers.txt", true);
		                    BufferedWriter bw = new BufferedWriter(fw);
		                    PrintWriter out = new PrintWriter(bw);
		                    
		                    // write the username to registers.txt file 
		                    out.println(username);
		                	
		                	out.close();
		                	bw.close();
		                	fw.close();
		                	
		                	// take his public key from user 
		                	PublicKey publicKey = (PublicKey) ois.readObject();
	                		
		                	// certificate public key with his username
		                	String certificate = username + "," + savePublicKey(publicKey);
		                	byte[] certification = encryptWithPrivKey(certificate.getBytes("UTF-8"), serverPrivateKey);
		                	certificate = Base64.getEncoder().encodeToString(certification);
		                	
		                	oos.writeObject(certificate); // send certificate to user
		                	
		                	// some log record 
		                	logfw = new FileWriter("log\\log.txt", true);
		                	logbw = new BufferedWriter(logfw);
		                	logWriter = new PrintWriter(logbw);
		                	logWriter.println(username + " has been registered succesfully with password " + password + ".");
		                	logWriter.println(username + " sent his public key to server.");
		                	logWriter.println("Server has sent " + username + "'s certificate to user. " + certificate );
		                	logWriter.close();
		                	logbw.close();
		                	logfw.close();
		                	
		                	
		                	// create a folder named with 'username'
		                	String dir = System.getProperty("user.home") + "\\eclipse-workspace\\ISS_Server\\registered users\\" + username + "\\";
	    					File newDir = new File(dir);
	    					
	    					if ( !newDir.exists()) {
	    						newDir.mkdir();
	    					}
	    					
	    					// create images folder under the 'username' folder
	    					dir = System.getProperty("user.home") + "\\eclipse-workspace\\ISS_Server\\registered users\\" + username + "\\images\\";
	    					newDir = new File(dir);
	    					
	    					if ( !newDir.exists()) {
	    						newDir.mkdir();
	    					}
	    					
	    					// create notification folder under the 'username' folder
	    					dir = System.getProperty("user.home") + "\\eclipse-workspace\\ISS_Server\\registered users\\" + username + "\\notifications\\";
	    					newDir = new File(dir);
	    					
	    					if ( !newDir.exists()) {
	    						newDir.mkdir();
	    					}
	    					
	    					/* create txt files
	    					 * username.txt ( keeps username and password )
	    					 * certificate.txt ( keeps certificate )
	    					 * images.txt under images folder ( keeps name of images sent to user )
	    					 * notifications.txt under notification folder ( keeps notifications )
	    					 */
	    					out = new PrintWriter(new File("registered users\\" + username + "\\username.txt"));
	    					out.write(username + " " + password);
	    					out.close();
	    					
	    					out = new PrintWriter(new File("registered users\\" + username + "\\certificate.txt"));
	    					out.write(certificate);
	    					out.close();
	    					
	    					out = new PrintWriter(new File("registered users\\" + username + "\\images\\images.txt"));
	    					out.write("");
	    					out.close();
	    					
	    					out = new PrintWriter(new File("registered users\\" + username + "\\notifications\\notification.txt"));
	    					out.write("");
	    					out.close();
	    					
	    					username = null;
	                	}
	                	else {
	                		
	                		// if register is not succesful keep record on log file
	                		logfw = new FileWriter("log\\log.txt", true);
		                	logbw = new BufferedWriter(logfw);
		                	logWriter = new PrintWriter(logbw);
	                		logWriter.println(username + " register request has been failed.");
	                		logWriter.close();
		                	logbw.close();
		                	logfw.close();
		                	
		                	username = null;
	                	}
	                }
	                
	                // if user wants to login process the protocol below
	                else if (event.equals("login")) {
	                	
	                	// take username and password from user
	                	username = (String) ois.readObject();
	                	String password = (String) ois.readObject();
	                	
	                	// control password
	                	success = checkLogin(username,password);
	                	
	                	// if password is correct send to user password is correct
	                	oos.writeObject(success);
	                	
	                	// if password is correct check if he is online ?
	                	if( success == true ) {
	                		
	                		// check online list  ( false means user is not online and login is succesful )
	                		success = names.contains(username);
	                		// if login is succesful send to user
	                		oos.writeObject(success);
	                		
	                		// if user is not online then login is succesfull
	                		if ( success == false ) {
	                			// add the username to online list 
		                		names.add(username);
		                		
		                		// log records again :)
		                		logfw = new FileWriter("log\\log.txt", true);
			                	logbw = new BufferedWriter(logfw);
			                	logWriter = new PrintWriter(logbw);
		                		logWriter.println(username + " has logged in succesfully with the password " + password +"."); 
		                		logWriter.close();
			                	logbw.close();
			                	logfw.close();
			                	
			                	// while user want to log out or exit keep user in the server
			                	while( !event.equals("Log_out")) {
			                		
			                		event = (String) ois.readObject();
			                		
			                		/* notification protocol works as follow
			                		 * server do not notify notifications to user 
			                		 * user demands any notificaion from server every 5 seconds 
			                		 * ( there is a timer on the client side )
			                		 * if there is any notification server send it to user 
			                		 */
			                		if( event.equals("Notification")) {
			                			// get number of notification
			                			numberOfNotifications = notifications(username);
			                			
			                			// fill notification list array
			                			notificationList = new String[numberOfNotifications];
			                			fillNotificationList(username);
			                			
			                			// again log ( if there is any notification )
			                			if ( numberOfNotifications > 0) {
			                				logfw = new FileWriter("log\\log.txt", true);
			    		                	logbw = new BufferedWriter(logfw);
			    		                	logWriter = new PrintWriter(logbw);
			                				logWriter.println("Server has sent notifications to " + username + ".");
			                				logWriter.close();
			    		                	logbw.close();
			    		                	logfw.close();
			                			}
			                			
			                			// send number of notifications and notification list
			                			oos.writeObject(numberOfNotifications);
			                			oos.writeObject(notificationList);
			                		}
			                		
			                		// send user to user list 
			                		else if ( event.equals("Get_List")) {
			                			// number of registered users 
			                			numberOfClients = clients();
			                			
			                			// fill user list array
			                			clientList = new String[numberOfClients];
			                			fillClientList();
			                			
			                			// sent them to user
			                			oos.writeObject(numberOfClients);
			                			oos.writeObject(clientList);
			                			
			                			// again log 
			                			logfw = new FileWriter("log\\log.txt", true);
					                	logbw = new BufferedWriter(logfw);
					                	logWriter = new PrintWriter(logbw);
			                			logWriter.println(username + " has requested users list.");
			                			logWriter.println("Server has sent users list to " + username + ".");
			                			logWriter.close();
					                	logbw.close();
					                	logfw.close();
			                			
					                	
					                	// check image name availability
					                	String imageName = new String();
			                			Boolean available = false;
			                			imageName = (String) ois.readObject();
			                			if ( imageName.length() != 0 ) {
			            					available = checkAvailable(imageName);
			            				}
			                			oos.writeObject(available);
			                			
			                			// if post name is appropriate add it to posts 
			                			if ( available == true ) {
			                				posts.add(imageName);
			                			}
			                				
			                			
			                			
			                		}
			                		// post protocol 
			                		else if (event.equals("Post_Image")) {
			                			
			                			
			                			String uploadName = new String();
			                			
			                			String imageName = new String();
			                			String cipherImage = new String();
			                			String digitalSignature = new String();
			                			String encryptedAesKey = new String();
			                			String IV = new String();
			                			String certificate = new String();
			                			
			                			
			                			// take image name, encrypted image, and digital signature
			                			imageName = (String) ois.readObject();
			                			cipherImage = (String) ois.readObject();
			                			digitalSignature = (String) ois.readObject();
			                			
			                			
			                			FileWriter fw = new FileWriter("image\\image.txt", true);
			                			BufferedWriter bw = new BufferedWriter(fw);
			                			PrintWriter out = new PrintWriter(bw);
					                    
			                			// write image name to image.txt under the image folder
					                    out.println(imageName);
					                	
					                	out.close();
					                	bw.close();
					                	fw.close();
			                			
			                			// log
			                			logfw = new FileWriter("log\\log.txt", true);
					                	logbw = new BufferedWriter(logfw);
					                	logWriter = new PrintWriter(logbw);
			                			logWriter.println(username + " has sent post image request to server.");
			                			logWriter.println(username + " has sent image name to server. " + imageName);
			                			logWriter.println(username + " has sent encrypted image to server.");
			                			logWriter.println(username + " has sent digital signature to server. " + digitalSignature);
			                			logWriter.close();
					                	logbw.close();
					                	logfw.close();
			                			
			                			
					                	
					                	
					                	/*create a txt file named as given image name
					                	 * this txt file keeps 
					                	 * owner name 
					                	 * encrypted image 
					                	 * digital signature 
					                	 * certificate
					                	 */
					                	fw = new FileWriter("image\\" + imageName + ".txt", true);
					                	bw = new BufferedWriter(fw);
					                	out = new PrintWriter(bw);
					                    
					                    out.println("ownerName: " + username);			// store owner name
					                    out.println("cipherImage: " + cipherImage);			// store encrypted image
					                    out.println("digitalSignature: " + digitalSignature);	// store digital signature
					                    							// store IV
					                    
					                    Scanner s = new Scanner(new File("registered users\\" + username + "\\certificate.txt"));
					                    out.println("Certificate: " + s.next());			// store certificate
					                	
					                	out.close();
					                	bw.close();
					                	fw.close();
					                	
					                	uploadName = (String) ois.readObject();
					                	
					                	// user selects to send which users 
					                	// for every selected user
				                		while(!(uploadName.equals("Completed"))) {
				                			logfw = new FileWriter("log\\log.txt", true);
						                	logbw = new BufferedWriter(logfw);
						                	logWriter = new PrintWriter(logbw);
				                			logWriter.println(username + " has requested " + uploadName + "'s certificate.");
				                			
				                			// user wants selected user's certificate 
				                			s = new Scanner(new File("registered users\\" + uploadName + "\\certificate.txt"));
				                			certificate = s.next();
				                			oos.writeObject(certificate);
				                			s.close();
				                			
				                			
				                			logWriter.println("Server has sent " + uploadName + "'s certificate to " + username + ". " + certificate );
				                			
				                			// server gets encrypted aes key and encrypted IV 
				                			encryptedAesKey = (String) ois.readObject();
				                			IV = (String) ois.readObject();
				                			
				                			// log
				                			logWriter.println(username + " has sent encrypted Aes key with " + uploadName + " public key to server. " + encryptedAesKey);
				                			logWriter.println(username + " has sent encrypted IV with " + uploadName + " public key to server. " + IV);
				                			logWriter.close();
						                	logbw.close();
						                	logfw.close();
				                			
				                			fw = new FileWriter("registered users\\" + uploadName + "\\images\\images.txt", true);
						                    bw = new BufferedWriter(fw);
						                    out = new PrintWriter(bw);
						                    // keep image file on the common side
						                    out.println(imageName);
						                	
						                	out.close();
						                	bw.close();
						                	fw.close();
						                	
						                	fw = new FileWriter("registered users\\" + uploadName + "\\notifications\\notification.txt"  , true);
						                    bw = new BufferedWriter(fw);
						                    out = new PrintWriter(bw);
						                    
						                    // write notification to selected user's notification.txt file
						                    out.println("New_Post: " + username + " -> " + imageName);
						                	
						                	out.close();
						                	bw.close();
						                	fw.close();
						                	
						                	
						                
						                					                	
						                	
						                	fw = new FileWriter("registered users\\" + uploadName + "\\images\\" + imageName + ".txt", true);
						                    bw = new BufferedWriter(fw);
						                    out = new PrintWriter(bw);
						                   
						                    // create a file under selected user's image folder  with 'image name'
						                    // write to imagename   encrypted aes key and encrypted IV
						                    out.println("encryptedAesKey: " + encryptedAesKey);		//	store aes key
						                    out.println("IV: " + IV);							// store IV
						                    
						                	out.close();
						                	bw.close();
						                	fw.close();
						                	
						                	// take next selected user name from user
				                			uploadName = (String) ois.readObject();
				                		}
				                		
				                		
			                		}
			                		else if ( event.equals("Cancel_Post")) {
			                			String imageName = (String) ois.readObject();
			                			posts.remove(imageName);
			                		}
			                		// user want to download image and send his image list
			                		else if( event.equals("Get_Images")) {
			                			numberOfImages = images(username);
			                			imageList = new String[numberOfImages];
			                			fillImageList(username);
			                			
			                			// log
			                			logfw = new FileWriter("log\\log.txt", true);
					                	logbw = new BufferedWriter(logfw);
					                	logWriter = new PrintWriter(logbw);
			                			logWriter.println("Server has sent downloadable image list to " + username + ".");
			                			logWriter.close();
					                	logbw.close();
					                	logfw.close();
			                			
					                	oos.writeObject(numberOfImages); // send number of available image
			                			oos.writeObject(imageList);		// send available image list
			                		}
			                		// to download image
			                		else if ( event.equals("Download_Image")) {
			                			// take image name
			                			String imageName = (String) ois.readObject();
			                			
			                			// open image's txt file on the common side
			                			File f = new File("image\\" + imageName + ".txt");
			                	    	Scanner sc = new Scanner(f);
			                	    	
			                	    	String ownerName = new String();
			                	    	String cipherImage = new String();
			                	    	String digitalSignature = new String();
			                	    	String encryptedAesKey = new String();
			                	    	String IV = new String();
			                	    	String ownerCertificate = new String();
			                	    	
			                	    	// take owner name from txt file
			                	    	ownerName = sc.next();
			                	    	ownerName = sc.next();
			                	    	
			                	    	// take encrypted image from txt file
			                	    	cipherImage = sc.next();
			                	    	cipherImage = sc.next();
			                	    	
			                	    	// take digital signature from txt file
			                	    	digitalSignature = sc.next();
			                	    	digitalSignature = sc.next();
			                	    	
			                	    	// take certificate from file
			                	    	ownerCertificate = sc.next();
			                	    	ownerCertificate = sc.next();
			                	    	
			                	    	sc.close();
			                	    	// open image txt file under user's folder to take Encrypted aes key and encrypted IV
			                	    	f = new File("registered users\\" + username + "\\images\\" + imageName + ".txt");
			                	    	sc = new Scanner(f);
			                	    	
			                	    	// take encrypted aes key from txt file
			                	    	encryptedAesKey = sc.next();
			                	    	encryptedAesKey = sc.next();
			                	    	
			                	    	// take encrypted IV from file
			                	    	IV = sc.next();
			                	    	IV = sc.next();
			                	    	
			                	    	sc.close();
			                	    	
			                	    	
			                	    	// log
			                	    	logfw = new FileWriter("log\\log.txt", true);
					                	logbw = new BufferedWriter(logfw);
					                	logWriter = new PrintWriter(logbw);
			                	    	logWriter.println(username + " has sent Download image request to server.");
			                	    	logWriter.println("Server has sent owner name of image " 
			                	    	+ imageName + " to " + username + ". " + ownerName);
			                	    	logWriter.println("Server has sent encrypted image file to " + username);
			                	    	logWriter.println("Server has sent digital signature to " + username + ". " + digitalSignature );
			                	    	logWriter.println("Server has sent encrypted aes key to " + username + ". " + encryptedAesKey);
			                	    	logWriter.println("Server has sent encrypted IV to " + username + ". " + IV);
			                	    	logWriter.println("Server has sent owner certificate to " + username + ". " + ownerCertificate);
			                	    	logWriter.close();
					                	logbw.close();
					                	logfw.close();
			                	    	
					                	
					                	/* send to downloader
			                	    	 * owner name
			                	    	 * encrypted image
			                	    	 * digital signature
			                	    	 * encrypted aes key ( with downloader public key )
			                	    	 * encrypted IV ( with downloader public key )
			                	    	 * owner certificate
			                	    	 */
			                	    	oos.writeObject(ownerName);
			                	    	oos.writeObject(cipherImage);
			                	    	oos.writeObject(digitalSignature);
			                	    	oos.writeObject(encryptedAesKey);
			                	    	oos.writeObject(IV);
			                	    	oos.writeObject(ownerCertificate);
			                	    	
			                		}
			                	}
			                	// if user logs out remove him from online list
			                	names.remove(username);
			                	
	                		}
	                		else {  
		                		// if login is fails  record log
		                		logfw = new FileWriter("log\\log.txt", true);
			                	logbw = new BufferedWriter(logfw);
			                	logWriter = new PrintWriter(logbw);
			                	logWriter.print(username + " has failed to login.");
			                	logWriter.close();
			                	logbw.close();
			                	logfw.close();
			                	
			                	username = null;
			                	
		                	}
	                	}
	                	else {  
	                		// if login is fails  record log
	                		logfw = new FileWriter("log\\log.txt", true);
		                	logbw = new BufferedWriter(logfw);
		                	logWriter = new PrintWriter(logbw);
		                	logWriter.print(username + " has failed to login.");
		                	logWriter.close();
		                	logbw.close();
		                	logfw.close();
		                	
		                	username = null;
		                	
	                	}
	                	
	                }
	                else if (event.equals("exit")) {
	                	// user wants to exit   log 
	                	logfw = new FileWriter("log\\log.txt", true);
	                	logbw = new BufferedWriter(logfw);
	                	logWriter = new PrintWriter(logbw);
	                	logWriter.println(username + " has logged out succesfully.");
	                	logWriter.close();
	                	logbw.close();
	                	logfw.close();
	                	return;
	                }
	               
                }
            	
    		}
    		catch (IOException e) {
    			System.out.println(e);
    		} 
    		catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		finally {
	            try {
	            	// user disconnected and remove the user from online list
	            	if ( username != null) {
	            	names.remove(username);  
	            	
	            	}
	                socket.close(); // close socket
	            } 
	            catch (IOException e) {
	            }
    		}
    	}
    }
}
