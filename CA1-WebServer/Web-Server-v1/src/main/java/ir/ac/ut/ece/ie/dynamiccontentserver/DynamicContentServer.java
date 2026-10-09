package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class DynamicContentServer {
	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(9092);
		Socket socket;
		while ((socket = serverSocket.accept()) != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String readLine = reader.readLine();
			if (readLine == null)
				continue;

			String pageName = getPageName(readLine);
			String path = getPath(readLine);
			String body = getBody(reader);

			try {
				Class<?> c = Class.forName("ir.ac.ut.ece.ie.dynamiccontentserver." + pageName);
				Object page = c.getDeclaredConstructor(String.class, String.class).newInstance(path, body);
				Method method = c.getMethod("pageBody");
				byte[] data = (byte[]) method.invoke(page);
				String header = "HTTP1.1 200 OK \r\nContent-Type: text/html\r\nContent.Length: " + data.length + "\r\n\r\n";
				socket.getOutputStream().write(header.getBytes());
				socket.getOutputStream().write(data);
			}
			catch (FileNotFoundException |
					ClassNotFoundException | InstantiationException |
					IllegalAccessException | IllegalArgumentException |
					InvocationTargetException | NoSuchMethodException |
					SecurityException e) {

				String header = "HTTP1.1 404 Page Not Found\r\n\r\n";
				socket.getOutputStream().write(header.getBytes());
				System.out.println(e.getMessage());
			}

			socket.getOutputStream().flush();
			socket.close();
		}
		serverSocket.close();
	}
	
	private String getPath(String readLine) {
		StringTokenizer tokenizer = new StringTokenizer(readLine, " ");
		tokenizer.nextToken();
		String fileName = tokenizer.nextToken().substring(1);
		return fileName;
	}

	private String getPageName(String readLine) {
		StringTokenizer tokenizer = new StringTokenizer(readLine, " ");
		tokenizer.nextToken();
		String fileName = tokenizer.nextToken().substring(1);
		if (fileName.contains("?")) {
			fileName = fileName.substring(0, fileName.indexOf("?"));
		}
		return fileName;
	}

	private String getBody(BufferedReader reader) throws IOException {
		String line;
		int contentLength = 0;
		while (!(line = reader.readLine()).isEmpty()) {
			if (line.startsWith("Content-Length:")) {
				contentLength = Integer.parseInt(line.split(":")[1].trim());
			}
		}

		char[] bodyChars = new char[contentLength];
		if (contentLength > 0) {
			reader.read(bodyChars);
		}

		return new String(bodyChars);
	}
	
	public static void main(String[] args) throws IOException {
		ArticleRepository.loadArticlesFromDisk();
		DynamicContentServer dcs = new DynamicContentServer();
		dcs.start();
	}
}