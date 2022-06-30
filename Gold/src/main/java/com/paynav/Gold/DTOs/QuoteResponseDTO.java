package com.paynav.Gold.DTOs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class QuoteResponseDTO {

    private String quoteId;
    private double totalAmount;
    private double preTaxAmount;
    private double tax1Perc;
    private double tax2Perc;


    public String getQuoteId() {
        return quoteId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getPreTaxAmount() {
        return preTaxAmount;
    }

    public double getTax1Perc() {
        return tax1Perc;
    }

    public double getTax2Perc() {
        return tax2Perc;
    }
}
