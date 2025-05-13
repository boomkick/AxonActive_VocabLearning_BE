package org.acme.service.account;

import jakarta.persistence.*;
import lombok.*;
import org.acme.base.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;
    private String role;
    private String avatar;
    private String theme;
}
