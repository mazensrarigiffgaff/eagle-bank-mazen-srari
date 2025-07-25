package com.eaglebank.eagle_bank_api.model;

import com.example.project.model.BankAccountResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(name = "sort_code")
    @Enumerated(EnumType.STRING)
    private BankAccountResponse.SortCodeEnum sortCode;

    @Column(name = "name")
    private String name;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private BankAccountResponse.AccountTypeEnum accountType;

    @Column(name = "balance")
    private Double balance = 0.0;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private BankAccountResponse.CurrencyEnum currency;

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private OffsetDateTime createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private OffsetDateTime updatedTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
