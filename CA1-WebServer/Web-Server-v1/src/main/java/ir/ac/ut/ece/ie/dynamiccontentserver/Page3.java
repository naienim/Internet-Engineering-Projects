package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.Date;

public class Page3 {

	private String path;
	private String body;

	public Page3(String path, String body) {
		this.path = path;
		this.body = body;
	}

	public byte[] pageBody() {
		return ("<html>"
			 + "<header>"
			 + "<title>"
			 + "Time Page"
			 + "</title>"
			 + "</header>"
			 + "<body>"
			 + "The current time is: "
			 + (new Date()).toString()
			 + "</body>").getBytes();
	}
}
