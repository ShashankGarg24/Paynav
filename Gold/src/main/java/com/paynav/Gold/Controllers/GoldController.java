package com.paynav.Gold.Controllers;


import com.paynav.Gold.Services.GoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GoldController {

    @Autowired
    private GoldService goldService;

    @PostMapping("/getStatus")
    public ResponseEntity<?> getTransactionStatus(@RequestHeader("X_GOLD_QUOTE_TYPE") String goldQuoteType,
                                                  @RequestHeader("totalAmount") String totalAmount,
                                                  @RequestHeader("quantity") String quantity,
                                                  @RequestHeader("userId") String userId){

        if(goldQuoteType.equals("BUY")){
            return goldService.createGoldTransactionForBuy(goldQuoteType, totalAmount, userId);
        }
        else{
            return goldService.createGoldTransactionForSell(goldQuoteType, quantity, userId);
        }
    }

}
