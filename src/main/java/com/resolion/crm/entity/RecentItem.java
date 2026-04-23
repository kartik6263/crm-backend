package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crm_recent_items")
public class RecentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String userEmail;
    private String itemType;
    private String itemName;
    private String itemLink;
    private String openedDate;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemLink() { return itemLink; }
    public void setItemLink(String itemLink) { this.itemLink = itemLink; }

    public String getOpenedDate() { return openedDate; }
    public void setOpenedDate(String openedDate) { this.openedDate = openedDate; }
}
