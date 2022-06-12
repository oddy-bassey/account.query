package com.revoltcode.account.query.domain.model;

import com.revoltcode.account.common.dto.AccountType;
import com.revoltcode.cqrs.core.domain.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class BankAccount extends BaseEntity {

    @Id
    private String id;
    private String customerId;
    private Date createdDate;
    private AccountType accountType;
    private double balance;
}



