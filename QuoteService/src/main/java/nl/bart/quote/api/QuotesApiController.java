package nl.bart.quote.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import nl.bart.quote.mapper.QuoteMapper;
import nl.bart.quote.model.Quote;
import nl.bart.quote.model.QuoteDto;
import nl.bart.quote.repository.QuoteRepository;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("${openapi.quoteService.base-path:/quotes}")
public class QuotesApiController implements QuotesApi {
	
	@Value("${nl.bart.quote.importer.simplejsonimporter.source.url:http://quotes.stormconsultancy.co.uk/quotes.json}")
	private String importSourceUrl;
	@Value("${nl.bart.quote.importer.simplejsonimporter.fieldname.quote:quote}")
	private String importQuoteFieldname;
	@Value("${nl.bart.quote.importer.simplejsonimporter.fieldname.author:author}")
	private String importAuthorFieldname;
	@Value("${nl.bart.quote.importer.simplejsonimporter.forbidden.word:C++}")
	private String importForbiddenWord;
	
	private final NativeWebRequest request;
    
    private QuoteRepository quoteRepository;
    private QuoteMapper quoteMapper = new QuoteMapper();

    @PostConstruct
    public void initialize() {
    	quoteRepository = new QuoteRepository(importSourceUrl, importQuoteFieldname, importAuthorFieldname,importForbiddenWord);
    }
    
    @org.springframework.beans.factory.annotation.Autowired
    public QuotesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    @Override
    public ResponseEntity<String> getRandomQuote() {
    	Quote quote = quoteRepository.getRandomQuote();
    	String quoteStr = quote.getQuote();
        return ResponseEntity.ok(quoteStr);        
    }
    
    @Override
    public ResponseEntity<QuoteDto> getRandomQuoteJson() {
    	QuoteDto quote = quoteMapper.mapQuote(quoteRepository.getRandomQuote());
    	return ResponseEntity.ok(quote);
    }
    
    @Override
    public ResponseEntity<QuoteDto> getRandomQuoteXml() {
    	QuoteDto quote = quoteMapper.mapQuote(quoteRepository.getRandomQuote());
    	return ResponseEntity.ok(quote);
    }
    
    @Override
    public ResponseEntity<Void> likeQuoteById(@NotNull @Valid Integer id) {
    	if (quoteRepository.likeQuoteById(id)) {
    		return new ResponseEntity<Void>(HttpStatus.OK);
    	} else {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}
    }
    
    @Override
    public ResponseEntity<Void> likeQuoteByQuote(@NotNull @Valid String quote) {
    	if (quoteRepository.likeQuoteByQuote(quote)) {
    		return new ResponseEntity<Void>(HttpStatus.OK);
    	} else {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}
    }

}
