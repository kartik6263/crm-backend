package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sticky_notes")
public class StickyNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String userEmail;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String bgColor;
    private String textColor;
    private String fontSize;

    private Boolean boldEnabled = false;
    private Boolean italicEnabled = false;
    private Boolean underlineEnabled = false;
    private Boolean strikeEnabled = false;

    private String reminderDate;
    private String reminderTime;


    private Boolean minimized = false;
    private String createdDate;
    private String updatedDate;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }

    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }

    public Boolean getBoldEnabled() { return boldEnabled; }
    public void setBoldEnabled(Boolean boldEnabled) { this.boldEnabled = boldEnabled; }

    public Boolean getItalicEnabled() { return italicEnabled; }
    public void setItalicEnabled(Boolean italicEnabled) { this.italicEnabled = italicEnabled; }

    public Boolean getUnderlineEnabled() { return underlineEnabled; }
    public void setUnderlineEnabled(Boolean underlineEnabled) { this.underlineEnabled = underlineEnabled; }

    public Boolean getStrikeEnabled() { return strikeEnabled; }
    public void setStrikeEnabled(Boolean strikeEnabled) { this.strikeEnabled = strikeEnabled; }

    public String getReminderDate() { return reminderDate; }
    public void setReminderDate(String reminderDate) { this.reminderDate = reminderDate; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }


    public Boolean getMinimized() { return minimized; }
    public void setMinimized(Boolean minimized) { this.minimized = minimized; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
}