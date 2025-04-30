package com.example.cashcard;



import com.example.cashcard.cashcard.CashCard;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import javax.swing.event.DocumentEvent;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //  allow to clean state as if tests haven't been runned, this is class level
class CashCardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }


    @Test
    //@DirtiesContext  // also you can use it at test level
    void shouldCreateANewCashCard() {
        CashCard cashCard = new CashCard( 123.45, "sarah1");
        ResponseEntity<Void> response =  restTemplate.withBasicAuth("sarah1","123").postForEntity("/cashcards", cashCard, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = response.getHeaders().getLocation();
        ResponseEntity<String> getReponse = restTemplate.withBasicAuth("sarah1","123").getForEntity(locationOfNewCashCard, String.class);
        assertThat(getReponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getReponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(123.45);



    }

    @Test //i have done it
    void shouldReturnAllCashCardsWhenListIsRequested(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int cashCardCount = documentContext.read("$.length()");

        assertThat(cashCardCount).isEqualTo(7);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101,102,103,104,105);
        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00,1425.00,150.00,134.00,50.00);

        //I've done this instead:   but know its working :)  using different library

//        CashCard firstCashCard = documentContext.read("$[6]", CashCard.class);
//        assertThat(firstCashCard.getId()).isNotNull();
//        assertThat(firstCashCard.getAmount()).isEqualTo(50.00);
    }

    @Test
    void shouldReturnAPageOfCashCards(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards?page=0&size=3", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);
    }


    @Test
    void shouldReturnASortedPageOfCashCards(){
        ResponseEntity<String>  response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards?page=0&size=3&sort=amount,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(1425.00);

    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(7);


        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1425.00,150.00,150.00,134.00,123.45,50.00,1.00);


        //for size and pageNumber comes from Spring, so are setted up for 20 and 0 by default

    }


    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("BAD-USER", "123").getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);


        response = restTemplate.withBasicAuth("sarah1", "BAD-PASSWORD").getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void shouldRejectUsersWhoAreNotCardOwners(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("hank-owns-no-cards","qrs456").getForEntity("/cashcards/99",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn(){
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1","123").getForEntity("/cashcards/106", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCashCard(){
        CashCard cashCardUpdate = new CashCard( 19.99 , null);

        // .exchange needs a Http entity
        HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);


        //putForEntity does not exist
        ResponseEntity<Void>  response  = restTemplate.withBasicAuth("sarah1", "123").exchange("/cashcards/99", HttpMethod.PUT,request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);



        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "123").getForEntity("/cashcards/99", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());


        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);


        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(19.99);


    }



    @Test
    void shouldNotUpdateACashCardThatDoesNotExist(){
        CashCard unKnownCard = new CashCard(19.99, null);

        HttpEntity<CashCard>  request = new HttpEntity<>(unKnownCard);



        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "123").exchange("/cashcards/99999999",HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DirtiesContext
    void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse(){
        CashCard kumarsCashCard = new CashCard(333.33,null);
        HttpEntity<CashCard> request = new HttpEntity<>(kumarsCashCard);


        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "123").exchange("/cashcards/106", HttpMethod.PUT, request, Void.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard(){

        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "123").exchange("/cashcards/99", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //We don't use restTemplate.delete() because this method return a void, he need the Response.Entity for getting the status code.

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "123").getForEntity("/cashcards/99", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void shouldNotDeleteACashCardThatDoesNotExist(){
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1" , "123").exchange("/cashcards/9999999", HttpMethod.DELETE,null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn(){
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "123").exchange("/cashcards/106",HttpMethod.DELETE,null,Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("kumar2", "123").getForEntity("/cashcards/106",String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


}

