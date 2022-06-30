package com.paynav.Gold.DTOs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BuyRequestDTO {

    private String calculationType;
    private double preTaxAmount;
    private double preTaxAmount1G;
    private double quantity;
    private String quoteId;
    private double tax1Amt;
    private double tax2Amt;
    private double totalAmount;
    private String tradeType;

    public BuyRequestDTO(String calculationType, double preTaxAmount, double preTaxAmount1G, double quantity, String quoteId, double tax1Amt, double tax2Amt, double totalAmount, String tradeType) {
        this.calculationType = calculationType;
        this.preTaxAmount = preTaxAmount;
        this.preTaxAmount1G = preTaxAmount1G;
        this.quantity = quantity;
        this.quoteId = quoteId;
        this.tax1Amt = tax1Amt;
        this.tax2Amt = tax2Amt;
        this.totalAmount = totalAmount;
        this.tradeType = tradeType;
    }


    @Override
    public String toString() {
        return "BuyRequestDTO{" +
                "calculationType='" + calculationType + '\'' +
                ", preTaxAmount=" + preTaxAmount +
                ", preTaxAmount1G=" + preTaxAmount1G +
                ", quantity=" + quantity +
                ", quoteId='" + quoteId + '\'' +
                ", tax1Amt=" + tax1Amt +
                ", tax2Amt=" + tax2Amt +
                ", totalAmount=" + totalAmount +
                ", tradeType='" + tradeType + '\'' +
                '}';
    }
}
