package io.cordova.lexuncompany.bean;

import java.io.Serializable;

import io.cordova.lexuncompany.units.BaseUnits;

/**
 * Created by JasonYao on 2018/3/20.
 */

public class CardItem implements Serializable {
    private String id;

    private String ID;
    private String CardID;
    private String Poster;
    private String Logo;
    private String Title;
    private String SubTitle;
    private String QRCodeImg;
    private String EndTime;
    private String AreaName;

    private String CardUrl;
    private String PosterUrl;
    private String LogoUrl;
    private String QRCodeUrl;
    private String ServiceTypeID;
    private String RecommendNote;
    private String CardType = "";
    private String FocusStatus;
    private String AuditStatus;
    private String DistrictName;

    public CardItem(String id) {
        this.id = id;
    }

    public CardItem() {
    }

    @Override
    public String toString() {
        return "CardItem{" +
                "id='" + id + '\'' +
                ", ID='" + ID + '\'' +
                ", CardID='" + CardID + '\'' +
                ", Poster='" + Poster + '\'' +
                ", Logo='" + Logo + '\'' +
                ", Title='" + Title + '\'' +
                ", SubTitle='" + SubTitle + '\'' +
                ", QRCodeImg='" + QRCodeImg + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", AreaName='" + AreaName + '\'' +
                ", CardUrl='" + CardUrl + '\'' +
                ", PosterUrl='" + PosterUrl + '\'' +
                ", LogoUrl='" + LogoUrl + '\'' +
                ", QRCodeUrl='" + QRCodeUrl + '\'' +
                ", ServiceTypeID='" + ServiceTypeID + '\'' +
                ", RecommendNote='" + RecommendNote + '\'' +
                ", CardType='" + CardType + '\'' +
                ", FocusStatus='" + FocusStatus + '\'' +
                ", AuditStatus='" + AuditStatus + '\'' +
                ", DistrictName='" + DistrictName + '\'' +
                '}';
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }

    public String getAuditStatus() {
        return AuditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        AuditStatus = auditStatus;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getFocusStatus() {
        return FocusStatus;
    }

    public void setFocusStatus(String focusStatus) {
        FocusStatus = focusStatus;
    }

    public String getRecommendNote() {
        return RecommendNote;
    }

    public void setRecommendNote(String recommendNote) {
        RecommendNote = recommendNote;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getServiceTypeID() {
        return ServiceTypeID;
    }

    public void setServiceTypeID(String serviceTypeID) {
        ServiceTypeID = serviceTypeID;
    }

    public String getPosterUrl() {
        return PosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        PosterUrl = posterUrl;
    }

    public String getLogoUrl() {
        return LogoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        LogoUrl = logoUrl;
    }

    public String getQRCodeUrl() {
        return QRCodeUrl;
    }

    public void setQRCodeUrl(String QRCodeUrl) {
        this.QRCodeUrl = QRCodeUrl;
    }

    public String getCardUrl() {
        return CardUrl + "?random=" + BaseUnits.getInstance().getSerilNumByLength(15);
    }

    public void setCardUrl(String cardUrl) {
        CardUrl = cardUrl;
    }

    public String getCardID() {
        return CardID;
    }

    public void setCardID(String cardID) {
        CardID = cardID;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public String getQRCodeImg() {
        return QRCodeImg;
    }

    public void setQRCodeImg(String QRCodeImg) {
        this.QRCodeImg = QRCodeImg;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
