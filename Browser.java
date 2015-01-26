package prog12;

import java.util.List;

public interface Browser {
	/**
	 * Looks up the page on the internet and if it is successful, it will get
	 * the words on the page as well as the URL
	 * @param url
	 * @return
	 */
    boolean loadPage (String url);
    /**
     * If loading was successful, then it will get the words on the web page.
     * @return
     */
    List<String> getWords ();
    /**
     * Once again, if loading was successful, it will get the applicable URLs.
     * @return
     */
    List<String> getURLs ();
}

