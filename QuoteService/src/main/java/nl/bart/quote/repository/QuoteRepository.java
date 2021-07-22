package nl.bart.quote.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import nl.bart.quote.importer.QuoteImporter;
import nl.bart.quote.importer.SimpleJsonImporter;
import nl.bart.quote.model.Quote;

public class QuoteRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuoteRepository.class);
	
	public static final Integer MAX_LIKES = 100;
	public static final Double PICK_LIKED_QUOTE_CHANCE = 0.4d;
	
	private List<Quote> importedQuotes;
	private List<Quote> backupQuotes;
	private Integer totalWeight;
	private Random rand = new Random();
	
	public QuoteRepository(String importSourceUrl, String importQuoteFieldname, String importAuthorFieldname, String importForbiddenWord) {
		initBackupQuotes();
		LOGGER.info("Started importing quotes.");
		QuoteImporter importer = new SimpleJsonImporter(importSourceUrl, importQuoteFieldname, importAuthorFieldname, importForbiddenWord);
		importedQuotes = importer.importQuotes();
		LOGGER.info("Ended importing " + importedQuotes.size() + " quotes.");
		totalWeight = 0;
	}
	
	public Quote getRandomQuote() {
		Quote quote = null;
		boolean pickLikedQuote = rand.nextDouble() <= PICK_LIKED_QUOTE_CHANCE;
		
		if (pickLikedQuote) {
			quote = pickLikedQuote();
		}
		if (quote == null) {
			quote = pickImportedQuote();
		}
		if (quote == null) {
			quote = pickBackupQuote();
		}
		return quote;
	}
	
	public boolean likeQuoteById(Integer id) {
		boolean quoteFound = false;
		for (Quote quote: importedQuotes) {
			if (quote.getId().equals(id)) {
				likeQuote(quote);
				quoteFound = true;
				break;
			}
		}
		return quoteFound;		
	}
	
	public boolean likeQuoteByQuote(String quoteStr) {
		boolean quoteFound = false;
		for (Quote quote: importedQuotes) {
			if (quote.getQuote().equals(quoteStr)) {
				likeQuote(quote);
				quoteFound = true;
				break;
			}
		}
		return quoteFound;		
	}
	
	private Quote pickLikedQuote() {
		Quote result = null;
		if (totalWeight != 0) {
			Integer accumulatedWeight = 0;
			Integer stopWeight = rand.nextInt(totalWeight);
			for(Quote importedQuote: importedQuotes) {
				if (importedQuote.getWeight() != null && importedQuote.getWeight() > 0) {
					accumulatedWeight = accumulatedWeight + importedQuote.getWeight();
					if (accumulatedWeight>= stopWeight) {
						result = importedQuote;
						break;
					}
				}
			}
		}
		return result;
	}
	
	private Quote pickImportedQuote() {
		Quote quote = null;
		if (importedQuotes != null && importedQuotes.size() > 0) {
			quote = importedQuotes.get(rand.nextInt(importedQuotes.size()));
		}
		return quote;
	}
	
	private Quote pickBackupQuote() {
		return backupQuotes.get(rand.nextInt(backupQuotes.size()));
	}
	
	private void likeQuote(Quote quote) {
		if (quote.getLikes() < MAX_LIKES) {
			Integer oldWeight = quote.getWeight();
			quote.setLikes(quote.getLikes() + 1);
			Integer newWeight = calculateWeight(quote.getLikes());
			quote.setWeight(newWeight);
			totalWeight = totalWeight + (newWeight - oldWeight);
		}
	}
	
	private Integer calculateWeight(Integer likes) {
		Integer weight;
		if (likes == null) {
			weight = 0;
		} else {
			weight = likes * likes;
		}
		return weight;
	}
	
	private void initBackupQuotes() {
		backupQuotes = new ArrayList<>();
		
		Quote quote1 = new Quote();
		quote1.setId(1);
		quote1.setQuote("Beter een quote dan geen quote.");
		quote1.setAuthor("Jan Jansen");
		backupQuotes.add(quote1);
		
		Quote quote2 = new Quote();
		quote2.setId(2);
		quote2.setQuote("Een quote stoot zich geen twee keer aan dezelfde steen.");
		quote2.setAuthor("Janine Jansen");
		backupQuotes.add(quote2);
	}

}
