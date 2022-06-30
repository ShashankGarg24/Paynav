package com.paynav.Gold.Services;

import com.paynav.Gold.DTOs.BuyRequestDTO;
import com.paynav.Gold.DTOs.QuoteResponseDTO;
import com.paynav.Gold.DTOs.TradeType;
import com.paynav.Gold.Interfaces.GoldInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class GoldService implements GoldInterface {

    @Autowired
    private WebClient webClient;

    private final String calculationType = "A";
    private final String getQuoteURL = "https://dev.thor.paynav.in/goldController/getQuote";
    private final String transactionStatusURL = "https://dev.thor.paynav.in/goldController/createGoldTransactionServerCalculation";

    public ResponseEntity<?> createGoldTransaction(String goldQuoteType, String totalAmount, String userId) {

        try{

            //webClient to call getQuote
            QuoteResponseDTO quoteResponseResponse = webClient.get()
                    .uri(getQuoteURL)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X_GOLD_QUOTE_TYPE", goldQuoteType)
                    .retrieve()
                    .bodyToMono(QuoteResponseDTO.class).block();

            String quoteId = quoteResponseResponse.getQuoteId();
            double tax1Perc = quoteResponseResponse.getTax1Perc()/100;
            double tax2Perc = quoteResponseResponse.getTax2Perc()/100;
            double preTaxAmount1G = quoteResponseResponse.getPreTaxAmount();
            double totalAmountNum = Double.parseDouble(totalAmount);

            //Decimal formatter to round off to 2 places
            DecimalFormat formatTo2 = new DecimalFormat("#.##");

            double totalTaxPer = tax1Perc + tax2Perc;
            String tax1Amt = formatTo2.format(totalAmountNum * (1 - (1 / (1 + totalTaxPer))) * (tax1Perc / totalTaxPer));
            String tax2Amt = formatTo2.format(totalAmountNum * (1 - (1 / (1 + totalTaxPer))) * (tax2Perc / totalTaxPer));

            double totalTaxAmt = Double.parseDouble(tax1Amt) + Double.parseDouble(tax2Amt);
            double preTaxAmount = totalAmountNum - totalTaxAmt;

            //Decimal formatter to round down to 4 places
            double quantity = preTaxAmount/preTaxAmount1G;
            BigDecimal bigDecimal = new BigDecimal(quantity);
            bigDecimal = bigDecimal.setScale(4, RoundingMode.DOWN);

            String precisedQuantity = String.valueOf(bigDecimal.doubleValue());

            //Building DTO for transaction
            BuyRequestDTO requestDTO = new BuyRequestDTO(
                    calculationType, preTaxAmount, preTaxAmount1G, Double.parseDouble(precisedQuantity),
                    quoteId, Double.parseDouble(tax1Amt), Double.parseDouble(tax2Amt),
                    Double.parseDouble(totalAmount), TradeType.BUY.name());

            System.out.println(requestDTO.toString());

            //WebClient to fetch transaction status
            String clientResponse = webClient.post()
                    .uri(transactionStatusURL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-USER-ID", userId)
                    .bodyValue(requestDTO)
                    .retrieve()
                    .bodyToMono(String.class).block();

            return new ResponseEntity<>(clientResponse, HttpStatus.OK);

        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
