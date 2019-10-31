package io.cordova.lexuncompany.bean;

/**
 * Created by JasonYao on 2018/3/5.
 */

public class Card {
    private String CardId;
    private String CardArea;
    private String CardIntroduction;
    private String CardBg;

    public Card(){}

    public Card(String cardId, String cardArea, String cardIntroduction, String cardBg) {
        CardId = cardId;
        CardArea = cardArea;
        CardIntroduction = cardIntroduction;
        CardBg = cardBg;
    }

    @Override
    public String toString() {
        return "Card{" +
                "CardId='" + CardId + '\'' +
                ", CardArea='" + CardArea + '\'' +
                ", CardIntroduction='" + CardIntroduction + '\'' +
                ", CardBg='" + CardBg + '\'' +
                '}';
    }

    public String getCardId() {
        return CardId;
    }

    public void setCardId(String cardId) {
        CardId = cardId;
    }

    public String getCardArea() {
        return CardArea;
    }

    public void setCardArea(String cardArea) {
        CardArea = cardArea;
    }

    public String getCardIntroduction() {
        return CardIntroduction;
    }

    public void setCardIntroduction(String cardIntroduction) {
        CardIntroduction = cardIntroduction;
    }

    public String getCardBg() {
        return CardBg;
    }

    public void setCardBg(String cardBg) {
        CardBg = cardBg;
    }
}
