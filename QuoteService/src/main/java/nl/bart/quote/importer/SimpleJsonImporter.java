package nl.bart.quote.importer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleJsonImporter extends QuoteImporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJsonImporter.class);
	
	private String sourceUrlStr;
	private String quoteFieldName;
	private String authorFieldName;
	private String forbiddenWord;
	private boolean censorshipActivated;

	public SimpleJsonImporter(String sourceUrlStr, String quoteFieldName, String authorFieldName, String forbiddenWord) {
		super();
		this.sourceUrlStr = sourceUrlStr;
		this.quoteFieldName = quoteFieldName;
		this.authorFieldName = authorFieldName;
		this.forbiddenWord = forbiddenWord;
		this.censorshipActivated = forbiddenWord != null && !forbiddenWord.isEmpty();
	}

	@Override
	protected void doImport() {
		try {
			URL url = new URL(sourceUrlStr);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode[] nodes = mapper.readValue(url, JsonNode[].class);
			for (JsonNode node: nodes) {
				String quote = node.get(quoteFieldName).asText();
				String author = node.get(authorFieldName).asText();
				if (isAllowed(quote)) {
					addQuote(quote, author, sourceUrlStr);
				} else {
					LOGGER.info("Quote censored during import: " + quote);
				}
			}
	    } catch (MalformedURLException e1) {
	    	LOGGER.error("The given source url string is malformed.");
		} catch (JsonParseException e) {
			LOGGER.error("Parse error while import quotes.");
		} catch (JsonMappingException e) {
			LOGGER.error("Mapping error while import quotes.");
		} catch (IOException e) {
			LOGGER.error("Error while reading the quote source.");
		}
	}
	
	private boolean isAllowed(String quote) {
		return !(censorshipActivated && quote.contains(forbiddenWord));
	}

}
