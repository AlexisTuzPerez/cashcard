package com.example.cashcard.cashcard;


import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CashCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String owner;

    public CashCard() {}

    public CashCard(Long id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    public CashCard(Double amount){
        this.amount = amount;
    }

    public CashCard(Long id, Double amount, String owner){
        this.id = id;
        this.amount = amount;
        this.owner = owner;
    }
    public CashCard(Double amount,String owner){
        this.amount= amount;
        this.owner = owner;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getOwner(){
        return owner;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CashCard cashCard = (CashCard) o;
        return Objects.equals(id, cashCard.id) && Objects.equals(amount, cashCard.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }
}
