package org.acme.service.word.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResponseWordDTO {
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
}
