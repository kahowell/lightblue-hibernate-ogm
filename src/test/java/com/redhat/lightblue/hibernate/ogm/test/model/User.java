package com.redhat.lightblue.hibernate.ogm.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user/1.0.0")
public class User {
    @Id
    @Column(name = "_id")
    private String userId;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "login")
    private String login;

    @Column(name = "sites#")
    private Integer numberSites;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getNumberSites() {
        return numberSites;
    }

    public void setNumberSites(Integer numberSites) {
        this.numberSites = numberSites;
    }

}
