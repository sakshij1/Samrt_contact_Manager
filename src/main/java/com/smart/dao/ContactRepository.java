package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
    // Pagination query for fetching contacts by user
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
    
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:keyword% AND c.user = :user")
    public List<Contact> findByNameContainingAndUser(@Param("keyword") String keyword, @Param("user") User user);

}
