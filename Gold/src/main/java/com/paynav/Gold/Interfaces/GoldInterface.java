package com.paynav.Gold.Interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface GoldInterface {

    ResponseEntity<?> createGoldTransactionForBuy(String goldQuoteType, String totalAmount, String userId);
    ResponseEntity<?> createGoldTransactionForSell(String goldQuoteType, String quantity, String userId);
}
