package org.acme.service.word.dto;

import lombok.*;
import org.acme.shared.enums.StatusEnum;
import org.acme.shared.validation.ValueOfEnum;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestPatchWordDTO {
    private String name;

    private String pronounce;

    private String meaning;

    private LocalDate studiedDay;

    private LocalDate repeatDay1;

    @ValueOfEnum(enumClass = StatusEnum.class)
    private StatusEnum statusDay1;

    private LocalDate repeatDay2;

    @ValueOfEnum(enumClass = StatusEnum.class)
    private StatusEnum statusDay2;

    private LocalDate repeatDay3;

    @ValueOfEnum(enumClass = StatusEnum.class)
    private StatusEnum statusDay3;

    private String story;

    private String accountId;
}
