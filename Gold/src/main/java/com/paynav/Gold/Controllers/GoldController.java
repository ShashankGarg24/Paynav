package com.paynav.Gold.Controllers;


import com.paynav.Gold.Services.GoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GoldController {

    @Autowired
    private GoldService goldService;

    @PostMapping("/getStatus")
    public ResponseEntity<?> getTransactionStatus(@RequestHeader("X_GOLD_QUOTE_TYPE") String goldQuoteType,
                                                  @RequestHeader("totalAmount") String totalAmount,
                                                  @RequestHeader("userId") String userId){

        return goldService.createGoldTransaction(goldQuoteType, totalAmount, userId);
    }

}
