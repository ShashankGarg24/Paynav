package com.paynav.Gold.Interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface GoldInterface {

    ResponseEntity<?> createGoldTransaction(String goldQuoteType, String totalAmount, String userId);
}
