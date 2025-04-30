package com.example.cashcard.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository  extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard,Long> {



    // there is defined methods with auth, thanks Spring Security!!
    CashCard findByIdAndOwner(Long id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);


    boolean existsByIdAndOwner(Long id, String owner);
}
