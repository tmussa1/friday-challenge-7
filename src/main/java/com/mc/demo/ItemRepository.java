package com.mc.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long>{
    //Iterable<Item> findAllTopTenByPrice(Iterable <Item> items);
    Iterable<Item> findAllByDescriptionContainingIgnoreCase(String searchTerm);
}
