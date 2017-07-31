package coding.challenge;

import coding.challenge.domain.Statistics;
import coding.challenge.domain.Transaction;
import coding.challenge.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService service;

    @RequestMapping(path = "/transaction", method = POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity add(@RequestBody Transaction transaction) {

        return service.add(transaction) ?
                new ResponseEntity(HttpStatus.CREATED) :
                new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/statistics", method = GET, produces = APPLICATION_JSON_VALUE)
    public Statistics statistics() {
        return service.getStatistics();
    }


}
