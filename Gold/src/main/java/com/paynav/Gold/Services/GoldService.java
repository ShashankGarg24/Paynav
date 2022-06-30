package com.paynav.Gold.Services;

import com.paynav.Gold.DTOs.BuyRequestDTO;
import com.paynav.Gold.DTOs.QuoteResponseDTO;
import com.paynav.Gold.DTOs.TradeType;
import com.paynav.Gold.Interfaces.GoldInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${calculationTypeBuy}")
    private String calculationTypeForBuy;

    @Value("${calculationTypeSell}")
    private String calculationTypeForSell;

    @Value("${getQuoteURL}")
    private String getQuoteURL;

    @Value("${transactionStatusURL}")
    private String transactionStatusURL;


    private QuoteResponseDTO getQuoteResponse(String goldQuoteType){

        return webClient.get()
                .uri(getQuoteURL)
                .accept(MediaType.APPLICATION_JSON)
                .header("X_GOLD_QUOTE_TYPE", goldQuoteType)
                .retrieve()
                .bodyToMono(QuoteResponseDTO.class).block();
    }

    private String getTransactionalResponse(String userId, BuyRequestDTO requestDTO){

        return webClient.post()
                .uri(transactionStatusURL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", userId)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public ResponseEntity<?> createGoldTransactionForBuy(String goldQuoteType, String totalAmount, String userId) {

        try{

            //webClient to call getQuote
            QuoteResponseDTO quoteResponse = getQuoteResponse(goldQuoteType);

            String quoteId = quoteResponse.getQuoteId();
            double tax1 = quoteResponse.getTax1Perc()/100;
            double tax2 = quoteResponse.getTax2Perc()/100;
            double preTaxAmount1G = quoteResponse.getPreTaxAmount();
            double totalAmountNum = Double.parseDouble(totalAmount);

            //Decimal formatter to round off to 2 places
            DecimalFormat formatTo2 = new DecimalFormat("#.##");

            double totalTaxPer = tax1 + tax2;
            String tax1Amt = formatTo2.format(totalAmountNum * (1 - (1 / (1 + totalTaxPer))) * (tax1 / totalTaxPer));
            String tax2Amt = formatTo2.format(totalAmountNum * (1 - (1 / (1 + totalTaxPer))) * (tax2 / totalTaxPer));

            double totalTaxAmt = Double.parseDouble(tax1Amt) + Double.parseDouble(tax2Amt);
            double preTaxAmount = totalAmountNum - totalTaxAmt;
            double quantity = preTaxAmount/preTaxAmount1G;

            //Decimal formatter to round down to 4 places
            BigDecimal bigDecimal = new BigDecimal(quantity);
            bigDecimal = bigDecimal.setScale(4, RoundingMode.DOWN);

            String precisedQuantity = String.valueOf(bigDecimal.doubleValue());

            //Building DTO for transaction
            BuyRequestDTO requestDTO = new BuyRequestDTO(
                    calculationTypeForBuy, preTaxAmount, preTaxAmount1G, Double.parseDouble(precisedQuantity),
                    quoteId, Double.parseDouble(tax1Amt), Double.parseDouble(tax2Amt),
                    Double.parseDouble(totalAmount), TradeType.BUY.name());

            System.out.println(requestDTO.toString());

            String clientResponse = getTransactionalResponse(userId, requestDTO);

            return new ResponseEntity<>(clientResponse, HttpStatus.OK);

        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    public ResponseEntity<?> createGoldTransactionForSell(String goldQuoteType, String quantity, String userId) {
        try {

            QuoteResponseDTO quoteResponse = getQuoteResponse(goldQuoteType);

            String quoteId = quoteResponse.getQuoteId();
            double tax1 = quoteResponse.getTax1Perc() / 100;
            double tax2 = quoteResponse.getTax2Perc() / 100;
            double preTaxAmount1G = quoteResponse.getPreTaxAmount();
            double quantityNum = Double.parseDouble(quantity);

            DecimalFormat formatTo2 = new DecimalFormat("#.##");

            double preTaxAmount = preTaxAmount1G * quantityNum;
            String precisedPreTaxAmount = formatTo2.format(preTaxAmount);

            String tax1Amt = formatTo2.format(preTaxAmount * tax1);
            String tax2Amt = formatTo2.format(preTaxAmount * tax2);

            double totalTaxableVale = Double.parseDouble(tax1Amt) + Double.parseDouble(tax2Amt);

            String totalAmount = formatTo2.format(preTaxAmount + totalTaxableVale);

            BuyRequestDTO requestDTO = new BuyRequestDTO(
                    calculationTypeForSell, Double.parseDouble(precisedPreTaxAmount), preTaxAmount1G, quantityNum,
                    quoteId, Double.parseDouble(tax1Amt), Double.parseDouble(tax2Amt),
                    Double.parseDouble(totalAmount), TradeType.SELL.name());

            System.out.println(requestDTO.toString());

            String clientResponse = getTransactionalResponse(userId, requestDTO);

            return new ResponseEntity<>(clientResponse, HttpStatus.OK);

        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }




    }
