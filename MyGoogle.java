package prog12;

import java.util.*;

public class MyGoogle implements Google {
	
	private boolean print = true;
	
	/**
	 * Every time we encounter a new page, we add it to the master list of
	 * web pages
	 */
	private List<String> pagesSeen = new ArrayList<String>();
	
	/**
	 * This will give us the index of the page when we want to find it
	 */
	private Map<String, Integer> pageToIndex = new TreeMap<String, Integer>();
	
	/**
	 * This will keep track of the "significance" (how many references on other web pages)
	 * of the page
	 */
	private List<Integer> refCounts = new ArrayList<Integer>();
	
	
	public void indexPage(String url){
		if (print) {
		      System.out.println("\nIndexing URL " + url);
		      System.out.println("BEFORE");
		      System.out.println("pagesSeen " + pagesSeen);
		      System.out.println("pageToIndex " + pageToIndex);
		      System.out.println("refCounts " + refCounts);
		    }
		    pagesSeen.add(url);
		    pageToIndex.put(url, pagesSeen.size()-1);
		    refCounts.add(0);
		    if (print) {
		      System.out.println("AFTER");
		      System.out.println("pagesSeen " + pagesSeen);
		      System.out.println("pageToIndex " + pageToIndex);
		      System.out.println("refCounts " + refCounts);
		    }
	}
	
	
	/**
	 * This will keep track of all the words seen on a page.
	 */
	private List<String> wordsSeen = new ArrayList<String>();
	
	/**
	 * After we get the words seen, we index them to find them quickly
	 */
	private Map<String, Integer> wordToIndex = new HashMap<String, Integer>();
	
	/**
	 * List of web page indices which contain the word
	 */
	private List< List<Integer> > pageIndexLists = new ArrayList< List<Integer>>();
	
	public void indexWord(String word){
		if (print) {
		      System.out.println("\nIndexing word " + word);
		      System.out.println("BEFORE");
		      System.out.println("wordsSeen " + wordsSeen);
		      System.out.println("wordToIndex " + wordToIndex);
		      System.out.println("pageIndexLists " + pageIndexLists);
		    }
		    wordsSeen.add(word);
		    wordToIndex.put(word, wordsSeen.size()-1);
		    pageIndexLists.add(new ArrayList<Integer>());
		    if (print) {
		      System.out.println("AFTER");
		      System.out.println("wordsSeen " + wordsSeen);
		      System.out.println("wordToIndex " + wordToIndex);
		      System.out.println("pageIndexLists " + pageIndexLists);
		    }
	}
	
	public void gather (Browser browser, List<String> startingURLs){
		Queue<String> urlQueue = new ArrayDeque<String>();
	    
	    // Put unknown pages into the queue, but don't look at them yet.
	    for (String url : startingURLs)
	      if (!pageToIndex.containsKey(url)) {
	        indexPage(url);
	        urlQueue.offer(url);
	      }
	    
	    // Set of page indices of these urls, WITHOUT duplications.
	    Set<Integer> pageIndicesOnPage = new HashSet<Integer>();
	    
	    // Look at each page in the queue.
	    int count = 0;
	    while (!urlQueue.isEmpty() && count++ < 100) {
	      if (print)
	        System.out.println("\nqueue " + urlQueue);
	      String url = urlQueue.poll();

	      // Clear set of indices of URLs on this page.
	      pageIndicesOnPage.clear();

	      // The index of the current page.
	      int pageIndex = pageToIndex.get(url);
	      if (print)
	        System.out.println("dequeued " + url + "=" + pageIndex);
	      
	      if (browser.loadPage(url)) {
	        // List of urls on this page, WITH duplications.
	        List<String> urlsOnPage = browser.getURLs();
	        if (print)
	          System.out.println("\nURLs on page " + urlsOnPage);
	        
	        for (String urlOnPage : urlsOnPage) {
	          // Index links to unknown pages and put them into the queue.
	          if (!pageToIndex.containsKey(urlOnPage)) {
	            indexPage(urlOnPage);
	            urlQueue.offer(urlOnPage);
	            if (print)
	              System.out.println("\nenqueuing " + urlOnPage);
	          }
	          
	          // Get the index of a url linked from the current page.
	          int urlIndexOnPage = pageToIndex.get(urlOnPage);
	          
	          // If this is the first appearance, increment its refCounts.
		  if (!pageIndicesOnPage.contains(urlIndexOnPage)) {
	            pageIndicesOnPage.add(urlIndexOnPage);
	            if (print) {
	              System.out.println("\nincrementing ref count for " +
	                                 urlOnPage + "=" + urlIndexOnPage);
	              System.out.println("BEFORE");
	              System.out.println("refCounts " + refCounts);
	            }
	            refCounts.set(urlIndexOnPage, refCounts.get(urlIndexOnPage) + 1);
	            if (print) {
	              System.out.println("AFTER");
	              System.out.println("refCounts " + refCounts);
	            }
	          }
	          else if (print)
	            System.out.println("\n" + urlOnPage + " appeared earlier on page");
	        }
	        
	        // The words on this page.
	        List<String> words = browser.getWords();
	        if (print)
	          System.out.println("\nwords on page " + words);
	        for (String word : words) {
	          // Record new words.
	          if (!wordToIndex.containsKey(word))
	            indexWord(word);
	          
	          // Add the page index of this page to the end of the
	          // list of page indices for this word, but do it only
	          // once even if this word appears multiple times
	          // on this page.
	          int wordIndex = wordToIndex.get(word);
	          List<Integer> list = pageIndexLists.get(wordIndex);
	          if (list.isEmpty() || list.get(list.size()-1) != pageIndex) {
	            if (print) {
	              System.out.println("\nadding current page " +
	                                 url + "=" + pageIndex +
	                                 " to list of " +
	                                 word + "=" + wordIndex);
	              System.out.println("BEFORE");
	              System.out.println("pageIndexLists " + pageIndexLists);
	            }
	            list.add(pageIndex);
	            if (print) {
	              System.out.println("AFTER");
	              System.out.println("pageIndexLists " + pageIndexLists);
	            }
	            
	          }
	          else if (print)
	            System.out.println("\n" + word + "=" + wordIndex +
	                               " has already appeared on this page");
	        }
	      }
	    }

	    if (true) {
	      System.out.println("pagesSeen:");
	      System.out.println(pagesSeen);
	      System.out.println("pageToIndex:");
	      System.out.println(pageToIndex);
	      System.out.println("refCounts:");
	      System.out.println(refCounts);
	      System.out.println("wordsSeen:");
	      System.out.println(wordsSeen);
	      System.out.println("wordToIndex:");
	      System.out.println(wordToIndex);
	      System.out.println("pageIndexLists:");
	      System.out.println(pageIndexLists);
	    }
		
	}
	public String[] search (List<String> keyWords, int numResults){
		
		// Iterator into list of page ids for each key word.
	    Iterator<Integer>[] pageIndexIterators = 
	    	(Iterator<Integer>[]) new Iterator[keyWords.size()];
	    
	    // Current page index in each list, just ``behind'' the iterator.
	    int[] currentPageIndices = new int [keyWords.size()];
	    
	    // LEAST popular page is at top of heap so if heap has numResults
	    // elements and the next match is better than the least popular page
	    // in the queue, the least popular page can be thrown away.
	    PriorityQueue<Integer> bestPageIndices = 
	    	new PriorityQueue<Integer>(numResults, new PageComparator());
	    
	    for (int i = 0; i < keyWords.size(); i++){
	    	pageIndexIterators[i]= pageIndexLists.get(wordToIndex.get(keyWords.get(i))).iterator();
	    	currentPageIndices[i]= pageIndexLists.get(wordToIndex.get(keyWords.get(i))).get(0);
	    }
	    Stack<String> temp = new Stack<String>();
	    while (moveForward(currentPageIndices, pageIndexIterators)) {
	    	if (allEqual(currentPageIndices)){
	    		bestPageIndices.offer(currentPageIndices[0]);
	    	}
	    }
	    while(!bestPageIndices.isEmpty())
	    temp.push(pagesSeen.get(bestPageIndices.poll()));
	    String[] orderedList = new String[temp.size()];
	    int i = 0;
	    while (!temp.isEmpty()) {
	    	orderedList [i] = temp.pop();
	    	i++;
	    }
		return orderedList;
	}
	
	
	/** If all the currentPageIndices are the same (because are just
    starting or just found a match), move them all forward: call
    next() for each page index iterator and put the result into
    current page indices.

    If they are not all the same, don't move the largest one(s)
    forward.  (There may be more than one equal to the largest index
    in current page indices.)

    Return false if hasNext() is false for any iterator.

    @param currentPageIndices array of current page indices
    @param pageIndexIterators array of iterators with next page indices
    @return true if all minimum page indices updates, false otherwise
	 */
	private boolean moveForward (int[] currentPageIndices, Iterator<Integer>[] pageIndexIterators){
		
		int maximum = Integer.MIN_VALUE;
		boolean allSame = allEqual(currentPageIndices);
		
		for (int i = 0; i < currentPageIndices.length; i++){
			if (currentPageIndices[i] > maximum){
				maximum= currentPageIndices[i];
			}
		}
		
		for (int i = 0; i < currentPageIndices.length; i++) {
			if (currentPageIndices[i] != maximum) {
				allSame = false;
			    break;
			}
		}
		
		for (int j = 0; j < currentPageIndices.length; j++){
			if (allSame || currentPageIndices[j] != maximum){
				if (!pageIndexIterators[j].hasNext()){
					return false;
				}
				currentPageIndices[j]= pageIndexIterators[j].next();
			}
		}
		return true;
	}
	
	/** Check if all elements in an array are equal.
    @param array an array of numbers
    @return true if all are equal, false otherwise
	*/
	private boolean allEqual (int[] array) {
		
		boolean check = true;
		for (int i = 0; i < array.length-1; i++){
			if (array[i] != array[i+1]){
				check = false;
			}
		}
		return check;
	}
	
	public class PageComparator implements Comparator<Integer> {
		
		public int compare (Integer page1, Integer page2) {
			int ref1 = refCounts.get(page1);
			int ref2 = refCounts.get(page2);
			
			return ref1 - ref2;
		}
	}

}

