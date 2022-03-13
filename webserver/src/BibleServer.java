package bibleServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;
import java.util.StringTokenizer;

// Original code copied and modified from SSaurel's Blog:
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class BibleServer implements Runnable {

	public static void main(String[] args) {
		// have some code in here that ensures that all necessary files are in the right place
		startServer();
	}
	static final int PORT = 8080; // port to listen connection
	static final boolean verbose = true; // verbose mode
	private Socket connect; // Client Connection via Socket Class

	public BibleServer(Socket c) {
		connect = c;
	}

	private static void startServer() {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

			while (true) {
				BibleServer serverJob = new BibleServer(serverConnect.accept());
				if (verbose) System.out.println("Connecton opened. (" + new Date() + ")");
				Thread serverJobThread = new Thread(serverJob);
				serverJobThread.start();
			}
			//serverConnect.close(); -- unreachable
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null;
		PrintWriter headerOut = null;
		BufferedOutputStream dataOut = null;

		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream())); // we read characters from the client via input stream on the socket
			headerOut = new PrintWriter(connect.getOutputStream()); // we get character output stream to client (for headers)
			dataOut = new BufferedOutputStream(connect.getOutputStream()); // get binary output stream to client (for requested data)

			// get first line of the request from the client
			String input = in.readLine();

			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			String fileRequested = parse.nextToken().toLowerCase();

			if (method.equals("GET")) { // we only support GET method
				String response = RequestHandler.handle(fileRequested);
				String header = response.split("\n\n")[0];
				String body = response.split("\n\n")[1];

				if (verbose) {
					System.out.println("----- GET Response Header -----");
					System.out.println(header);
					System.out.println("----- GET Response Body -------");
					System.out.println(new String(body));
					System.out.println("-------------------------------");
				}

				headerOut.println(header);
				headerOut.println(); // blank line between headers and content, very important !
				headerOut.flush();
				dataOut.write(body.getBytes(), 0, body.length());
				dataOut.flush();
			}
			else { // Method not supported
				if (verbose) {
					System.out.println("501 Not Implemented : " + method + " method.");
				}

				String header = String.join( "\n",
					"HTTP/1.1 501 Not Implemented",
					"Server: Server for Bible Text Requests : 1.0",
					"Date: " + new Date(),
					"Content-length: 0"
				);

				headerOut.println(header);
				headerOut.flush();
			}
		} catch (IOException ioe) {
			System.err.println("ioe caught!!");
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				headerOut.close();
				dataOut.close();
				connect.close(); // close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
			if (verbose) {
				System.out.println("Connection closed.");
				System.out.println("===============================");
			}
		}
	}
}
