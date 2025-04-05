package org.acme.service.word;

import jakarta.persistence.*;
import lombok.*;
import org.acme.base.entity.BaseEntity;
import org.acme.service.account.Account;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word")
public class Word extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String pronounce;
    private String meaning;
    private LocalDate studiedDay;
    private LocalDate repeatDay1;
    private String statusDay1;
    private LocalDate repeatDay2;
    private String statusDay2;
    private LocalDate repeatDay3;
    private String statusDay3;
    private String story;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;
}
