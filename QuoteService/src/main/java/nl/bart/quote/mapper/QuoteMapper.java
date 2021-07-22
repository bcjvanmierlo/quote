package nl.bart.quote.mapper;

import nl.bart.quote.model.Quote;
import nl.bart.quote.model.QuoteDto;

public class QuoteMapper {
	
	public QuoteDto mapQuote(Quote quote) {
		QuoteDto quoteDto = null;
		if (quote != null) {
			quoteDto = new QuoteDto();
			quoteDto.setId(quote.getId());
			quoteDto.setQuote(quote.getQuote());
			quoteDto.setAuthor(quote.getAuthor());
		}
		return quoteDto;		
	}

}
