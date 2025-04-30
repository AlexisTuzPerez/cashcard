package com.example.cashcard.cashcard;


import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
class CashCardController {


    
    private final CashCardRepository cashCardRepository;


    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable,Principal principal){

        Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC,"amount"))
                )
        );



        return ResponseEntity.ok(page.getContent());
    }


    //With principal you can use the basic auth credentials
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {

        CashCard cashCard = findCashCard(requestedId, principal);

        if (cashCard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cashCard);



    }


    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard,  UriComponentsBuilder ucb, Principal principal){

        CashCard cardWithOwner = new CashCard(cashCard.getAmount(), principal.getName());
        CashCard newCashCard = cashCardRepository.save(cardWithOwner);

        URI locationOfNewCashCard = ucb.path("/cashcards/{id}").buildAndExpand(newCashCard.getId()).toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }


    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long requestedId , @RequestBody CashCard cashCardUpdate, Principal principal){

        CashCard cashCard = findCashCard(requestedId, principal);



        if(cashCard == null){
            return ResponseEntity.notFound().build();
        }

        CashCard updatedCashCard = new CashCard( cashCard.getId(), cashCardUpdate.getAmount(), principal.getName());

        cashCardRepository.save(updatedCashCard);

        return ResponseEntity.noContent().build();



    }


    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long requestedId, Principal principal ){


        if( cashCardRepository.existsByIdAndOwner(requestedId, principal.getName())){
            cashCardRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();



    }





    private CashCard findCashCard(Long requestedId, Principal principal){

        CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());

        return  cashCard;
    }










}