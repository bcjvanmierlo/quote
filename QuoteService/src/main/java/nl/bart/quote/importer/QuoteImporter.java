package nl.bart.quote.importer;

import java.util.ArrayList;
import java.util.List;

import nl.bart.quote.model.Quote;

public abstract class QuoteImporter {
	
	List<Quote> quotes = new ArrayList<>();
	Integer lastId = 1;
	
	public List<Quote> importQuotes() {
		quotes.clear();
		lastId = 1;
		doImport();
		return quotes;
	}
	
	protected void addQuote(String quoteStr, String author, String source) {
		lastId = lastId + 1;
		Quote quote = new Quote();
		quote.setId(lastId);
		quote.setQuote(quoteStr);
		quote.setAuthor(author);
		quote.setSource(source);
		quote.setLikes(0);
		quote.setWeight(0);
		quotes.add(quote);
	}
	
	protected abstract void doImport();

}
